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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
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
		String serviceUrl = null, authUrl = null;

		try {
			boolean modelVal;
			File model = null;
			File servicejar = null;
			String token = null, tokenType = null, tokenFilePath = null;
			int count = 1;
			String inputCSVFile = null;
			String modelType = null, path = null, modelName = null, onboardingType = null, dumpPath = null;

			logger.info("Length : {} ", args.length);
			path = args[1];

			//String projectPath = System.getProperty("user.dir");
			Properties prop = new Properties();
			InputStream input = null;
			try {
				input = new FileInputStream(new File(path, "application.properties"));
				prop.load(input);
				serviceUrl = prop.getProperty("push_url");
				authUrl = prop.getProperty("auth_url");
				tokenType = prop.getProperty("token_type");
				tokenFilePath = prop.getProperty("token_file");
				dumpPath = prop.getProperty("dump_path");

				// get the model name from command line
				modelType = args[0];
				modelName = args[2];

				if (args.length < 5) {

					String temp = args[3];
					if (temp.contains("csv")) {
						inputCSVFile = temp;
					} else {
						onboardingType = temp;
					}

				} else {
					inputCSVFile = args[3];
					onboardingType = args[4];
				}

				logger.info("Model type is {}", modelType);
			} catch (IOException e) {
				logger.error(e.getMessage());
			}

			// boolean valid = false;

			token = client.checkToken(tokenType, tokenFilePath, authUrl);
			
			if (token!=null && !token.isEmpty()) {

			modelVal = client.isValidWord(modelName);

			if (modelVal == true ) {

				switch (modelType) {
				case "H":
					servicejar = new File(path + File.separator + "H2OModelService.jar");
					model = new File(path + File.separator + modelName + ".zip");
					break;
				case "G":
					servicejar = new File(path + File.separator + "GenericModelService.jar");
					model = new File(path + File.separator + modelName + ".jar");
					break;
				default:
					logger.info("Invalid model type");
					break;
				}
				try {

					File congif = client.getConfigFile(path);

					File appFile = client.getAppFile(path);

					// Call generateModelService input is modelService.jar
					client.generateModelService(model, servicejar, congif, modelType, appFile);

					// Generate Protobuf file
					File protof = client.generateProtobuf(path, inputCSVFile, modelType, modelName);

					// Generate Metadata.json file
					client.generateMetadata(modelType, modelName);

					if (onboardingType != null && onboardingType.equalsIgnoreCase("webonboard")) {
						logger.info("Creating modeldump for web based onboarding");
						List<String> files = new ArrayList<>();
						files.add(new File("modelpackage.zip").getAbsolutePath());
						files.add(new File("metadata.json").getAbsolutePath());
						files.add(protof.getAbsolutePath());
						client.zipFile(files, "modeldump.zip");
						FileUtils.copyFileToDirectory(new File("modeldump.zip"), new File(dumpPath));
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
				logger.error(
						"Model name should not contain special character or spaces. Don't include the file extensions.");
				throw new Exception("Model name should not contain special character or spaces. Don't include the file extensions");
			}
			} else {
				logger.error(
						"Invalid authentication, Update the Tokenfile with valid token and provide valid Token Type");
				throw new Exception("Invalid authentication, Update the Tokenfile with valid token and provide valid Token Type");
			}

		} catch (ArrayIndexOutOfBoundsException ae) {
			logger.error(ae.toString());
		} catch (RuntimeException re) {
			logger.error(re.toString());
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
	public void generateModelService(File model, File service, File congif, String modelType, File appFile) {

		// Pack modelService.jar and model.jar into zip file
		List<String> files = new ArrayList<String>();
		files.add(model.getAbsolutePath());
		files.add(service.getAbsolutePath());
		files.add(appFile.getAbsolutePath());
		if (modelType.equals("G")) {
			files.add(congif.getAbsolutePath());
		}
		zipFile(files, "modelpackage.zip");
	}

	// Generate the protobuf file from sample data file
	public File generateProtobuf(String path, String inputCSVFile, String modelType, String modelName)
			throws IOException {
		logger.info("Generating proto file");
		logger.info("Model type is {}", modelType);
		File protoFile = null;
		String inputPath = null;
		String h2oModelFullPath = null;

		if (inputCSVFile == null) {
			logger.info("The input csv file is null");
			protoFile = new File(path + File.separator + "default.proto");
			return protoFile;
		} else {
			inputPath = path + File.separator + inputCSVFile;
			h2oModelFullPath = path + File.separator + modelName + ".zip";

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

	// Generate the modelConfig.properties file from directory
	public File getConfigFile(String path) throws FileNotFoundException {

		logger.info("Get modelConfig.properties");
		File conFile = null;
		conFile = new File(path + File.separator + "modelConfig.properties");
		return conFile;
	}

	// Generate the application.properties file from directory
	public File getAppFile(String path) throws FileNotFoundException {

		logger.info("Get application.properties");
		File appFile = null;
		appFile = new File(path + File.separator + "application.properties");
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

			meta.put("schema", "acumos.schema.model:0.4.0");
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

			logger.info("Model On-boarded successfully on: " + url);

		} catch (Exception e) {
			logger.error(e.getMessage());

		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}

	public String checkToken(String tokenType, String tokenFilePath, String authUrl) {

		JSONObject obj = new JSONObject();
		JSONObject obj1 = new JSONObject();
		String token = null;
		
		logger.info("Token Type is: " + tokenType);

		if (tokenType.equalsIgnoreCase("apitoken")) {

			if (tokenFilePath != null && !tokenFilePath.isEmpty()) {

				BufferedReader reader;
				try {
					reader = new BufferedReader(new FileReader(tokenFilePath));

					StringBuilder stringBuilder = new StringBuilder();
					String line = null;
					String ls = System.getProperty("line.separator");
					while ((line = reader.readLine()) != null) {
						stringBuilder.append(line);
						stringBuilder.append(ls);
					}
					// delete the last new line separator
					stringBuilder.deleteCharAt(stringBuilder.length() - 1);
					reader.close();
					token = stringBuilder.toString();

				} catch (FileNotFoundException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}
			} else {

				System.out.println("Please fetch API Token from Acumos Portal Marketplace and save it");
				System.exit(1);
			}

		} else if (tokenType.equalsIgnoreCase("jwttoken")) {

			int count = 1;

			try {
				while (count < 4) {
					Scanner sc = new Scanner(System.in);
					System.out.println("Enter Username");
					String username = sc.next();
					// sc.close();

					System.out.println("Please enter the password");
					Scanner sc1 = new Scanner(System.in);
					String passwd = sc1.next();
					// sc1.close();

					obj1.put("username", username);
					obj1.put("password", passwd);
					obj.put("request_body", obj1);

					token = loginUser(obj.toString(), authUrl);

					if (token != null) {
						try {
							// Create a new FileWriter object
							FileWriter fileWriter = new FileWriter("tokenfile.txt");
							fileWriter.write(token);
							fileWriter.close();
							FileUtils.copyFileToDirectory(new File("tokenfile.txt"), new File(tokenFilePath));
							count++;
							break;
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
					} else {
						count++;
					}
				}
				if (count == 4) {
					System.exit(1);
				}
			} catch (IOException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return token;
	}
  }