package com.deloitte.mockservice.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.deloitte.mockservice.dto.MockDataDto;
import com.deloitte.mockservice.dto.SaveMockDataRequest;
import com.deloitte.mockservice.model.MockData;

@Component
public class MockServiceMapper {

	public List<MockDataDto> map(List<MockData> mockDataList) {
		List<MockDataDto> mockDtoList = new ArrayList<>();
		for (MockData mockData : mockDataList) {
			MockDataDto mockDto = new MockDataDto();			
			mockDto.setRequest(mockData.getRequest());
			mockDto.setContenttype(mockData.getContenttype());
			mockDto.setRequestid(mockData.getId());
			mockDto.setResponse(mockData.getResponse());
			mockDto.setClient(mockData.getClient());
			mockDtoList.add(mockDto);
		}
		return mockDtoList;
	}
	
	public MockData map(SaveMockDataRequest saveMockDataRequest) {
		MockData mockData = new MockData();
		mockData.setContenttype(saveMockDataRequest.getContenttype());
		mockData.setRequest(saveMockDataRequest.getRequest());
		mockData.setResponse(saveMockDataRequest.getResponse());
		mockData.setClient(saveMockDataRequest.getClient());
		return mockData;		
	}
}
