package com.solactive.service.instrument.tick.stats.controller;

import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.solactive.service.instrument.tick.stats.config.InstrumentTickConfig;
import com.solactive.service.instrument.tick.stats.model.InstrumentTick;
import com.solactive.service.instrument.tick.stats.model.InstrumentTickStatsResponse;
import com.solactive.service.instrument.tick.stats.service.InstrumentTickStatsService;

@RestController
@RequestMapping("/instrument-stats-svc")
public class InstrumentTickStatsController {
	
	@Autowired
	InstrumentTickStatsService instrumentTickSvc;
	
	@Autowired
	private InstrumentTickConfig instrTickConf;

	@PostMapping("/ticks")
	public ResponseEntity<Object> createOrUpdateInstrumentTick(@Valid @RequestBody InstrumentTick tick) {

		Date now = new Date();
		Integer windowSizeSeconds = instrTickConf.getWindowSizeSec() * 1000;
		
		if (tick.getTimestamp() + windowSizeSeconds < now.getTime()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}else {
			instrumentTickSvc.updateInstrumentTick(tick, now.getTime());
			return new ResponseEntity<>(HttpStatus.CREATED);
		}
	}
	
	@GetMapping("/statistics")
	public ResponseEntity<InstrumentTickStatsResponse> getAllInstrumentStatsForLastWindow() {
		return new ResponseEntity<InstrumentTickStatsResponse>(instrumentTickSvc.getAllInstrumentWindowStats(), HttpStatus.OK);
	}
	
	@GetMapping("/statistics/{instrument_identifier}")
	public ResponseEntity<InstrumentTickStatsResponse> getInstrumentStatusForLastWindow(@PathVariable String instrumentCode) {
		return new ResponseEntity<InstrumentTickStatsResponse>(instrumentTickSvc.getInstrumentWindowStats(instrumentCode), HttpStatus.OK);
	}
	

}
