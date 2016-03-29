package com.gft.bench;

public class ResultMsg {

	private String message;
	private ResultType result;
	
	public ResultMsg() { }
	
	public ResultMsg(String message, ResultType resultType) {
		this.message = message;
		this.result = resultType;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public ResultType getResult() {
		return result;
	}
	public void setResult(ResultType result) {
		this.result = result;
	}
}
