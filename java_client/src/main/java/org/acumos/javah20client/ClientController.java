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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.acumos.javah20client.CSVToProto;
import org.acumos.javah20client.H2oCSVtoProto;

public class ClientController {

	static Logger logger = LoggerFactory.getLogger(ClientController.class);

	public static void main(String args[]) {

		if (args.length == 0) {
			System.err.println("No arguments!");
			System.exit(0);
		}

		ClientController client = new ClientController();
		UrlValidator defaultValidator = new UrlValidator();

		try {
			boolean isWindows;
			boolean modelVal;
			isWindows = isWindowsSys();
			File model = null;
			File servicejar = null;
			String username;
			String passwd;
			String token = null;
			int count = 1;
			String inputCSVFile = null;

			// get the model name from command line
			String serviceUrl = args[0];
			String authUrl = args[1];
			String modelType = args[2];
			String path = args[3];
			String modelName = args[4];
			logger.info("Length : {} ", args.length);

			logger.info("Model type is {}", modelType);

			if (args.length == 8) {
				inputCSVFile = args[7];
			}

			/*
			 * If web based onboarding,there is no username or password required. In this
			 * case, the 4th argument will be the csv file if at all it is present.
			 */
			if (args.length == 6) {
				inputCSVFile = args[5];
			}

			JSONObject obj = new JSONObject();
			JSONObject obj1 = new JSONObject();

			// boolean valid = false;

			// Code for Authentication
			if (defaultValidator.isValid(serviceUrl)) {
				// if (valid) {
				while (count < 4) {
					if (args.length <= 5) {
						if (count == 1) {
							System.out.println("Please enter Username and Password");
						} else {
							System.out.println(
									"Username or Password is not correct, Please enter the correct credentials");
						}

						Scanner sc = new Scanner(System.in);
						System.out.println("Enter Username");
						username = sc.next();
						//sc.close();

						System.out.println("Please enter the password");
						Scanner sc1 = new Scanner(System.in);
						passwd = sc1.next();
						//sc1.close();

						obj1.put("username", username);
						obj1.put("password", passwd);
						obj.put("request_body", obj1);

						logger.info("JSON:" + obj.toString());

					} else {
						username = args[5];
						passwd = args[6];

						obj1.put("username", username);
						obj1.put("password", passwd);
						obj.put("request_body", obj1);

						logger.info("JSON:" + obj.toString());
					}

					token = loginUser(obj.toString(), authUrl);

					if (token != null) {
						try {
							// Create a new FileWriter object
							FileWriter fileWriter = new FileWriter("tokenfile.txt");
							fileWriter.write(token);
							fileWriter.close();
							FileUtils.copyFileToDirectory(new File("tokenfile.txt"), new File(path));
							count++;
							break;
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
							// logger.info(e);
						}
					} else {
						count++;
					}
				}

			}

			if (count == 4) {
				throw new RuntimeException();
			}

			modelVal = client.isValidWord(modelName);

			if (modelVal == true) {

				switch (modelType) {
				case "H":
					if (isWindows) {
						servicejar = new File(path + "\\" + modelName + "Service.jar");
						model = new File(path + "\\" + modelName + ".zip");
					} else {
						servicejar = new File(path + "/" + modelName + "Service.jar");
						model = new File(path + "/" + modelName + ".zip");
					}
					break;
				case "G":
					if (isWindows) {
						servicejar = new File(path + "\\" + "GenericModelService.jar");
						model = new File(path + "\\" + modelName + ".jar");
					} else {
						servicejar = new File(path + "/" +"GenericModelService.jar");
						model = new File(path + "/" + modelName + ".jar");
					}
					break;

				default:
					logger.info("Invalid model type");
					break;
				}
				try {
					File pbuff = client.getPBuffJar(path, isWindows);

					File congif = client.getConfigFile(path, isWindows);

					File appFile = client.getAppFile(path, isWindows);

					// Call generateModelService input is modelService.jar
					client.generateModelService(model, servicejar, congif, modelType, pbuff, appFile);

					// Generate Protobuf file
					File protof = client.generateProtobuf(path, isWindows, inputCSVFile, modelType, modelName);

					// Generate Metadata.json file
					client.generateMetadata(modelType, modelName);

					if (!defaultValidator.isValid(serviceUrl)) {
						// if (!valid) {
						List<String> files = new ArrayList<>();
						files.add(new File("modelpackage.zip").getAbsolutePath());
						files.add(new File("metadata.json").getAbsolutePath());
						files.add(protof.getAbsolutePath());
						client.zipFile(files, "modeldump.zip");
						FileUtils.copyFileToDirectory(new File("modeldump.zip"), new File(serviceUrl));
						logger.info("copied modeldump.zip to destination folder");

					} else {

						// Call Rest Client for Onboarding API
						pushModel(serviceUrl, "modelpackage.zip", "metadata.json", protof, token);
					}
				} catch (FileNotFoundException fe) {
					logger.info(fe.getMessage());
					logger.error(fe.toString());
					System.exit(0);
				}

			} else {
				logger.info(
						"Model name should not contain special character or spaces. Don't include the file extensions.");
			}

		} catch (ArrayIndexOutOfBoundsException ae) {
			logger.error(ae.toString());
		} catch (RuntimeException re) {
			logger.error(re.toString());
			logger.info("incorrect username, password");
		} catch (Exception e) {
			logger.error(e.toString());
		}

	}

	public boolean isValidWord(String w) {
		return w.matches("^[a-zA-Z0-9_-]*$");
	}

	public static String loginUser(String obj, String authUrl)
			throws ClientProtocolException, IOException, ParseException {
		logger.info("Started User Authentication...!");
		HttpClient c = new DefaultHttpClient();
		String token = null;

		/*String loginURL = null;
		
		if (serviceUrl.contains("dev1")) {
			loginURL = "http://cognita-dev1-vm01-core.eastus.cloudapp.azure.com:8090/onboarding-app/v2/auth";
			logger.info("loginToAcumos: " + loginURL);
		} else if (serviceUrl.contains("ist2")) {
			loginURL = "http://cognita-ist2-vm01-core.eastus.cloudapp.azure.com:8090/onboarding-app/v2/auth";
			logger.info("loginToAcumos: " + loginURL);
		} else if (serviceUrl.contains("ist")) {
			loginURL = "http://cognita-ist-vm01-core.eastus.cloudapp.azure.com:8090/onboarding-app/v2/auth";
			logger.info("loginToAcumos: " + loginURL);
		}*/

		HttpPost p = new HttpPost(authUrl);

		p.setEntity(new StringEntity(obj, ContentType.create("application/json")));

		HttpResponse r = c.execute(p);

		org.apache.http.Header head = r.getFirstHeader("jwtToken");

		if (head != null) {
			token = head.getValue();
			logger.info("User Authentication Successful...!");
			return token;
		} else {
			return null;
		}
	}

	public void zipFile(List<String> files, String name) {

		FileOutputStream fos = null;
		ZipOutputStream zipOut = null;
		FileInputStream fis = null;
		try {
			fos = new FileOutputStream(name);
			zipOut = new ZipOutputStream(new BufferedOutputStream(fos));
			for (String filePath : files) {
				File input = new File(filePath);
				fis = new FileInputStream(input);
				ZipEntry ze = new ZipEntry(input.getName());
				logger.info("Zipping the file: " + input.getName());
				zipOut.putNextEntry(ze);
				byte[] tmp = new byte[4 * 1024];
				int size = 0;
				while ((size = fis.read(tmp)) != -1) {
					zipOut.write(tmp, 0, size);
				}
				zipOut.flush();
				fis.close();
			}

			zipOut.close();
			logger.info("Done... Zipped the files...");
			// return zipOut;
		} catch (FileNotFoundException e) {
			logger.error(e.toString());
		} catch (IOException e) {
			logger.error(e.toString());

		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (Exception ex) {
				logger.error(ex.toString());

			}
		}

	}

	// Get the model.jar file
	public void generateModelService(File model, File service, File congif, String modelType, File pbuff,
			File appFile) {

		// Pack modelService.jar and model.jar into zip file
		List<String> files = new ArrayList<String>();
		files.add(model.getAbsolutePath());
		files.add(service.getAbsolutePath());
		files.add(pbuff.getAbsolutePath());
		files.add(appFile.getAbsolutePath());
		if (modelType.equals("G")) {
			files.add(congif.getAbsolutePath());
		}
		zipFile(files, "modelpackage.zip");
	}

	// Generate the protobuf file from sample data file
	public File generateProtobuf(String path, boolean isWindows, String inputCSVFile, String modelType,
			String modelName) throws IOException {
		logger.info("Generating proto file");
		logger.info("Model type is {}", modelType);
		File protoFile = null;
		String inputPath = null;
		String h2oModelFullPath = null;

		if (inputCSVFile == null) {
			logger.info("The input csv file is null");
			if (isWindows) {
				protoFile = new File(path + "\\default.proto");
			} else {
				protoFile = new File(path + "/default.proto");
			}
			return protoFile;
		} else {

			if (isWindows) {
				inputPath = path + "\\" + inputCSVFile;
			} else {
				inputPath = path + "/" + inputCSVFile;
			}

			if (isWindows) {
				h2oModelFullPath = path + "\\" + modelName + ".zip";
			} else {
				h2oModelFullPath = path + "/" + modelName + ".zip";
			}

			logger.info("I/P File : {} ", inputPath);
			logger.info("Model type is {}", modelType);
			if (modelType.equals("H")) {
				logger.debug("Entered h2o protobuf generation call");
				H2oCSVtoProto c = new H2oCSVtoProto();
				protoFile = c.writeToProto(inputPath, modelName, h2oModelFullPath);

			} else {
				logger.debug("Entered generic java protobuf generation call");
				CSVToProto c = new CSVToProto();
				protoFile = c.writeToProto(inputPath, modelName);
			}
			return protoFile;
		}
	}

	// Generate the pBuff jar file from sample data file
	public File getPBuffJar(String path, boolean isWindows) throws FileNotFoundException {

		logger.info("Get the protobuf jar");
		File pBuffFile = null;
		if (isWindows) {
			pBuffFile = new File(path + "\\protobuf-java-3.4.0.jar");
		} else {
			pBuffFile = new File(path + "/protobuf-java-3.4.0.jar");
		}

		return pBuffFile;
	}

	// Generate the modelConfig.properties file from directory
	public File getConfigFile(String path, boolean isWindows) throws FileNotFoundException {

		logger.info("Get modelConfig.properties");
		File conFile = null;
		if (isWindows) {
			conFile = new File(path + "\\modelConfig.properties");
		} else {
			conFile = new File(path + "/modelConfig.properties");
		}

		return conFile;
	}

	// Generate the application.properties file from directory
	public File getAppFile(String path, boolean isWindows) throws FileNotFoundException {

		logger.info("Get application.properties");
		File appFile = null;
		if (isWindows) {
			appFile = new File(path + "\\application.properties");
		} else {
			appFile = new File(path + "/application.properties");
		}

		return appFile;
	}

	// Generate metadata.json
	public void generateMetadata(String modelType, String name) {

		try {
			logger.info("Generate metadata.json");

			JSONObject meta = new JSONObject();

			JSONArray reqarray = new JSONArray();
			JSONObject reqarrayElementOne = new JSONObject();

			reqarrayElementOne.put("name", "java");
			reqarrayElementOne.put("version", "1.8.0_131");

			JSONObject reqarrayElementTwo = new JSONObject();
			reqarrayElementTwo.put("name", "protoc");
			reqarrayElementTwo.put("version", "3.4.0");

			JSONObject reqarrayElementThree = new JSONObject();
			reqarrayElementThree.put("name", "javac");
			reqarrayElementThree.put("version", "1.8.0_131");

			reqarray.put(reqarrayElementOne);
			reqarray.put(reqarrayElementTwo);
			reqarray.put(reqarrayElementThree);

			JSONArray idxarray = new JSONArray();

			JSONObject jav = new JSONObject();
			jav.put("indexes", idxarray);
			jav.put("requirements", reqarray);

			JSONObject dep = new JSONObject();
			dep.put("java", jav);

			JSONObject rnt = new JSONObject();
			if (modelType.equals("H")) {
				rnt.put("name", "h2o");
				rnt.put("toolkit", "H2O");
			} else {
				rnt.put("name", "javageneric");
			}
			rnt.put("encoding", "protobuf");
			rnt.put("version", "0.0.1");
			rnt.put("dependencies", dep);

			JSONObject met = new JSONObject();

			meta.put("schema", "cognita.schema.model:0.3.0");
			// replace h2o_helloworld with model_name_for_json
			meta.put("name", name); // name of the zip file
			meta.put("runtime", rnt);
			meta.put("methods", met);

			JSONObject tra = new JSONObject();
			tra.put("description", "transform");
			tra.put("input", "DataFrame");
			tra.put("output", "Prediction");
			met.put("transform", tra);

			System.out.println(meta.toString());

			try {
				// Create a new FileWriter object
				FileWriter fileWriter = new FileWriter("metadata.json");

				// Writting the jsonObject into sample.json
				fileWriter.write(meta.toString());
				fileWriter.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		} catch (JSONException e) {
			logger.error(e.getMessage());
		}

	}

	// Restful service to push the model to onboarding server
	public static void pushModel(String url, String modelFilePath, String metadataFilePath, File protoFile,
			String token) {
		HttpClient httpclient = null;
		try {

			logger.info("Uploading model to " + url);
			String boundary = "--" + UUID.randomUUID().toString();

			httpclient = new DefaultHttpClient();

			HttpPost post = new HttpPost(url);

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();

			File modelFile = new File(modelFilePath);
			File metadataFile = new File(metadataFilePath);

			builder.setBoundary(boundary);
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

			builder.addBinaryBody("model", new FileInputStream(modelFile), ContentType.MULTIPART_FORM_DATA,
					modelFile.getName());
			builder.addBinaryBody("metadata", new FileInputStream(metadataFile), ContentType.MULTIPART_FORM_DATA,
					metadataFile.getName());
			builder.addBinaryBody("schema", new FileInputStream(protoFile), ContentType.MULTIPART_FORM_DATA,
					protoFile.getName());

			HttpEntity entity = builder.build();
			post.setEntity(entity);
			post.setHeader("Authorization", token);

			HttpResponse response = httpclient.execute(post);

			if (response.getStatusLine().getStatusCode() != 200 && response.getStatusLine().getStatusCode() != 201) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			}

			logger.info("Model On-boarded successfully on " + url);

		} catch (Exception e) {
			logger.error(e.getMessage());

		} finally {
			httpclient.getConnectionManager().shutdown();
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