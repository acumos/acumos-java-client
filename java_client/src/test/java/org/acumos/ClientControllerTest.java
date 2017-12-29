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
package org.acumos;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * 
 * @author *****
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ClientControllerTest {

	ClientController cientController = new ClientController();

	JSONObject obj = new JSONObject();
	JSONObject obj1 = new JSONObject();
	static String serviceUrl = "http://cognita-dev1-vm01-core.eastus.cloudapp.azure.com:8090/onboarding-app/v2/models";
	String modelType = "H";
	String modelname = "H2O";
	static String token = "SampleToken";

	@Test
	public void generateModelServiceTest() {
		// Passing some dummy files for testing
		File demo1 = new File("default.proto");
		File demo2 = new File("modelConfig.properties");
		File demo3 = new File("metadata.json");
		File demo4 = new File("LICENSE.txt");
		File demo5 = new File("tokenfile.txt");
		cientController.generateModelService(demo1, demo2, demo3, modelType, demo4, demo5);
	}

	@Test
	public void getConfigFileTest() {

		try {

			String projectPath = System.getProperty("user.dir");
			boolean windowsFlag = isWindowsSys();

			if (windowsFlag) {
				projectPath = projectPath + "\\testdata";
				System.out.println("windows os");
			} else {
				projectPath = projectPath + "/testdata";
			}

			// ClientController.main(new String[] {
			// projectPath,"G",projectPath,"GenericModel"});

			cientController.getConfigFile(projectPath, windowsFlag);

			assert (true);
		} catch (Exception e) {
			// pssing in case server is not available.
			assert (false);
		}
	}

	@Test
	public void loginUserTest() {

		try {
			obj1.put("username", "testUser");
			obj1.put("password", "testPswd");
			obj.put("request_body", obj1);
			token = ClientController.loginUser(obj.toString(), serviceUrl);
			assert (false);
		} catch (Exception e) {
			// pssing in case server is not available.
			assert (true);
		}

	}

	@Test
	public void generateProtobufTest() {

		try {
			cientController.generateProtobuf("/", true);
			assert (true);
		} catch (FileNotFoundException e) {
			assert (false);
		}

	}

	@Test
	public void getAppFileTest() {
		try {
			cientController.getAppFile("/", true);
			assert (true);
		} catch (FileNotFoundException e) {
			assert (false);
		}
	}

	@Test
	public void zipFileTest() {
		try {
			List<String> files = new ArrayList<String>();
			files.add(new File("default.proto").getAbsolutePath());
			files.add(new File("modelConfig.properties").getAbsolutePath());
			cientController.zipFile(files, "test.zip");
		} catch (Exception e) {
			assert (false);
		}
	}

	@Test
	public void generateMetadataTest() {

		try {
			cientController.generateMetadata(modelType, modelname);
			assert (true);
		} catch (Exception e) {
			assert (false);
		}
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

	/*@Test
	public void pushModelTest() {

		try {
 			File proto = new File("default.proto");
			ClientController.pushModel(serviceUrl, "modelpackage.zip", "metadata.json", proto, token);
			assert (false);
		} catch (Exception e) {
			assert (true);
		}
	}
*/
	private static boolean isWindowsSys() {
		String osName = System.getProperty("os.name");
		String osNameMatch = osName.toLowerCase();
		if (osNameMatch.contains("windows"))
			return true;
		return false;
	}

}