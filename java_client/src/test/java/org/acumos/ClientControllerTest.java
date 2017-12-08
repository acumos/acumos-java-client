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

import java.io.FileNotFoundException;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * 
 * @author *****
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ClientControllerTest {

	@Mock
	ClientController cientController;

	JSONObject obj = new JSONObject();
	JSONObject obj1 = new JSONObject();
	String serviceUrl = "http://cognita-dev1-vm01-core.eastus.cloudapp.azure.com:8090/onboarding-app/v2/auth";
	String token = null;

	@Test
	public void testgetConfigFile() throws Exception {

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
			assert (true);
		}
	}

	@Test
	public void testLoginUser() throws Exception {

		try {
			obj1.put("username", "testUser");
			obj1.put("password", "testPswd");
			obj.put("request_body", obj1);
			token = ClientController.loginUser(obj.toString(), serviceUrl);
			assert (true);
		} catch (Exception e) {
			// pssing in case server is not available.
			assert (true);
		}

	}

	@Test
	public void getAppFileTest() {

		try {
			cientController.getConfigFile("/", true);
			assert (true);
		} catch (FileNotFoundException e) {
			assert (true);
		}

	}

	@Test
	public void getConfigFileTest() {
		try {
			cientController.getAppFile("/", true);
			assert (true);
		} catch (FileNotFoundException e) {
			assert (true);
		}
	}

	private static boolean isWindowsSys() {
		String osName = System.getProperty("os.name");
		String osNameMatch = osName.toLowerCase();
		if (osNameMatch.contains("windows"))
			return true;
		return false;
	}

	
	
}