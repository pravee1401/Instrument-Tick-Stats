package com.solactive.service.instrument.tick.stats.service;

import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.solactive.service.instrument.tick.stats.config.InstrumentTickConfig;
import com.solactive.service.instrument.tick.stats.model.AllInstrumentWindowStats;
import com.solactive.service.instrument.tick.stats.model.InstrumentTick;
import com.solactive.service.instrument.tick.stats.model.InstrumentTickStatsResponse;
import com.solactive.service.instrument.tick.stats.model.InstrumentWindowAvgStats;
import com.solactive.service.instrument.tick.stats.model.InstrumentWindowCountStats;
import com.solactive.service.instrument.tick.stats.model.InstrumentWindowMaxStats;
import com.solactive.service.instrument.tick.stats.model.InstrumentWindowMinStats;

@Service
public class InstrumentTickStatsService {

	@Autowired
	@Qualifier("fixedThreadPool")
	private ExecutorService executorService;

	@Autowired
	private InstrumentTickConfig instrTickConf;

	@Autowired
	private InstrumentWindowCountStats instrumentWindowCountStats;

	@Autowired
	private InstrumentWindowMinStats instrumentWindowMinStats;

	@Autowired
	private InstrumentWindowMaxStats instrumentWindowMaxStats;

	@Autowired
	private InstrumentWindowAvgStats instrumentWindowAvgStats;

	@Autowired
	private AllInstrumentWindowStats allInstrumentWindowStats;

	/**
	 * This function submits the tasks for every tick received. The tasks will update the statistics on the whole 
	 * and for each instrument
	 * 
	 * @param tick
	 * @param currentTime
	 */
	public void updateInstrumentTick(InstrumentTick tick, Long currentTime) {
		executorService.submit(new AllInstrumentsSlidingStatisticCalc(allInstrumentWindowStats, currentTime, tick,
				instrTickConf.getWindowSizeSec()));

		executorService.submit(
				new SlidingCountTask(instrumentWindowCountStats, currentTime, tick, instrTickConf.getWindowSizeSec()));
		executorService.submit(
				new SlidingMinTask(instrumentWindowMinStats, currentTime, tick, instrTickConf.getWindowSizeSec()));
		executorService.submit(
				new SlidingMaxTask(instrumentWindowMaxStats, currentTime, tick, instrTickConf.getWindowSizeSec()));
		executorService.submit(
				new SlidingAvgTask(instrumentWindowAvgStats, currentTime, tick, instrTickConf.getWindowSizeSec()));
	}

	/**
	 * Retrieves tick statistics for all instruments
	 * 
	 * @return
	 */
	public InstrumentTickStatsResponse getAllInstrumentWindowStats() {
		InstrumentTickStatsResponse resp = new InstrumentTickStatsResponse();
		resp.setCount(this.allInstrumentWindowStats.getCountLastWindow().intValue());
		resp.setMax(this.allInstrumentWindowStats.getMaxLastWindow().doubleValue());
		resp.setMin(this.allInstrumentWindowStats.getMinLastWindow().doubleValue());
		resp.setAvg(this.allInstrumentWindowStats.getAvgLastWindow().doubleValue());
		return resp;
	}

	/**
	 * retrieves tick statistics for each instrument
	 * 
	 * @param instrumentCode
	 * @return
	 */
	public InstrumentTickStatsResponse getInstrumentWindowStats(String instrumentCode) {
		InstrumentTickStatsResponse resp = new InstrumentTickStatsResponse();
		resp.setCount(instrumentWindowCountStats.getInstrumentCountLastWindow().get(instrumentCode).intValue());
		resp.setMax(instrumentWindowMaxStats.getInstrumentMaxLastWindow().get(instrumentCode));
		resp.setMin(instrumentWindowMinStats.getInstrumentMinLastWindow().get(instrumentCode));
		resp.setAvg(instrumentWindowAvgStats.getInstrumentAvgLastWindow().get(instrumentCode));
		return resp;
	}

}
