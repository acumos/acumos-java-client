package org.acumos.javah20client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JavaGenericModelImpl extends AbstractClientController {

	// Get the model method
	public List<String> returnModelMethodList(String path) {

		List<String> modelNameList = new ArrayList<String>();
		try {

			Properties prop = new Properties();

			InputStream inputModelConfig;

			inputModelConfig = new FileInputStream(new File(path, "modelConfig.properties"));

			prop.load(inputModelConfig);
			String modelMethod = prop.getProperty("modelMethod");

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
		String path = supportingPath + File.separator + "GenericModelService.jar";
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
			rnt.put("name", "javageneric");
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

		if (inputCSVFile == null) {

			protoFile = new File(path + File.separator + "default.proto");

			if (protoFile.exists()) {
				logger.info("The input csv file is null, so using User provided proto file.");
				return protoFile;
			} else {

				logger.info("Generating proto file by inspecting model file");

				InputStream inputModelConfig = null;
				InputStream app_prop = null;
				Properties prop = new Properties();
				List<String> inputDataTypeList = new ArrayList<String>();
				List<String> outputDataTypeList = new ArrayList<String>();
				List<String> inputFieldNameList = new ArrayList<String>();
				List<String> matchingMethodList = new ArrayList<String>();
				String modelClassName = null;
				String protoInputType = null;

				JarFile jarFile;
				Class<?> cls = null;

				try {
					jarFile = new JarFile(path + File.separator + modelName + ".jar");
					Enumeration<JarEntry> e = jarFile.entries();
					URL[] urls = { new URL("jar:file:" + path + File.separator + modelName + ".jar!/") };
					URLClassLoader cl = URLClassLoader.newInstance(urls);

					while (e.hasMoreElements()) {
						JarEntry je = e.nextElement();
						if (je.isDirectory() || !je.getName().endsWith(".class")) {
							continue;
						}
						// -6 because of .class
						String className = je.getName().substring(0, je.getName().length() - 6);
						className = className.replace('/', '.');

						inputModelConfig = new FileInputStream(new File(path, "modelConfig.properties"));
						prop.load(inputModelConfig);
						modelClassName = prop.getProperty("modelClassName");

						String[] classList = null;
						if (modelClassName.contains(",")) {
							classList = modelClassName.trim().replaceAll("\\s+", "").split(",");
						} else {
							classList = new String[] { modelClassName };
						}
						for (String classs : classList) {
							if (className.equals(classs)) {
								logger.debug("ClassName in model --> " + className);
								cls = cl.loadClass(className);
							}
						}

					}

					Properties property = new Properties();
					app_prop = getClass().getClassLoader().getResourceAsStream("application.properties");
					property.load(app_prop);
					protoInputType = property.getProperty("proto_input_type");

					String[] protoInputTypeList = null;
					if (protoInputType.contains(",")) {
						protoInputTypeList = protoInputType.trim().replaceAll("\\s+", "").split(",");
					} else {
						protoInputTypeList = new String[] { protoInputType };
					}

					List<String> protoInputTypeArrayList = Arrays.asList(protoInputTypeList);

					for (Method meth : cls.getMethods()) {
						for (String method : modelNameList) {
							if (meth.getName().equals(method)) {
								for (Parameter p : meth.getParameters()) {
									if (protoInputTypeArrayList.contains(p.getType().getSimpleName())) {
										logger.debug(" Input parameter --> " + p.getType().getSimpleName());
										inputDataTypeList.add(p.getType().getSimpleName());
										inputFieldNameList.add(meth.getName() + "Input_" + p.getName());
									} else {
										logger.error("Input Parameter ('" + p.getType().getSimpleName()
												+ "') in method '" + meth.getName() + "', is not a supported type.");
										throw new Exception(
												"Argument Not Supported. Please set supported arguments in your method.");
									}
								}
								outputDataTypeList.add(meth.getReturnType().getSimpleName());
								matchingMethodList.add(method);
							}
						}
					}
					logger.debug("Output Data Type List --> " + outputDataTypeList.toString());
					CSVToProto c = new CSVToProto();
					protoFile = c.writeToProto(modelName, modelNameList, inputDataTypeList, outputDataTypeList,
							inputFieldNameList, matchingMethodList);

				} catch (Exception e1) {
					e1.getMessage();
					e1.printStackTrace();
				}
				return protoFile;
			}
		} else {
			inputPath = path + File.separator + inputCSVFile;
			logger.info("I/P File : {} ", inputPath);
			logger.info("Generating Proto file from inputCsv file");
			logger.info("Model type is {}", modelType);

			CSVToProto c = new CSVToProto();
			protoFile = c.writeToProto(inputPath, modelName, modelNameList);
			return protoFile;
		}
	}

	// Get the model.jar file
	public void generateModelService(File model, File service, File congif, String modelType, File appFile, File sparkConf) {

		// Pack modelService.jar and model.jar into zip file
		List<String> files = new ArrayList<String>();
		files.add(model.getAbsolutePath());
		files.add(appFile.getAbsolutePath());
		files.add(congif.getAbsolutePath());
		zipFile(files, "modelpackage.zip");
	}

	//Get the model file
	public File getModelFile(String path, String modelName) {
		return new File(path + File.separator + modelName + ".jar");
	}

}
