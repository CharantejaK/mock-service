package com.deloitte.mockservice.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.delolitte.mockservice.exception.MockServiceSystemException;

public class MockServiceHelperTest {
	
	private static final String JSON_STRING = "{\"DocDate\":\"2011-05-11\",\"CustomerCode\":\"CUST1\"}";
	private static final String INVALID_XML_STRING = "?xml version=\"1.0\" encoding=\"utf-8\"?><Response><ResponseCode>1</ResponseCode><ResponseMessage>Successful-Scenario-1</ResponseMessage></Response>";
	private static final String XML_STRING = "<?xml version=\"1.0\" encoding=\"utf-8\"?><Response><ResponseCode>1</ResponseCode><ResponseMessage>Successful-Scenario-1</ResponseMessage></Response>";
	
	@Test
	public void testGetFormattedJsonString() throws MockServiceSystemException {
		MockServiceHelper helper = new MockServiceHelper();
		String jsonString = helper.getFormattedJsonString(JSON_STRING);
		assertEquals(jsonString, JSON_STRING);
	}
	
	@Test(expected=MockServiceSystemException.class)	
	public void testFailGetXmlFormattedString() throws MockServiceSystemException 	{	
		MockServiceHelper helper = new MockServiceHelper();
		helper.getFormattedXmlString(INVALID_XML_STRING);		
	}
	
	@Test
	public void testGetXmlFormattedString() throws MockServiceSystemException 	{	
		MockServiceHelper helper = new MockServiceHelper();
		String xmlString = helper.getFormattedXmlString(XML_STRING);	
		assertNotNull(xmlString);
	}
}
