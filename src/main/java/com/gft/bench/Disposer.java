package com.gft.bench;

public final class Disposer implements AutoCloseable {

	AutoCloseable disposable;
	
	public Disposer(AutoCloseable disposable) {
		this.disposable = disposable;
	}

	@Override
	public void close() throws Exception {
		disposable.close();
	}

}
