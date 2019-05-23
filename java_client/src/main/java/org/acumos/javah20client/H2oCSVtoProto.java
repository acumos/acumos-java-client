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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.MojoModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class H2oCSVtoProto {

	private static Logger logger = LoggerFactory.getLogger(H2oCSVtoProto.class);

	private static final String SAMPLE_CSV_FILE_PATH = "IRIS3.csv";
	List<String> inputFields = new ArrayList<>();
	List<String> outputFields = new ArrayList<>();
	boolean modelIsSupervised = false;
	String opDatatype;

	public void getH2oModelInfo(String h2oModelFullPath, List<String> inputFields, List<String> outputFields)
			throws Exception {

		/* start prediction */
		MojoModel mojo = null;
		EasyPredictModelWrapper model = null;
		try {

			mojo = MojoModel.load(h2oModelFullPath);
			model = new EasyPredictModelWrapper(mojo);

		} // try ends

		catch (IOException ie) {
			ie.getMessage();
		} // catch ends

		String current_model_category = mojo.getModelCategory().toString();
		logger.info("model category again: {}", current_model_category);
		logger.info("isSupervised: {}", mojo.isSupervised());
		logger.info("number of input features: {}", mojo.nfeatures());
		logger.info("number of output classes: {}", mojo.nclasses());
		logger.info("model category: {}", mojo.getModelCategory());
		logger.info("model's unique identifier: {} ", mojo.getUUID());

		logger.info("Number of columns used as input for training (i.e., exclude response and offset columns). : {}",
				mojo.getNumCols());

		// Code to find the datatype of the output message
		switch (current_model_category) {
		case "Binomial":
			opDatatype = "string";
			break;

		case "Multinomial":
			opDatatype = "string";
			break;

		case "Regression":
			opDatatype = "double";
			break;

		case "Clustering":
			opDatatype = "int32";
			break;
		default:
			opDatatype = "string";
			break;
		}

		String[] allColumnNames = mojo.getNames();
		List<String> inputColumnNames = new ArrayList<String>();
		List<String> outputColumnNames = new ArrayList<String>();

		for (int i = 0; i < allColumnNames.length; i++) {
			inputColumnNames.add(allColumnNames[i]);
		}

		// NOW SPIT OUT THE COLUMNS TO BE READ PUT INTO THE .PROTO FILE OR TO BE READ
		// FROM CSV / ASSIGNED TO ROW.PUT WHILE PREDICTING. RESPONSE COLUMN IS NOT
		// IGNORED.
		logger.info("All model columns including response and offset columns");
		for (int i = 0; i < inputColumnNames.size(); i++) {
			logger.info(inputColumnNames.get(i));
		}

		// If the model is supervised, the model will have a response column. If model
		// is unsupervised, it will not have a response column. In that case we will set
		// the response column name to be "prediction"
		modelIsSupervised = mojo.isSupervised();
		if (mojo.isSupervised() == true) {
			logger.info("Index of the response column : {}", mojo.getResponseIdx());
			int responseColumnIndex = mojo.getResponseIdx();
			// there is a response column at the index 'response_column_index'. Truncate it
			// to create final columns list
			outputColumnNames.add(inputColumnNames.get(responseColumnIndex));
			inputColumnNames.remove(responseColumnIndex);
		} else {
			outputColumnNames.add("prediction");
		}

		for (int j = 0; j < inputColumnNames.size(); j++) {
			inputFields.add(inputColumnNames.get(j));
		}

		logger.info("Now printing input columns");
		for (int j = 0; j < inputColumnNames.size(); j++) {
			logger.info(inputFields.get(j));
		}

		for (int j = 0; j < outputColumnNames.size(); j++) {
			outputFields.add(outputColumnNames.get(j));
		}

		logger.info("Now printing output columns");
		for (int j = 0; j < outputColumnNames.size(); j++) {
			logger.info(outputFields.get(j));
		}

		if (mojo.isClassifier() == true) {
			logger.info("number of classes in response column : {}", mojo.getNumResponseClasses());
		}

		logger.info("isClassifier : {}", mojo.isClassifier());
		logger.info("isAutoEncoder : {}", mojo.isAutoEncoder());
		logger.info("isAutoEncoder : {}", mojo.isAutoEncoder());

		// Printing all categorical values for each predictors
		for (int i = 0; i < mojo.getNumCols(); i++) {
			String[] domainValues = mojo.getDomainValues(i);
			logger.info("Printing domain value (return domain for given column or null if column is numeric) {}",
					Arrays.toString(domainValues));
		}
		logger.info("expected size of preds array which is passed to `predict(double[], double[])` function : {}",
				mojo.getPredsSize());
	}

	public static void main(String[] args) throws IOException {

		if (SAMPLE_CSV_FILE_PATH.endsWith(".csv")) {

			// writeToProto(SAMPLE_CSV_FILE_PATH);
			logger.info("Done");
		} else {
			logger.info("Not Valid Format");
		}
	}

	public static String getProtoDataType(String value) {
		String dataType = null;

		if (value.matches("[0-9]+\\.[0-9]+")) {

			dataType = "double";
		} else if (value.matches("\\d+")) {
			dataType = "int32";
		} else if (value.matches("[Tt]rue") || value.matches("[Ff]alse") || value.matches("[Yy]es")
				|| value.matches("[Nn]o")) {
			dataType = "bool";
		} else {
			dataType = "string";
		}

		return dataType;
	}

	public static String createService(String serviceName, String inputMessageName, String outputMessageName, List<String> h2oModelNameList) {
		String template = null;
		StringBuffer rpcString = new StringBuffer();
		for(String modelMethod : h2oModelNameList) {
			outputMessageName = modelMethod+"Out";
			rpcString.append("  rpc "+modelMethod+" (DataFrame) returns ("+modelMethod+"Out);\n");
		}
		template = "service %s {\n" + rpcString + "}";
		return String.format(template, serviceName, inputMessageName, outputMessageName);
	}

	public static String createProtoHeader() {
		final String template = "syntax = \"proto3\";";
		return template;
	}

	public static String createProtoFooter() {
		return "";
	}

	public static String createMessage(final String messageName, List<String> fields, List<String> dtList,
			boolean isRepeated) {
		final String startTemplate = "message %s {\n";
		final String endTemplate = "}";

		StringBuffer sb = new StringBuffer();
		sb.append(String.format(startTemplate, messageName));
		int i = 0;
		int j = 1;
		for (String f : fields) {
			// Is repeated the right thing?
			if (isRepeated)
				sb.append("repeated ");
			// sb.append(getProtoDataType(f));
			sb.append(dtList.get(i));
			sb.append(' ');
			sb.append(f);
			sb.append(" = ");
			sb.append("" + j + ";");
			sb.append('\n');
			i++;
			j++;
		}
		sb.append(endTemplate);
		return sb.toString();
	}

	public File writeToProto(String SAMPLE_CSV_FILE_PATH, String modelName, String h2oModelFullPath, List<String> h2oModelNameList)
			throws FileNotFoundException, IOException {

		BufferedReader reader = new BufferedReader(new FileReader(SAMPLE_CSV_FILE_PATH));

		String[] header = reader.readLine().split(",");

		// Change this part according to h2o need
		try {
			getH2oModelInfo(h2oModelFullPath, inputFields, outputFields);
		} catch (Exception e) {
			System.out
					.println("Cannot get model info for the h2o model. Check project setup and/or filename supplied.");
		}

		List<String> dataTypeList = new ArrayList<>();
		List<String> outdataTypeList = new ArrayList<>();

		/*
		 * for (String h : header) { inputFields.add(h); }
		 * inputFields.remove(header[header.length - 1]);
		 * outputFields.add(header[header.length - 1]);
		 */
		String[] line = reader.readLine().split(",");

		for (String l : line) {
			// logger.info(l+" - "+getProtoDataType(l));
			dataTypeList.add(getProtoDataType(l));
		}

		// This assumes that in the csv, last column will be the target column. This may
		// not be the case. Add intelligence here.
		// Also read header.length-1 to interpret column datatype only if the model is
		// supervised.
		// If the model was unsupervised, then the header.length-1 will not be the
		// 'target' column. Infact, there will be no target column in the csv file.
		// In this case, we will set the datatype of that expected result column to be
		// simply string.
		// This will be used in the model runner as String prediction.
		if (modelIsSupervised == true) {
			// outdataTypeList.add(dataTypeList.get(header.length - 1));
			// The output message dataype will not be interpreted by looking at the model
			// considering h2o's limitation.
			outdataTypeList.add(opDatatype);
		} else
			outdataTypeList.add("string");

		reader.close();
		// Creating Proto....!

		StringBuffer sb = new StringBuffer();
		sb.append(createProtoHeader());
		sb.append('\n');

		final String inputMessageName = "DataFrameRow";
		String outputMessageName = null;
		String svcName = modelName + "Service";
		sb.append("option java_package = \"com.google.protobuf\";");
		sb.append('\n');
		sb.append("option java_outer_classname = \"DatasetProto\";");
		sb.append('\n');
		sb.append(createService(svcName, "DataFrame", outputMessageName, h2oModelNameList));
		sb.append('\n');
		for(String modelMethod : h2oModelNameList) {
			outputMessageName = modelMethod+"Out";
			sb.append(createMessage(outputMessageName, outputFields, outdataTypeList, true));
			sb.append('\n');
		}
		sb.append(createMessage(inputMessageName, inputFields, dataTypeList, false));
		sb.append('\n');
		sb.append("message DataFrame { \nrepeated DataFrameRow rows = 1;\n }");
		sb.append('\n');
		sb.append(createProtoFooter());

		logger.info(sb.toString());
		File p = new File("default.proto");

		FileWriter proto = new FileWriter(p);
		proto.write(sb.toString());
		proto.close();

		return p;
	}
}
