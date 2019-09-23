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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientController {

	static Logger logger = LoggerFactory.getLogger(ClientController.class);

	public static void main(String args[]) {

		if (args.length == 0) {
			System.err.println("No arguments!");
			System.exit(0);
		}

		String serviceUrl = null, authUrl = null;

		try {
			boolean modelVal;
			String isMicroserviceFlag = null;
			File model = null;
			File servicejar = null, licenseFile = null;
			String token = null, tokenType = null, tokenFilePath = null;
			String inputCSVFile = null;
			String modelType = null, path = null, modelName = null, onboardingType = null, dumpPath = null;
			List<String> modelNameList = new ArrayList<String>();

			logger.info("Length : {} ", args.length);
			// command line arguments
			modelType = args[0];
			path = args[1];
			modelName = args[2];

			// Get the modelImp reference according to the model type
			logger.info("Model type is {}", modelType);
			ClientControllerInterface clientRef = getModelImplementer(modelType);

			Properties prop = new Properties();
			InputStream input = null;

			input = new FileInputStream(new File(path, "application.properties"));
			prop.load(input);
			serviceUrl = prop.getProperty("push_url");
			authUrl = prop.getProperty("auth_url");
			tokenType = prop.getProperty("token_type");
			tokenFilePath = prop.getProperty("token_file");
			dumpPath = prop.getProperty("dump_path");
			isMicroserviceFlag = prop.getProperty("isMicroservice");

			modelNameList = clientRef.returnModelMethodList(path);

			if (args.length == 4) {

				String temp = args[3];
				if (temp.contains("csv")) {
					inputCSVFile = temp;
				} else {
					onboardingType = temp;
				}

			} else if (args.length == 5) {
				inputCSVFile = args[3];
				onboardingType = args[4];
			}

			logger.debug("onboarding Type is " + onboardingType);
			if (onboardingType == null || !onboardingType.equalsIgnoreCase("webonboard")) {
				token = clientRef.checkToken(tokenType, tokenFilePath, authUrl);
			}

			if ((token != null && !token.isEmpty()) || onboardingType.equalsIgnoreCase("webonboard")) {

				modelVal = clientRef.isValidWord(modelName);

				if (modelVal == true) {

					// Get the service jar from supporting folder
					String servicePath = clientRef.getModelService(path);
					servicejar = new File(servicePath);
					model = new File(path + File.separator + modelName + ".jar");

					try {

						File config = null;
						if (!modelType.equalsIgnoreCase("H")) {
							config = clientRef.getConfigFile(path);
						}
						
						File sparkConfig = null;
						if (modelType.equalsIgnoreCase("S")) {
							sparkConfig = clientRef.getsparkConfigFile(path);
						}
						

						File appFile = clientRef.getAppFile(path);
						File protof = null;

						// create the modelpackage.zip file
						clientRef.generateModelService(model, servicejar, config, modelType, appFile, sparkConfig);

						// Generate Protobuf file
						protof = clientRef.generateProtobuf(path, inputCSVFile, modelType, modelName, modelNameList);

						// Get licence File if available
						File dir = new File(path);
						String[] fileList = dir.list();
						for (String name : fileList) {
							if (name.toLowerCase().contains("license") && name.toLowerCase().contains(".json")) {
								licenseFile = new File(path + File.separator + name);
							}
						}

						// Generate Metadata.json file
						clientRef.generateMetadata(modelType, modelName);
						
						if (protof.exists()) {

							if (onboardingType != null && onboardingType.equalsIgnoreCase("webonboard")) {
								logger.info("Creating modeldump for web based onboarding");
								List<String> files = new ArrayList<>();
								files.add(new File("modelpackage.zip").getAbsolutePath());
								files.add(new File("metadata.json").getAbsolutePath());
								files.add(protof.getAbsolutePath());
								if (licenseFile != null && licenseFile.exists()) {
									files.add(licenseFile.getAbsolutePath());
								}
								clientRef.zipFile(files, "modeldump.zip");
								FileUtils.copyFileToDirectory(new File("modeldump.zip"), new File(dumpPath));
								logger.info("copied modeldump.zip to destination folder");

							} else {
								// Call Rest Client for Onboarding API

								if (licenseFile != null && licenseFile.exists()) {
									clientRef.pushModel(serviceUrl, "modelpackage.zip", "metadata.json", protof,
											licenseFile, token, isMicroserviceFlag);
								} else {
									clientRef.pushModel(serviceUrl, "modelpackage.zip", "metadata.json", protof, null,
											token, isMicroserviceFlag);
								}
							}
						} else {
							throw new FileNotFoundException("proto file is not generated/provided");
						}
					} catch (FileNotFoundException fe) {
						logger.error(fe.getMessage());
					} catch (Exception e) {
						logger.error(e.getMessage());
					}

				} else {
					logger.error(
							"Model name should not contain special character or spaces. Don't include the file extensions.");
					throw new Exception(
							"Model name should not contain special character or spaces. Don't include the file extensions");
				}
			} else {
				logger.error(
						"Invalid authentication, Update the Tokenfile with valid token and provide valid Token Type");
				throw new Exception(
						"Invalid authentication, Update the Tokenfile with valid token and provide valid Token Type");
			}

		} catch (ArrayIndexOutOfBoundsException ae) {
			logger.error(ae.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		} catch (RuntimeException re) {
			logger.error(re.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	public static ClientControllerInterface getModelImplementer(String modelType) {

		ClientControllerInterface client = null;
		switch (modelType) {
		case "H":
			client = new H2OModelImpl();
			break;
		case "G":
			client = new JavaGenericModelImpl();
			break;
		case "S":
			client = new JavaSparkModelImpl();
			break;
		default:
			logger.info("Invalid model type");
			break;
		}

		return client;
	}
}