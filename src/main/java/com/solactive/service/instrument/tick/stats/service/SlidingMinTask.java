package com.solactive.service.instrument.tick.stats.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import com.solactive.service.instrument.tick.stats.model.InstrumentTick;
import com.solactive.service.instrument.tick.stats.model.InstrumentWindowMinStats;
import com.solactive.service.instrument.tick.stats.utilities.InstrumentTickServiceUtil;

public class SlidingMinTask implements Runnable {

	private InstrumentWindowMinStats instrumentWindowMinStats;

	private Long currentTime;

	private InstrumentTick tick;

	private Integer windowSizeSec;

	public SlidingMinTask(InstrumentWindowMinStats instrumentWindowMinStats, Long currentTime, InstrumentTick tick,
			Integer windowSizeSec) {
		this.instrumentWindowMinStats = instrumentWindowMinStats;
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

		ConcurrentHashMap<String, ConcurrentSkipListMap<Long, Double>> tickMap = this.instrumentWindowMinStats
				.getTickMap();

		String instr = this.tick.getInstrumentCode();

		ConcurrentSkipListMap<Long, Double> tickMinMap = tickMap.putIfAbsent(instr,
				new ConcurrentSkipListMap<Long, Double>());
		if (tickMinMap == null) {
			tickMinMap = tickMap.get(instr);
		}
		Double previousVal = tickMinMap.putIfAbsent(tickTimestampSeconds, this.tick.getPrice());

		if (previousVal == null) {
			// Time stamp key was not present in the map
			this.setInstrMinPriceLastWindow(this.tick.getInstrumentCode(), this.tick.getPrice());
			this.removeOldPricesAndUpdateMin(tickMinMap, cutoffTimeSec);
		} else {
			// Time stamp key was already present in the map
			// Ignoring the tick entry if it is already present for the tick second
		}
	}

	private void setInstrMinPriceLastWindow(String instrument, Double tickPrice) {
		// Atomic operation and needs no synchronization and hence avoiding overhead of
		// synchronization, resulting in faster processing
		this.instrumentWindowMinStats.getInstrumentMinLastWindow().compute(instrument,
				(instr, maxPrice) -> (maxPrice == null) ? tickPrice : Math.min(maxPrice, tickPrice));
	}

	private void removeOldPricesAndUpdateMin(ConcurrentSkipListMap<Long, Double> tickMinMap, Long cutoffTimeSec) {
		synchronized (tickMinMap) {
			final Double minValRemoved = tickMinMap.headMap(cutoffTimeSec).values().stream().reduce(Double.MAX_VALUE,
					Double::min);
			if (this.instrumentWindowMinStats.getInstrumentMinLastWindow()
					.get(this.tick.getInstrumentCode()) == minValRemoved.doubleValue()) {
				
				tickMinMap.headMap(cutoffTimeSec).clear();
				final Double minValCurrentWindow = tickMinMap.values().stream().reduce(Double.MAX_VALUE, Double::min);

				this.instrumentWindowMinStats.getInstrumentMinLastWindow().compute(this.tick.getInstrumentCode(),
						(instr, minPrice) -> minPrice.equals(minValRemoved) ? minValCurrentWindow : minPrice);
			}
		}
	}

}
