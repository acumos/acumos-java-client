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
