package com.solactive.service.instrument.tick.stats.model;

import java.util.concurrent.ConcurrentSkipListMap;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.util.concurrent.AtomicDouble;

@Component
public class AllInstrumentWindowStats {

	//Marking variables final to make it thread safe
	private final AtomicDouble avgLastWindow = new AtomicDouble(0);

	private final AtomicDouble maxLastWindow = new AtomicDouble(Double.MIN_VALUE);

	private final AtomicDouble minLastWindow = new AtomicDouble(Double.MAX_VALUE);

	private final AtomicDouble countLastWindow = new AtomicDouble(0.0);

	@JsonIgnore
	private final ConcurrentSkipListMap<Double, Double> tickPrices = new ConcurrentSkipListMap<Double, Double>();

	public AtomicDouble getAvgLastWindow() {
		return avgLastWindow;
	}

	public AtomicDouble getMaxLastWindow() {
		return maxLastWindow;
	}

	public AtomicDouble getMinLastWindow() {
		return minLastWindow;
	}

	public AtomicDouble getCountLastWindow() {
		return countLastWindow;
	}

	public ConcurrentSkipListMap<Double, Double> getTickPrices() {
		return tickPrices;
	}

}
