package com.solactive.service.instrument.tick.stats.service;

import java.util.concurrent.ConcurrentSkipListMap;

import com.solactive.service.instrument.tick.stats.model.AllInstrumentWindowStats;
import com.solactive.service.instrument.tick.stats.model.InstrumentTick;
import com.solactive.service.instrument.tick.stats.utilities.InstrumentTickServiceUtil;

public class AllInstrumentsSlidingStatisticCalc implements Runnable {

	private AllInstrumentWindowStats allInstrumentWindowStats;

	private Long currentTime;

	private InstrumentTick tick;

	private Integer windowSizeSec;

	public AllInstrumentsSlidingStatisticCalc(AllInstrumentWindowStats allInstrumentWindowStats, Long currentTime,
			InstrumentTick tick, Integer windowSizeSec) {
		this.allInstrumentWindowStats = allInstrumentWindowStats;
		this.currentTime = currentTime;
		this.tick = tick;
		this.windowSizeSec = windowSizeSec;
	}

	@Override
	public void run() {
		this.updateStats();
	}

	/**
	 * Updates the statistics like count,min,max,avg for the ticks of all the instruments.
	 * Operations are performed mostly atomically.
	 */
	public void updateStats() {
		Long currentTimeSec = InstrumentTickServiceUtil.trucateMillis(currentTime);
		Double cutoffTimeSec = InstrumentTickServiceUtil.timeMinusSeconds(currentTimeSec, this.windowSizeSec - 1)
				.doubleValue();

		// Map which maintains time->tickPrice pairs for all instruments
		ConcurrentSkipListMap<Double, Double> tickPricesMap = this.allInstrumentWindowStats.getTickPrices();

		Double timestamp = this.tick.getTimestamp().doubleValue();

		Double previousVal = tickPricesMap.putIfAbsent(timestamp, this.tick.getPrice());
		// Loop until the key,value is successfully put in the map, increment by 0.001 if in case of collision in the map.
		while (previousVal != null) {
			timestamp += 0.001;
			//Atomic operation to put time->tickPrice in the map if its not present
			previousVal = tickPricesMap.putIfAbsent(timestamp, this.tick.getPrice());
		}

		if (previousVal == null) {
			//Remove the entries which are older than the window size w.r.t current time
			tickPricesMap.headMap(cutoffTimeSec).clear();

			/**
			 * All the operations are atomic in nature and needs no synchronization and
			 * hence performance will be better compared to its synchronized counterpart
			 */
			this.allInstrumentWindowStats.getCountLastWindow().getAndSet(tickPricesMap.size());

			this.allInstrumentWindowStats.getMaxLastWindow()
					.getAndSet(tickPricesMap.values().stream().reduce(Double.MIN_VALUE, Double::max));

			this.allInstrumentWindowStats.getMinLastWindow()
					.getAndSet(tickPricesMap.values().stream().reduce(Double.MAX_VALUE, Double::min));

			this.allInstrumentWindowStats.getAvgLastWindow()
					.getAndSet(tickPricesMap.values().stream().mapToDouble(Double::doubleValue).average().orElse(0));

		}
	}
}
