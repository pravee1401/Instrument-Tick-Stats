package com.solactive.service.instrument.tick.stats.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import com.google.common.util.concurrent.AtomicDouble;
import com.solactive.service.instrument.tick.stats.model.InstrumentTick;
import com.solactive.service.instrument.tick.stats.model.InstrumentWindowCountStats;
import com.solactive.service.instrument.tick.stats.utilities.InstrumentTickServiceUtil;

public class SlidingCountTask implements Runnable {

	private InstrumentWindowCountStats instrumentWindowCountStats;
	
	private Long currentTime;

	private InstrumentTick tick;

	private Integer windowSizeSec;

	public SlidingCountTask(InstrumentWindowCountStats instrumentWindowCountStats, Long currentTime,
			InstrumentTick tick, Integer windowSizeSec) {
		this.instrumentWindowCountStats = instrumentWindowCountStats;
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
		
		ConcurrentHashMap<String, ConcurrentSkipListMap<Long, Double>> tickMap = this.instrumentWindowCountStats.getTickMap();

		String instr = this.tick.getInstrumentCode();

		ConcurrentSkipListMap<Long, Double> tickCountMap = tickMap.putIfAbsent(instr,
				new ConcurrentSkipListMap<Long, Double>());
		if (tickCountMap == null) {
			tickCountMap = tickMap.get(instr);
		}
		Double previousVal = tickCountMap.putIfAbsent(tickTimestampSeconds, this.tick.getPrice());

		if (previousVal == null) {
			// Time stamp key was not present in the map
			this.addToInstrCountLastWindow(this.tick.getInstrumentCode(), 1);
			this.removeOldPrices(tickCountMap, cutoffTimeSec);
		} else {
			// Time stamp key was already present in the map
			// Ignoring the tick entry if it is already present for the tick second
		}
	}

	private void addToInstrCountLastWindow(String instrument, int valueToAdd) {
		// Atomic operation and needs no synchronization and hence avoiding overhead of
		// synchronization, resulting in faster processing
		this.instrumentWindowCountStats.getInstrumentCountLastWindow().compute(instrument,
				(instr, windowCount) -> (windowCount == null) ? valueToAdd : windowCount + valueToAdd);
	}

	private void removeOldPrices(ConcurrentSkipListMap<Long, Double> tickCountMap, Long cutoffTimeSec) {
		Integer removedCount = null;
		synchronized(tickCountMap) {
			removedCount = tickCountMap.headMap(cutoffTimeSec).size();
			if (removedCount != null && removedCount != 0) {
				tickCountMap.headMap(cutoffTimeSec).clear();
			}
		}
		this.addToInstrCountLastWindow(this.tick.getInstrumentCode(), -removedCount);
	}

}
