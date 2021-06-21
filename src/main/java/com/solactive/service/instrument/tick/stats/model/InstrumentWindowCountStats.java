package com.solactive.service.instrument.tick.stats.model;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.springframework.stereotype.Component;

@Component
public class InstrumentWindowCountStats {

	// Mark final to make the constructs thread safe
	private final ConcurrentHashMap<String, Double> instrumentCountLastWindow = new ConcurrentHashMap<String, Double>();

	private final ConcurrentHashMap<String, ConcurrentSkipListMap<Long, Double>> tickMap = new ConcurrentHashMap<String, ConcurrentSkipListMap<Long, Double>>();

	public ConcurrentHashMap<String, Double> getInstrumentCountLastWindow() {
		return instrumentCountLastWindow;
	}

	public ConcurrentHashMap<String, ConcurrentSkipListMap<Long, Double>> getTickMap() {
		return tickMap;
	}

}
