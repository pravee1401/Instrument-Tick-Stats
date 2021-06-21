package com.solactive.service.instrument.tick.stats.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "instrument-tick-stats")
public class InstrumentTickConfig {

	private int windowSizeSec;

	public int getWindowSizeSec() {
		return windowSizeSec;
	}

	public void setWindowSizeSec(int windowSizeSec) {
		this.windowSizeSec = windowSizeSec;
	}
}
