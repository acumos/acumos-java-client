package org.acumos.javah20client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mock;

import ch.qos.logback.core.net.SyslogOutputStream;

public class H2oCSVtoProtoTest {
	
	@Mock
	H2oCSVtoProto h2oCSVtoProto;
	
	private static final String SAMPLE_CSV_FILE_PATH = "IRIS3.csv";
	static final String h2oModelFullPath = "modelpackage.zip";
	List<String> inputFields = new ArrayList<>();
	List<String> dataTypeList = new ArrayList<>();
	List<String> outputFields = new ArrayList<>();
	
/*	@Test
	public void getH2oModelInfoTest() throws Exception {
		h2oCSVtoProto.getH2oModelInfo(h2oModelFullPath, inputFields, outputFields);
	}*/
	
	@Test
	public void getProtoDataTypeTest() {
		String value = "Test";
		H2oCSVtoProto.getProtoDataType(value);
		value = "1234";
		H2oCSVtoProto.getProtoDataType(value);
		value = "True";
		H2oCSVtoProto.getProtoDataType(value);
	}
	
	@Test
	public void createServiceTest() {
		String serviceName = "Demo";
		String inputMessageName = "input";
		String outputMessageName = "output";
		H2oCSVtoProto.createService(serviceName, inputMessageName, outputMessageName);
	}
	
	@Test
	public void createProtoHeaderTest() {
		H2oCSVtoProto.createProtoHeader();
	}
	
	@Test
	public void createProtoFooterTest() {
		H2oCSVtoProto.createProtoFooter();
	}
	
	@Test
	public void createMessageTest(){
		String value = "TestValue";
		String messageName = "SampleMsg";
		inputFields.add(CSVToProto.getProtoDataType(value));
		dataTypeList.add(CSVToProto.getProtoDataType(value));
		H2oCSVtoProto.createMessage(messageName, inputFields, dataTypeList, true);		
	}
	
/*	@Test
	public void writeToProtoTest() throws FileNotFoundException, IOException {
		String modelName = "sampleH2o";
		System.out.println("In writeToProtoTest()");
		h2oCSVtoProto.writeToProto(SAMPLE_CSV_FILE_PATH, modelName, h2oModelFullPath);
		System.out.println("After writeToProtoTest()");
	}*/

}
