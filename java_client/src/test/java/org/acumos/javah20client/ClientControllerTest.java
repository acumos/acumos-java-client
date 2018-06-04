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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ClientControllerTest {

	ClientController cientController = new ClientController();

	JSONObject obj = new JSONObject();
	JSONObject obj1 = new JSONObject();
	String modelType = "H";
	String modelname = "H2O";
	static String token = null;

	@Test
	public void generateModelServiceTest() {
		// Passing some dummy files for testing
		File demo1 = new File("default.proto");
		File demo2 = new File("modelConfig.properties");
		File demo3 = new File("metadata.json");
		File demo4 = new File("tokenfile.txt");
		cientController.generateModelService(demo1, demo2, demo3, modelType, demo4);
		modelType = "G";
		cientController.generateModelService(demo1, demo2, demo3, modelType, demo4);
	}

	@Test
	public void getConfigFileTest() throws FileNotFoundException {

		String projectPath = System.getProperty("user.dir");
		projectPath = projectPath + File.separator + "testdata";
		cientController.getConfigFile(projectPath);
	}

	/*
	 * @Test public void loginUserTest() throws ClientProtocolException,
	 * IOException, ParseException { obj1.put("username", "testUser");
	 * obj1.put("password", "testPswd"); obj.put("request_body", obj1); token =
	 * ClientController.loginUser(obj.toString(), serviceUrl); if (token != null) {
	 * assert (true); } else { assert (false); } }
	 */

	@Test
	public void generateProtobufTest() throws IOException {

		cientController.generateProtobuf("/", null, null, null);

	}

	@Test
	public void getAppFileTest() throws FileNotFoundException {
		cientController.getAppFile("/");
	}

	@Test
	public void zipFileTest() {
		List<String> files = new ArrayList<String>();
		files.add(new File("default.proto").getAbsolutePath());
		files.add(new File("modelConfig.properties").getAbsolutePath());
		cientController.zipFile(files, "test.zip");
	}

	@Test
	public void generateMetadataTest() {

		cientController.generateMetadata(modelType, modelname);
	}

	@Test
	public void isValidWordTest() {
		String value = "Acumos";
		boolean result;
		result = cientController.isValidWord(value);

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
}
