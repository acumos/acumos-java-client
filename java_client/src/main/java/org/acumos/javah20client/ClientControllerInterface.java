package org.acumos.javah20client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.parser.ParseException;

public interface ClientControllerInterface {

	public List<String> returnModelMethodList(String modelMethod);

	public boolean isValidWord(String w);

	public String loginUser(String obj, String authUrl) throws ClientProtocolException, IOException, ParseException;

	public void zipFile(List<String> files, String name);

	public void generateModelService(File model, File service, File congif, String modelType, File appFile, File sparkConf);

	public File generateProtobuf(String path, String inputCSVFile, String modelType, String modelName,
			List<String> modelNameList) throws Exception;

	public File getConfigFile(String path) throws FileNotFoundException;

	public File getAppFile(String path) throws FileNotFoundException;

	public void generateMetadata(String modelType, String name);

	public void pushModel(String url, String modelFilePath, String metadataFilePath, File protoFile, File licenseFile,
			String token, String msFlag);

	public String checkToken(String tokenType, String tokenFilePath, String authUrl);
	
	public String getModelService(String supportingPath);
	
	public File getsparkConfigFile(String path) throws FileNotFoundException;
	
	public File getModelFile(String path, String modelName);
}
