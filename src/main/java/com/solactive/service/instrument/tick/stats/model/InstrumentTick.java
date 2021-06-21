package com.solactive.service.instrument.tick.stats.model;

import javax.validation.constraints.NotNull;

public class InstrumentTick {
	

	@NotNull(message = "Instrument code must not be null")
	private String instrumentCode;

	@NotNull(message = "Price must not be null")
	private Double price;

	@NotNull(message = "Timestamp must not be null")
	private Long timestamp;
	
	public InstrumentTick(String instrumentCode, Double price, Long timestamp){
		this.instrumentCode = instrumentCode;
		this.price = price;
		this.timestamp = timestamp;
	}

	public String getInstrumentCode() {
		return instrumentCode;
	}

	public void setInstrumentCode(String instrumentCode) {
		this.instrumentCode = instrumentCode;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((instrumentCode == null) ? 0 : instrumentCode.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InstrumentTick other = (InstrumentTick) obj;
		if (instrumentCode == null) {
			if (other.instrumentCode != null)
				return false;
		} else if (!instrumentCode.equals(other.instrumentCode))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}

}
