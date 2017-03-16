package com.deloitte.mockservice.dto;

public class MockDataDto {	

	private String request;

	private String response;
	
	private String contenttype;

	private Long requestid;
	
	private String client;

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public Long getRequestid() {
		return requestid;
	}

	public void setRequestid(Long requestid) {
		this.requestid = requestid;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getContenttype() {
		return contenttype;
	}

	public void setContenttype(String contenttype) {
		this.contenttype = contenttype;
	}
}
