package com.gft.bench.events.business;

import java.io.Serializable;

public interface BusinessEvent extends Serializable {

	String getData();
	void setData(String message);
}
