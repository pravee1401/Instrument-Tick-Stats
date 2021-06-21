package com.solactive.service.instrument.tick.stats.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import com.solactive.service.instrument.tick.stats.model.InstrumentTick;
import com.solactive.service.instrument.tick.stats.model.InstrumentWindowAvgStats;
import com.solactive.service.instrument.tick.stats.utilities.InstrumentTickServiceUtil;

public class SlidingAvgTask implements Runnable {

	private InstrumentWindowAvgStats instrumentWindowAvgStats;

	private Long currentTime;

	private InstrumentTick tick;

	private Integer windowSizeSec;

	public SlidingAvgTask(InstrumentWindowAvgStats instrumentWindowAvgStats, Long currentTime, InstrumentTick tick,
			Integer windowSizeSec) {
		this.instrumentWindowAvgStats = instrumentWindowAvgStats;
		this.currentTime = currentTime;
		this.tick = tick;
		this.windowSizeSec = windowSizeSec;
	}

	@Override
	public void run() {
		this.updateStats();
	}

	/**
	 * Updates the average of the instrument tick received
	 */
	public void updateStats() {
		Long tickTimestampSeconds = InstrumentTickServiceUtil.trucateMillis(this.tick.getTimestamp());
		Long currentTimeSec = InstrumentTickServiceUtil.trucateMillis(currentTime);
		Long cutoffTimeSec = InstrumentTickServiceUtil.timeMinusSeconds(currentTimeSec, this.windowSizeSec - 1);

		ConcurrentHashMap<String, ConcurrentSkipListMap<Long, Double>> tickMap = this.instrumentWindowAvgStats
				.getTickMap();

		ConcurrentSkipListMap<Long, Double> tickSumMap = tickMap.putIfAbsent(this.tick.getInstrumentCode(),
				new ConcurrentSkipListMap<Long, Double>());
		if (tickSumMap == null) {
			tickSumMap = tickMap.get(this.tick.getInstrumentCode());
		}
		Double previousVal = tickSumMap.putIfAbsent(tickTimestampSeconds, this.tick.getPrice());

		// Time stamp key was not present in the map
		if (previousVal == null) {
			
			//Remove the entries which are older than the window size w.r.t current time
			tickSumMap.headMap(cutoffTimeSec).clear();
			
			this.setInstrAvgOfLastWindow(tickSumMap);
		} else {
			// Time stamp key was already present in the map
			// Ignoring the tick entry if it is already present for the tick second
		}
	}

	private void setInstrAvgOfLastWindow(ConcurrentSkipListMap<Long, Double> tickSumMap) {

		// Atomic call to compute the average and set for the instrument of the tick
		this.instrumentWindowAvgStats.getInstrumentAvgLastWindow().compute(this.tick.getInstrumentCode(),
				(instr, windowAvg) -> (windowAvg == null) ? this.tick.getPrice()
						: tickSumMap.values().stream().mapToDouble(Double::doubleValue).average().orElse(0));
	}

}
