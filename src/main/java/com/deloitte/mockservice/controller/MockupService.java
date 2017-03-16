package com.deloitte.mockservice.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.deloitte.mockservice.dto.SaveMockDataRequest;
import com.deloitte.mockservice.dto.SaveMockDataResponse;
import com.deloitte.mockservice.dto.ViewMockDataResponse;
import com.deloitte.mockservice.helper.MockServiceHelper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class MockupService {
	
	
	Logger LOG = Logger.getLogger(MockupService.class);

	@Autowired
	MockServiceHelper mockServiceHelper;

	@RequestMapping(value = "/mockservice", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE, MediaType.ALL_VALUE })
	public @ResponseBody ResponseEntity<String> getMockResponse(
			@RequestHeader(value = "Content-Type") String contentType, @RequestBody String request,
			@RequestParam Long id)  {
		LOG.info("Inside the getMockResponse Service");
		return mockServiceHelper.getMockResponse(request, contentType, id);
	}
	
	@CrossOrigin(origins = "http://localhost:8888")
	@RequestMapping(value = "/getmockdatalist", method = RequestMethod.GET, produces = (MediaType.APPLICATION_JSON_VALUE))
	public @ResponseBody ResponseEntity<ViewMockDataResponse> getMockDataByClient(@RequestParam String clientName)  {
		LOG.info("Inside the getMockResponse Service");
		return mockServiceHelper.getMockDataByClient(clientName);
	}
	
	@RequestMapping(value = "/savemockdata", method = RequestMethod.POST, produces = (MediaType.APPLICATION_JSON_VALUE))
	public @ResponseBody ResponseEntity<SaveMockDataResponse> saveMockData(@RequestBody SaveMockDataRequest saveMockDataRequest)  {
		LOG.info("Inside the getMockResponse Service");
		return mockServiceHelper.saveMockData(saveMockDataRequest);
	}
}