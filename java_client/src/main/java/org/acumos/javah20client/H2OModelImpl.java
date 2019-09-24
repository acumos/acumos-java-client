package org.acumos.javah20client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class H2OModelImpl extends AbstractClientController {

	// Get the model method
	public List<String> returnModelMethodList(String path) {

		List<String> modelNameList = new ArrayList<String>();
		try {

			Properties prop = new Properties();

			InputStream inputModelConfig;

			inputModelConfig = new FileInputStream(new File(path, "application.properties"));

			prop.load(inputModelConfig);
			String modelMethod = prop.getProperty("h2oModelMethod");

			String[] modelNameArray = new String[] {};

			if (modelMethod != null) {
				if (modelMethod.contains(",")) {
					modelNameArray = modelMethod.split(",");
					modelNameList = Arrays.asList(modelNameArray);
				} else {
					modelNameList.add(modelMethod);
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return modelNameList;
	}
	
	//Get the model service jar file
	public String getModelService(String supportingPath)
	{
		String path = supportingPath + File.separator + "H2OModelService.jar";
		return path;
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
			rnt.put("name", "h2o");
			rnt.put("toolkit", "H2O");
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

	// Generate the protobuf file from sample data file
	public File generateProtobuf(String path, String inputCSVFile, String modelType, String modelName,
			List<String> modelNameList) throws Exception {
		logger.info("Generating proto file");
		File protoFile = null;
		String inputPath = null;
		String h2oModelFullPath = null;

		if (inputCSVFile == null) {
			logger.info("The input csv file is null, so using User provided proto file.");
			protoFile = new File(path + File.separator + "default.proto");
			return protoFile;
		} else {
			inputPath = path + File.separator + inputCSVFile;
			h2oModelFullPath = path + File.separator + modelName + ".zip";

			logger.info("I/P File : {} ", inputPath);
			logger.info("Generating Proto file from inputCsv file");
			logger.info("Model type is {}", modelType);

			logger.debug("Entered h2o protobuf generation call");
			H2oCSVtoProto c = new H2oCSVtoProto();
			protoFile = c.writeToProto(inputPath, modelName, h2oModelFullPath, modelNameList);
			return protoFile;
		}
	}
	
	// Get the model.jar file
	public void generateModelService(File model, File service, File congif, String modelType, File appFile, File sparkConf) {

		// Pack modelService.jar and model.jar into zip file
		List<String> files = new ArrayList<String>();
		files.add(model.getAbsolutePath());
		files.add(service.getAbsolutePath());
		files.add(appFile.getAbsolutePath());
		zipFile(files, "modelpackage.zip");
	}
	
	//Get the model file
	public File getModelFile(String path, String modelName) {
		return new File(path + File.separator + modelName + ".zip");
	}

}
