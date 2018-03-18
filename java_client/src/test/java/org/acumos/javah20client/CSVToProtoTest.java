package org.acumos.javah20client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CSVToProtoTest {
	
	@Mock
	CSVToProto cSVToProto;// = new CSVToProto();
	
	public final String SAMPLE_CSV_FILE_PATH = "IRIS2.csv";
	
	@Test
	public void getProtoDataTypeTest(){
		String value = "TestFunctionality";
		String dataType =null;
		dataType = CSVToProto.getProtoDataType(value);
		value = "12345";
		dataType = CSVToProto.getProtoDataType(value);
		value = "123.45";
		dataType = CSVToProto.getProtoDataType(value);
		value = "True";
		dataType = CSVToProto.getProtoDataType(value);
	}
	
	@Test
	public void createServiceTest(){
		String serviceName = "SService";
		String inputMessageName = "SInputmsg";
		String outputMessageName = "SOutputmsg";
		CSVToProto.createService(serviceName, inputMessageName, outputMessageName);
	}
	
	@Test
	public void createProtoHeaderTest(){
		CSVToProto.createProtoHeader();
	}
	
	@Test
	public void createProtoFooterTest(){
		CSVToProto.createProtoFooter();
	}
	
	/*@Test
	public void writeToProtoTest() throws FileNotFoundException, IOException{
		cSVToProto.writeToProto(SAMPLE_CSV_FILE_PATH);
	}*/
	
	@Test
	public void mainTest() throws IOException{
		String[] args = null;
		CSVToProto.main(args);
	}
	
	@Test
	public void createMessageTest(){
		
		String value = "TestValue";
		String messageName = "SampleMsg";
		
		List<String> inputFields = new ArrayList<>();
		inputFields.add(CSVToProto.getProtoDataType(value));
		List<String> dataTypeList = new ArrayList<>();
		dataTypeList.add(CSVToProto.getProtoDataType(value));
		CSVToProto.createMessage(messageName, inputFields, dataTypeList, true);		
	}
}
