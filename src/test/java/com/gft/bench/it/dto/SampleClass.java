package com.gft.bench.it.dto;

import java.io.Serializable;

public class SampleClass implements Serializable {
	
	static final long serialVersionUID = 1L;
	
	public String data;
	
	public SampleClass(String data) {
		this.data = data;
	}
}
