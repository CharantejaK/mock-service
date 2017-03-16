package com.deloitte.mockservice.helper;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.deloitte.mockservice.dao.MockDataDao;
import com.deloitte.mockservice.dto.ErrorCode;
import com.deloitte.mockservice.dto.SaveMockDataRequest;
import com.deloitte.mockservice.dto.SaveMockDataResponse;
import com.deloitte.mockservice.dto.ViewMockDataResponse;
import com.deloitte.mockservice.mapper.MockServiceMapper;
import com.deloitte.mockservice.model.MockData;
import com.deloitte.mockservice.type.ContentType;
import com.delolitte.mockservice.exception.MockServiceSystemException;

@Component
public class MockServiceHelper {
	Logger LOG = Logger.getLogger(MockServiceHelper.class);

	private static final String EMPTY = "";
	private static final String SPACE = " ";
	private static final String NEW_LINE = "\n";
	private static String DUPLICATE_REQUEST = "00001";
	private static String DUPLICATE_REQUEST_MESSAGE = "Duplicate request and response. An entry with the same request and response already exists.";
	private static String INVALID_REQUEST = "00002";
	private static String INVALID_RESPONSE = "Invalid request or response . Please check the request and responses are valid as per the selected content type;";

	@Autowired
	MockDataDao mockDataDao;
	
	@Autowired
	MockServiceMapper mockServiceMapper;

	/**
	 * @param request
	 * @param contentType
	 * @param id
	 * @return
	 */
	public ResponseEntity<String> getMockResponse(String request, String contentType, Long id) {
		LOG.info("Inside get Mock Response method inside the mock helper id=" + id + " request =" + request);
		String response = null;
		HttpHeaders httpHeaders = new HttpHeaders();
		try {
			// Gets the MockData from the DB based on the  id
			List<MockData> mockDataList = mockDataDao.findById(id);
			for (MockData mockData : mockDataList) {			
				// validates the request content type with the request and
				// returns the corresponding response
				if ((contentType.equals(MediaType.APPLICATION_JSON_VALUE))
						&& (getFormattedJsonString(request).equals(getFormattedJsonString(mockData.getRequest())))
						|| (contentType.equals(MediaType.APPLICATION_XML_VALUE)) && (getFormattedXmlString(request)
								.equals(getFormattedXmlString(mockData.getRequest())))) {
					// gets the response from the MockData for the request id
					response = mockData.getResponse();
					// Sets the content type in the response header
					httpHeaders.setContentType(ContentType.findByName(mockData.getContenttype()).getType());
					break;
				}
			}
		} catch (MockServiceSystemException e) {
			LOG.error("Exception inside getMockResponse", e);			
		}

		return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
	}

	/**
	 * @param json
	 * @return
	 */
	protected String getFormattedJsonString(String json) throws MockServiceSystemException {
		String jsonStr = null;
		try {
		JSONObject body = new JSONObject(json);
		jsonStr =  body.toString();
		} catch(Exception e) {
			throw new MockServiceSystemException(e);
		}
		return jsonStr;
	}

	/**
	 * @param xmlStr
	 * @return
	 * @throws validates
	 *             the xml and throws MockServiceSystemException in case of
	 *             invalid xml
	 * 
	 */
	protected String getFormattedXmlString(String xmlStr) throws MockServiceSystemException {
		String formattedXmlStr = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			formattedXmlStr = writer.getBuffer().toString().replaceAll(SPACE, EMPTY).replaceAll(NEW_LINE, EMPTY);
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			LOG.error("exception inside getFormattedXmlString", e);
			throw new MockServiceSystemException(e);
		}
		return formattedXmlStr;
	}
	
	public ResponseEntity<ViewMockDataResponse> getMockDataByClient(String client) {
		ViewMockDataResponse response = new ViewMockDataResponse();
		List<MockData> mockDataList = mockDataDao.findByClient(client.toLowerCase());
		response.setMockDataList(mockServiceMapper.map(mockDataList));
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
	}
	
	public ResponseEntity<SaveMockDataResponse> saveMockData(SaveMockDataRequest mockDataRequest) {
		SaveMockDataResponse response = new SaveMockDataResponse();
		List<ErrorCode> errorList = new ArrayList<>();
		response.setErrorList(errorList);
		try {
			String requestStr = null;
			String responseStr = null;
			if (mockDataRequest.getContenttype().equals(MediaType.APPLICATION_JSON_VALUE)) {
				requestStr = getFormattedJsonString(mockDataRequest.getRequest());
				responseStr = getFormattedJsonString(mockDataRequest.getResponse());
			} else if (mockDataRequest.getContenttype().equals(MediaType.APPLICATION_XML_VALUE)) {
				requestStr = getFormattedXmlString(mockDataRequest.getRequest());
				responseStr = getFormattedXmlString(mockDataRequest.getResponse());
			}
			
			List<MockData> mockDataList = mockDataDao.findByRequestAndResponse(requestStr, responseStr);
			if (mockDataList == null || mockDataList.isEmpty()) {
				mockDataRequest.setRequest(requestStr);
				mockDataRequest.setResponse(responseStr);
			} else {
				ErrorCode errorCode = new ErrorCode();
				errorCode.setErrorCode(DUPLICATE_REQUEST);
				errorCode.setErrorMessage(DUPLICATE_REQUEST_MESSAGE);
				response.getErrorList().add(errorCode);
			}		
		} catch (MockServiceSystemException e) {
			ErrorCode errorCode = new ErrorCode();
			errorCode.setErrorCode(INVALID_REQUEST);
			errorCode.setErrorMessage(INVALID_RESPONSE);
			response.getErrorList().add(errorCode);		
		}
		if (response.getErrorList().isEmpty()) {
		MockData mockData = mockDataDao.save(mockServiceMapper.map(mockDataRequest));
		response.setRequestId(mockData.getId());
		}
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
	}
}
