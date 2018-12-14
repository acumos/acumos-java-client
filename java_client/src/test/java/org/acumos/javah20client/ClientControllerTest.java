/* ===================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */
package org.acumos.javah20client;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito.Then;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.OngoingStubbing;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;



@RunWith(PowerMockRunner.class)
@PrepareForTest({ClientController.class, HttpResponse.class, DefaultHttpClient.class})
public class ClientControllerTest {


	@InjectMocks 
	ClientController clientController;// = new ClientController();

	JSONObject obj = new JSONObject();
	JSONObject obj1 = new JSONObject();
	String modelType = "H";
	String modelname = "H2O";
	static String token = "sampleToken";
	String projectPath = System.getProperty("user.dir");
	String authUrl = "http://cognita-dev1-vm01-core.eastus.cloudapp.azure.com:8090/onboarding-app/v2/auth";
	HttpPost p = new HttpPost(authUrl);
	
	@Mock
	DefaultHttpClient defaultHttpClient;
	
	@Mock
	HttpResponse resp;
	

	@Mock
	StringEntity stringEntity;

	@Test
	public void generateModelServiceTest() {
		// Passing some dummy files for testing
		File demo1 = new File("default.proto");
		File demo2 = new File("modelConfig.properties");
		File demo3 = new File("metadata.json");
		File demo4 = new File("tokenfile.txt");
		clientController.generateModelService(demo1, demo2, demo3, modelType, demo4);
		modelType = "G";
		clientController.generateModelService(demo1, demo2, demo3, modelType, demo4);
	}

	@Test
	public void getConfigFileTest() throws FileNotFoundException {

		projectPath = projectPath + File.separator + "testdata";
		clientController.getConfigFile(projectPath);
	}

	
	@Test
	public void loginUserTest() throws Exception {
		
		obj1.put("username", "testUser");
		obj1.put("password", "testPswd");
		obj.put("request_body", obj1);

//		PowerMockito.whenNew(StringEntity.class).withArguments(Mockito.anyObject(), Mockito.anyObject()).thenReturn(stringEntity); 
	//	PowerMockito.whenNew(StringEntity.class).withArguments(Mockito.anyString(), Mockito.anyString()).thenReturn(stringEntity);
		//p.setEntity(new StringEntity(obj, ContentType.create("application/json")));
		
		PowerMockito.whenNew(DefaultHttpClient.class).withNoArguments().thenReturn(defaultHttpClient);


		PowerMockito.when(defaultHttpClient.execute(Mockito.anyObject())).thenReturn(resp);
		
		Object object = new Object();
		
		  
				
		//Header header = OngoingStubbing<T>;
		PowerMockito.when(resp.getFirstHeader(Mockito.anyString())).thenReturn(null);
		
		//assertEquals(expected, actual);
		
		token = ClientController.loginUser(obj.toString(), authUrl);
		assertEquals(token, null);
		
		
		
		

	}
	 

	@Test
	public void generateProtobufTest() throws IOException {

		clientController.generateProtobuf("/", null, null, null);

	}

	@Test
	public void getAppFileTest() throws FileNotFoundException {
		clientController.getAppFile("/");
	}

	@Test
	public void zipFileTest() {
		List<String> files = new ArrayList<String>();
		files.add(new File("default.proto").getAbsolutePath());
		files.add(new File("modelConfig.properties").getAbsolutePath());
		clientController.zipFile(files, "test.zip");
	}

	@Test
	public void generateMetadataTest() {

		clientController.generateMetadata(modelType, modelname);
	}

	@Test
	public void isValidWordTest() {
		String value = "Acumos";
		boolean result;
		result = clientController.isValidWord(value);

		if (result) {
			assert (true);
		} else {
			assert (false);
		}
	}

	/*
	 * @Test public void pushModelTest() {
	 * 
	 * try { File proto = new File("default.proto");
	 * ClientController.pushModel(serviceUrl, "modelpackage.zip", "metadata.json",
	 * proto, token); assert (false); } catch (Exception e) { assert (true); } }
	 */
	
	@Test
	public void checkTokenTest() throws Exception {

		String tokenType = "apitoken", tokenFilePath = null;
		tokenFilePath = projectPath + File.separator + "tokenfile.txt";
		clientController.checkToken(tokenType, tokenFilePath, authUrl);
		/*tokenType = "jwttoken";
		obj1.put("username", "dummy");
		obj1.put("password", "dummy");
		obj.put("request_body", obj1);
	
		PowerMockito.whenNew(DefaultHttpClient.class).withNoArguments().thenReturn(defaultHttpClient);

		p.setEntity(new StringEntity(obj.toString(), ContentType.create("application/json")));
		
		PowerMockito.when(defaultHttpClient.execute(Mockito.anyObject())).thenReturn(resp);
		
		clientController.checkToken(tokenType, tokenFilePath, authUrl);*/
	}
}
