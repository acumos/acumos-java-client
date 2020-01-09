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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractClientController implements ClientControllerInterface {

	static Logger logger = LoggerFactory.getLogger(AbstractClientController.class);

	public static final String DOCKER_IMAGE_URI = "dockerImageUri";

	public boolean isValidWord(String w) {
		return w.matches("^[a-zA-Z0-9_-]*$");
	}

	public String loginUser(String obj, String authUrl) throws ClientProtocolException, IOException, ParseException {
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

	// Generate the application.properties file from directory
	public File getAppFile(String path) throws FileNotFoundException {

		logger.info("Get application.properties");
		File appFile = null;
		appFile = new File(path + File.separator + "application.properties");
		return appFile;
	}

	// Restful service to push the model to onboarding server
	public void pushModel(String url, String modelFilePath, String metadataFilePath, File protoFile, File licenseFile,
			String token, String msFlag) {
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

			if (licenseFile != null && licenseFile.exists()) {
				builder.addBinaryBody("license", new FileInputStream(licenseFile), ContentType.MULTIPART_FORM_DATA,
						licenseFile.getName());
			}

			HttpEntity entity = builder.build();
			post.setEntity(entity);
			post.setHeader("Authorization", token);
			post.addHeader("isCreateMicroservice", msFlag);

			HttpResponse response = httpclient.execute(post);

			if (response.getStatusLine().getStatusCode() != 200 && response.getStatusLine().getStatusCode() != 201) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			}
			//add response entity in jsonobject
			JSONObject respEntity = new JSONObject(EntityUtils.toString(response.getEntity()));

			//logging the dockeriamgeUrI
            logger.info("DockerImageUri: "+respEntity.getString(DOCKER_IMAGE_URI));

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

	// Generate the modelConfig.properties file from directory
	public File getConfigFile(String path) throws FileNotFoundException {

		logger.info("Get modelConfig.properties file");
		File conFile = null;
		conFile = new File(path + File.separator + "modelConfig.properties");

		if (!conFile.exists()) {
			throw new FileNotFoundException("modelConfig.properties for model is missing");
		}

		return conFile;
	}

	// Generate the sparkConfig.json file from directory
	public File getsparkConfigFile(String path) throws FileNotFoundException {

		logger.info("Get sparkConfig.json file");
		File sparkConFile = null;
		sparkConFile = new File(path + File.separator + "sparkConfig.json");

		if (!sparkConFile.exists()) {
			throw new FileNotFoundException("sparkConfig.json for spark model is missing");
		}

		return sparkConFile;
	}

}
