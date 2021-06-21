package com.solactive.service.instrument.tickerstats;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.math3.util.Precision;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import com.solactive.service.instrument.tick.stats.model.AllInstrumentWindowStats;
import com.solactive.service.instrument.tick.stats.model.InstrumentTick;
import com.solactive.service.instrument.tick.stats.service.AllInstrumentsSlidingStatisticCalc;

@SpringBootTest
class TestAllInstrumentSlidingStatisticCalc {

	@BeforeTestClass
	public void before() {

	}

	@Test
	void testUpdateStats() {

		Integer windowSizeSec = 3;
		AllInstrumentWindowStats allInstrumentWindowSumStats = new AllInstrumentWindowStats();

		// 2021/6/14 6:17:15
		// Tick time and current time are same
		// Simulate tick 15th second
		DateTime tickTime = new DateTime(2021, 6, 14, 6, 17, 15);
		DateTime currentTime = new DateTime(2021, 6, 14, 6, 17, 15);
		InstrumentTick tick = new InstrumentTick("instr1", 12.5, tickTime.getMillis());
		AllInstrumentsSlidingStatisticCalc allInstrSlidingStatisticsCalc = new AllInstrumentsSlidingStatisticCalc(allInstrumentWindowSumStats, currentTime.getMillis(), tick,
				windowSizeSec);
		allInstrSlidingStatisticsCalc.updateStats();
		assertEquals(allInstrumentWindowSumStats.getCountLastWindow().doubleValue(), 1);
		assertEquals(allInstrumentWindowSumStats.getMinLastWindow().doubleValue(), 12.5);
		assertEquals(allInstrumentWindowSumStats.getMaxLastWindow().doubleValue(), 12.5);
		assertEquals(allInstrumentWindowSumStats.getAvgLastWindow().doubleValue(), 12.5);

		// Simulate tick 16th second
		tickTime = new DateTime(2021, 6, 14, 6, 17, 16);
		currentTime = new DateTime(2021, 6, 14, 6, 17, 16);
		tick = new InstrumentTick("instr1", 15.5, tickTime.getMillis());
		allInstrSlidingStatisticsCalc = new AllInstrumentsSlidingStatisticCalc(allInstrumentWindowSumStats, currentTime.getMillis(), tick,
				windowSizeSec);
		allInstrSlidingStatisticsCalc.updateStats();
		assertEquals(allInstrumentWindowSumStats.getCountLastWindow().doubleValue(), 2);
		assertEquals(allInstrumentWindowSumStats.getMinLastWindow().doubleValue(), 12.5);
		assertEquals(allInstrumentWindowSumStats.getMaxLastWindow().doubleValue(), 15.5);
		assertEquals(allInstrumentWindowSumStats.getAvgLastWindow().doubleValue(), 14);

		// Simulate tick 17th second
		tickTime = new DateTime(2021, 6, 14, 6, 17, 17);
		currentTime = new DateTime(2021, 6, 14, 6, 17, 17);
		tick = new InstrumentTick("instr2", 12.0, tickTime.getMillis());
		allInstrSlidingStatisticsCalc = new AllInstrumentsSlidingStatisticCalc(allInstrumentWindowSumStats, currentTime.getMillis(), tick,
				windowSizeSec);
		allInstrSlidingStatisticsCalc.updateStats();
		assertEquals(allInstrumentWindowSumStats.getCountLastWindow().doubleValue(), 3);
		assertEquals(allInstrumentWindowSumStats.getMinLastWindow().doubleValue(), 12);
		assertEquals(allInstrumentWindowSumStats.getMaxLastWindow().doubleValue(), 15.5);
		assertEquals(Precision.round(allInstrumentWindowSumStats.getAvgLastWindow().doubleValue(),2), 13.33);

		// Simulate tick 18th second
		tickTime = new DateTime(2021, 6, 14, 6, 17, 18);
		currentTime = new DateTime(2021, 6, 14, 6, 17, 18);
		tick = new InstrumentTick("instr1", 8.5, tickTime.getMillis());
		allInstrSlidingStatisticsCalc = new AllInstrumentsSlidingStatisticCalc(allInstrumentWindowSumStats, currentTime.getMillis(), tick,
				windowSizeSec);
		allInstrSlidingStatisticsCalc.updateStats();
		assertEquals(allInstrumentWindowSumStats.getCountLastWindow().doubleValue(), 3);
		assertEquals(allInstrumentWindowSumStats.getMinLastWindow().doubleValue(), 8.5);
		assertEquals(allInstrumentWindowSumStats.getMaxLastWindow().doubleValue(), 15.5);
		assertEquals(allInstrumentWindowSumStats.getAvgLastWindow().doubleValue(), 12);

		// Tick not received at 19th second
		// Simulate tick at 20th second
		tickTime = new DateTime(2021, 6, 14, 6, 17, 20);
		currentTime = new DateTime(2021, 6, 14, 6, 17, 20);
		tick = new InstrumentTick("instr3", 13.5, tickTime.getMillis());
		allInstrSlidingStatisticsCalc = new AllInstrumentsSlidingStatisticCalc(allInstrumentWindowSumStats, currentTime.getMillis(), tick,
				windowSizeSec);
		allInstrSlidingStatisticsCalc.updateStats();
		assertEquals(allInstrumentWindowSumStats.getCountLastWindow().doubleValue(), 2);
		assertEquals(allInstrumentWindowSumStats.getMinLastWindow().doubleValue(), 8.5);
		assertEquals(allInstrumentWindowSumStats.getMaxLastWindow().doubleValue(), 13.5);
		assertEquals(allInstrumentWindowSumStats.getAvgLastWindow().doubleValue(), 11);

		// Simulate missed tick previously at 19th second at current time 21st second
		tickTime = new DateTime(2021, 6, 14, 6, 17, 19);
		currentTime = new DateTime(2021, 6, 14, 6, 17, 21);
		tick = new InstrumentTick("instr1", 60.5, tickTime.getMillis());
		allInstrSlidingStatisticsCalc = new AllInstrumentsSlidingStatisticCalc(allInstrumentWindowSumStats, currentTime.getMillis(), tick,
				windowSizeSec);
		allInstrSlidingStatisticsCalc.updateStats();
		assertEquals(allInstrumentWindowSumStats.getCountLastWindow().doubleValue(), 2);
		assertEquals(allInstrumentWindowSumStats.getMinLastWindow().doubleValue(), 13.5);
		assertEquals(allInstrumentWindowSumStats.getMaxLastWindow().doubleValue(), 60.5);
		assertEquals(allInstrumentWindowSumStats.getAvgLastWindow().doubleValue(), 37);

		// Simulate tick for the current time also i.e. 21st second
		tickTime = new DateTime(2021, 6, 14, 6, 17, 21);
		currentTime = new DateTime(2021, 6, 14, 6, 17, 21);
		tick = new InstrumentTick("instr6", 6.5, tickTime.getMillis());
		allInstrSlidingStatisticsCalc = new AllInstrumentsSlidingStatisticCalc(allInstrumentWindowSumStats, currentTime.getMillis(), tick,
				windowSizeSec);
		allInstrSlidingStatisticsCalc.updateStats();
		assertEquals(allInstrumentWindowSumStats.getCountLastWindow().doubleValue(), 3);
		assertEquals(allInstrumentWindowSumStats.getMinLastWindow().doubleValue(), 6.5);
		assertEquals(allInstrumentWindowSumStats.getMaxLastWindow().doubleValue(), 60.5);
		assertEquals(Precision.round(allInstrumentWindowSumStats.getAvgLastWindow().doubleValue(),2), 26.83);
		
		// Simulate tick at 22nd second
		tickTime = new DateTime(2021, 6, 14, 6, 17, 22);
		currentTime = new DateTime(2021, 6, 14, 6, 17, 22);
		tick = new InstrumentTick("instr1", 23.2, tickTime.getMillis());
		allInstrSlidingStatisticsCalc = new AllInstrumentsSlidingStatisticCalc(allInstrumentWindowSumStats, currentTime.getMillis(), tick,
				windowSizeSec);
		allInstrSlidingStatisticsCalc.updateStats();
		assertEquals(allInstrumentWindowSumStats.getCountLastWindow().doubleValue(), 3);
		assertEquals(allInstrumentWindowSumStats.getMinLastWindow().doubleValue(), 6.5);
		assertEquals(allInstrumentWindowSumStats.getMaxLastWindow().doubleValue(), 23.2);
		assertEquals(allInstrumentWindowSumStats.getAvgLastWindow().doubleValue(), 14.4);

		// Simulate tick at 23rd second
		tickTime = new DateTime(2021, 6, 14, 6, 17, 23);
		currentTime = new DateTime(2021, 6, 14, 6, 17, 23);
		tick = new InstrumentTick("instr1", 12.9, tickTime.getMillis());
		allInstrSlidingStatisticsCalc = new AllInstrumentsSlidingStatisticCalc(allInstrumentWindowSumStats, currentTime.getMillis(), tick,
				windowSizeSec);
		allInstrSlidingStatisticsCalc.updateStats();
		assertEquals(allInstrumentWindowSumStats.getCountLastWindow().doubleValue(), 3);
		assertEquals(allInstrumentWindowSumStats.getMinLastWindow().doubleValue(), 6.5);
		assertEquals(allInstrumentWindowSumStats.getMaxLastWindow().doubleValue(), 23.2);
		assertEquals(Precision.round(allInstrumentWindowSumStats.getAvgLastWindow().doubleValue(),2), 14.20);

		// Simulate tick at 24th second
		tickTime = new DateTime(2021, 6, 14, 6, 17, 24);
		currentTime = new DateTime(2021, 6, 14, 6, 17, 24);
		tick = new InstrumentTick("instr1", 26.4, tickTime.getMillis());
		allInstrSlidingStatisticsCalc = new AllInstrumentsSlidingStatisticCalc(allInstrumentWindowSumStats, currentTime.getMillis(), tick,
				windowSizeSec);
		allInstrSlidingStatisticsCalc.updateStats();
		assertEquals(allInstrumentWindowSumStats.getCountLastWindow().doubleValue(), 3);
		assertEquals(allInstrumentWindowSumStats.getMinLastWindow().doubleValue(), 12.9);
		assertEquals(allInstrumentWindowSumStats.getMaxLastWindow().doubleValue(), 26.4);
		assertEquals(Precision.round(allInstrumentWindowSumStats.getAvgLastWindow().doubleValue(),2), 20.83);

	}

	@Configuration
	static class config {

	}
}
