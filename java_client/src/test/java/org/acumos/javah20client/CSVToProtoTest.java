/*-
* ===============LICENSE_START=======================================================
* Acumos Apache-2.0
* ===================================================================================
* Copyright (C) 2017-2018 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
* ===================================================================================
* This Acumos software file is distributed by AT&T and Tech Mahindra
* under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* This file is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* ===============LICENSE_END=========================================================
*/

package org.acumos.javah20client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(CSVToProto.class)
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
	
	@Test
	public void writeToProtoTest( ) throws FileNotFoundException, IOException {
		File f = cSVToProto.writeToProto(SAMPLE_CSV_FILE_PATH, "sampleModel");
	}
}
