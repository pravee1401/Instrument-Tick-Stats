package com.solactive.service.instrument.tickerstats;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import com.solactive.service.instrument.tick.stats.model.InstrumentTick;
import com.solactive.service.instrument.tick.stats.model.InstrumentWindowMinStats;
import com.solactive.service.instrument.tick.stats.service.SlidingMinTask;

@SpringBootTest
class TestSlidingMinTest {

	@BeforeTestClass
	public void before() {

	}

	@Test
	void testUpdateStats() {

		Integer windowSizeSec = 3;
		InstrumentWindowMinStats instrumentWindowMinStats = new InstrumentWindowMinStats();

		// 2021/6/14 6:17:15
		// Tick time and current time are same
		// Simulate tick 15th second
		DateTime tickTime = new DateTime(2021, 6, 14, 6, 17, 15);
		DateTime currentTime = new DateTime(2021, 6, 14, 6, 17, 15);
		InstrumentTick tick = new InstrumentTick("instr1", 12.5, tickTime.getMillis());
		SlidingMinTask slidingMinTask = new SlidingMinTask(instrumentWindowMinStats, currentTime.getMillis(), tick,
				windowSizeSec);
		slidingMinTask.updateStats();
		assertEquals(instrumentWindowMinStats.getInstrumentMinLastWindow().get("instr1"), 12.5);

		// Simulate tick 16th second
		tickTime = new DateTime(2021, 6, 14, 6, 17, 16);
		currentTime = new DateTime(2021, 6, 14, 6, 17, 16);
		tick = new InstrumentTick("instr1", 15.5, tickTime.getMillis());
		slidingMinTask = new SlidingMinTask(instrumentWindowMinStats, currentTime.getMillis(), tick, windowSizeSec);
		slidingMinTask.updateStats();
		assertEquals(instrumentWindowMinStats.getInstrumentMinLastWindow().get("instr1"), 12.5);

		// Simulate tick 17th second
		tickTime = new DateTime(2021, 6, 14, 6, 17, 17);
		currentTime = new DateTime(2021, 6, 14, 6, 17, 17);
		tick = new InstrumentTick("instr1", 12.0, tickTime.getMillis());
		slidingMinTask = new SlidingMinTask(instrumentWindowMinStats, currentTime.getMillis(), tick, windowSizeSec);
		slidingMinTask.updateStats();
		assertEquals(instrumentWindowMinStats.getInstrumentMinLastWindow().get("instr1"), 12.0);

		// Simulate tick 18th second
		tickTime = new DateTime(2021, 6, 14, 6, 17, 18);
		currentTime = new DateTime(2021, 6, 14, 6, 17, 18);
		tick = new InstrumentTick("instr1", 8.5, tickTime.getMillis());
		slidingMinTask = new SlidingMinTask(instrumentWindowMinStats, currentTime.getMillis(), tick, windowSizeSec);
		slidingMinTask.updateStats();
		assertEquals(instrumentWindowMinStats.getInstrumentMinLastWindow().get("instr1"), 8.5);

		// Tick not received at 19th second
		// Simulate tick at 20th second
		tickTime = new DateTime(2021, 6, 14, 6, 17, 20);
		currentTime = new DateTime(2021, 6, 14, 6, 17, 20);
		tick = new InstrumentTick("instr1", 13.5, tickTime.getMillis());
		slidingMinTask = new SlidingMinTask(instrumentWindowMinStats, currentTime.getMillis(), tick, windowSizeSec);
		slidingMinTask.updateStats();
		assertEquals(instrumentWindowMinStats.getInstrumentMinLastWindow().get("instr1"), 8.5);

		// Simulate missed tick previously at 19th second at current time 21st second
		tickTime = new DateTime(2021, 6, 14, 6, 17, 19);
		currentTime = new DateTime(2021, 6, 14, 6, 17, 21);
		tick = new InstrumentTick("instr1", 60.5, tickTime.getMillis());
		slidingMinTask = new SlidingMinTask(instrumentWindowMinStats, currentTime.getMillis(), tick, windowSizeSec);
		slidingMinTask.updateStats();
		assertEquals(instrumentWindowMinStats.getInstrumentMinLastWindow().get("instr1"), 13.5);

		// Simulate tick for the current time also i.e. 21st second
		tickTime = new DateTime(2021, 6, 14, 6, 17, 21);
		currentTime = new DateTime(2021, 6, 14, 6, 17, 21);
		tick = new InstrumentTick("instr1", 6.5, tickTime.getMillis());
		slidingMinTask = new SlidingMinTask(instrumentWindowMinStats, currentTime.getMillis(), tick, windowSizeSec);
		slidingMinTask.updateStats();
		assertEquals(instrumentWindowMinStats.getInstrumentMinLastWindow().get("instr1"), 6.5);

		// Simulate tick at 22nd second
		tickTime = new DateTime(2021, 6, 14, 6, 17, 22);
		currentTime = new DateTime(2021, 6, 14, 6, 17, 22);
		tick = new InstrumentTick("instr1", 23.2, tickTime.getMillis());
		slidingMinTask = new SlidingMinTask(instrumentWindowMinStats, currentTime.getMillis(), tick, windowSizeSec);
		slidingMinTask.updateStats();
		assertEquals(instrumentWindowMinStats.getInstrumentMinLastWindow().get("instr1"), 6.5);

		// Simulate tick at 23rd second
		tickTime = new DateTime(2021, 6, 14, 6, 17, 23);
		currentTime = new DateTime(2021, 6, 14, 6, 17, 23);
		tick = new InstrumentTick("instr1", 12.9, tickTime.getMillis());
		slidingMinTask = new SlidingMinTask(instrumentWindowMinStats, currentTime.getMillis(), tick, windowSizeSec);
		slidingMinTask.updateStats();
		assertEquals(instrumentWindowMinStats.getInstrumentMinLastWindow().get("instr1"), 6.5);

		// Simulate tick at 24th second
		tickTime = new DateTime(2021, 6, 14, 6, 17, 24);
		currentTime = new DateTime(2021, 6, 14, 6, 17, 24);
		tick = new InstrumentTick("instr1", 26.4, tickTime.getMillis());
		slidingMinTask = new SlidingMinTask(instrumentWindowMinStats, currentTime.getMillis(), tick, windowSizeSec);
		slidingMinTask.updateStats();
		assertEquals(instrumentWindowMinStats.getInstrumentMinLastWindow().get("instr1"), 12.9);

	}

	@Configuration
	static class config {

	}
}
