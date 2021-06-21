package com.solactive.service.instrument.tick.stats.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import com.solactive.service.instrument.tick.stats.model.InstrumentTick;
import com.solactive.service.instrument.tick.stats.model.InstrumentWindowMaxStats;
import com.solactive.service.instrument.tick.stats.utilities.InstrumentTickServiceUtil;

public class SlidingMaxTask implements Runnable {

	private InstrumentWindowMaxStats instrumentWindowMaxStats;

	private Long currentTime;

	private InstrumentTick tick;

	private Integer windowSizeSec;

	public SlidingMaxTask(InstrumentWindowMaxStats instrumentWindowMaxStats, Long currentTime, InstrumentTick tick,
			Integer windowSizeSec) {
		this.instrumentWindowMaxStats = instrumentWindowMaxStats;
		this.currentTime = currentTime;
		this.tick = tick;
		this.windowSizeSec = windowSizeSec;
	}

	@Override
	public void run() {
		this.updateStats();
	}

	public void updateStats() {
		Long tickTimestampSeconds = InstrumentTickServiceUtil.trucateMillis(this.tick.getTimestamp());
		Long currentTimeSec = InstrumentTickServiceUtil.trucateMillis(currentTime);
		Long cutoffTimeSec = InstrumentTickServiceUtil.timeMinusSeconds(currentTimeSec, this.windowSizeSec - 1);

		ConcurrentHashMap<String, ConcurrentSkipListMap<Long, Double>> tickMap = this.instrumentWindowMaxStats
				.getTickMap();

		String instr = this.tick.getInstrumentCode();

		ConcurrentSkipListMap<Long, Double> tickMaxMap = tickMap.putIfAbsent(instr,
				new ConcurrentSkipListMap<Long, Double>());
		if (tickMaxMap == null) {
			tickMaxMap = tickMap.get(instr);
		}
		Double previousVal = tickMaxMap.putIfAbsent(tickTimestampSeconds, this.tick.getPrice());

		if (previousVal == null) {
			// Time stamp key was not present in the map
			this.setInstrMaxPriceLastWindow(this.tick.getInstrumentCode(), this.tick.getPrice());
			this.removeOldPricesAndUpdateMax(tickMaxMap, cutoffTimeSec);
		} else {
			// Time stamp key was already present in the map
			// Ignoring the tick entry if it is already present for the tick second
		}
	}

	private void setInstrMaxPriceLastWindow(String instrument, Double tickPrice) {
		// Atomic operation and needs no synchronization and hence avoiding overhead of
		// synchronization, resulting in faster processing
		this.instrumentWindowMaxStats.getInstrumentMaxLastWindow().compute(instrument,
				(instr, maxPrice) -> (maxPrice == null) ? tickPrice : Math.max(maxPrice, tickPrice));
	}

	private void removeOldPricesAndUpdateMax(ConcurrentSkipListMap<Long, Double> tickMaxMap, Long cutoffTimeSec) {
		synchronized (tickMaxMap) {
			final Double maxValRemoved = tickMaxMap.headMap(cutoffTimeSec).values().stream().reduce(Double.MIN_VALUE,
					Double::max);
			if (this.instrumentWindowMaxStats.getInstrumentMaxLastWindow()
					.get(this.tick.getInstrumentCode()) == maxValRemoved.doubleValue()) {
				tickMaxMap.headMap(cutoffTimeSec).clear();
				final Double maxValCurrentWindow = tickMaxMap.values().stream().reduce(Double.MIN_VALUE, Double::max);

				this.instrumentWindowMaxStats.getInstrumentMaxLastWindow().compute(this.tick.getInstrumentCode(),
						(instr, maxPrice) -> maxPrice.equals(maxValRemoved) ? maxValCurrentWindow : maxPrice);
			}
		}
	}

}
