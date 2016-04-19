package com.gft.bench.events;

public class ResultMsg {

	private String message;
	private RequestResult result;
	
	public ResultMsg() { }
	
	public ResultMsg(String message, RequestResult resultType) {
		this.message = message;
		this.result = resultType;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public RequestResult getResult() {
		return result;
	}
	public void setResult(RequestResult result) {
		this.result = result;
	}
}
