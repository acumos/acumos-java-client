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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVToProto {

	private static final String SAMPLE_CSV_FILE_PATH = "IRIS2.csv";

	public static void main(String[] args) throws IOException {

		if (SAMPLE_CSV_FILE_PATH.endsWith(".csv")) {

			// writeToProto(SAMPLE_CSV_FILE_PATH);
			System.out.println("Done");
		} else {
			System.out.println("Not Valid Format");
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

	public static String createService(String serviceName, String inputMessageName, String outputMessageName,
			List<String> modelNameList) {
		String template = "";
		StringBuffer rpcString = new StringBuffer();
		for (String modelMethod : modelNameList) {
			outputMessageName = modelMethod + "Out";
			rpcString.append("  rpc " + modelMethod + " (DataFrame) returns (" + modelMethod + "Out);\n");
		}
		template = "service %s {\n" + rpcString.toString() + "}";
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

	public static String createMessage(final String messageName, List<String> fields, List<String> dtList,
			boolean isRepeated, String modelMethod) {
		final String startTemplate = "message %s {\n";
		final String endTemplate = "}";

		StringBuffer sb = new StringBuffer();
		sb.append(String.format(startTemplate, messageName));
		int i = 0;
		int j = 1;
		for (String f : fields) {
			if (f.contains(modelMethod)) {
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
			}
			i++;
			j++;

		}
		sb.append(endTemplate);
		return sb.toString();
	}

	public File writeToProto(String SAMPLE_CSV_FILE_PATH, String modelName, List<String> modelNameList)
			throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(SAMPLE_CSV_FILE_PATH));

		String[] header = reader.readLine().split(",");

		List<String> inputFields = new ArrayList<>();
		List<String> dataTypeList = new ArrayList<>();
		List<String> outdataTypeList = new ArrayList<>();
		List<String> outputFields = new ArrayList<>();
		for (String h : header) {
			inputFields.add(h);
		}
		inputFields.remove(header[header.length - 1]);
		outputFields.add(header[header.length - 1]);

		String[] line = reader.readLine().split(",");

		for (String l : line) {
			// System.out.println(l+" - "+getProtoDataType(l));
			dataTypeList.add(getProtoDataType(l));
		}

		outdataTypeList.add(dataTypeList.get(header.length - 1));

		reader.close();
		// Creating Proto....!

		StringBuffer sb = new StringBuffer();
		sb.append(createProtoHeader());
		sb.append('\n');

		// String svcName = modelName + "Service";
		final String inputMessageName = "DataFrameRow";
		String outputMessageName = null;

		sb.append("option java_package = \"com.google.protobuf\";");
		sb.append('\n');
		sb.append("option java_outer_classname = \"DatasetProto\";");
		sb.append('\n');

		sb.append(createService(modelName, "DataFrame", outputMessageName, modelNameList));
		sb.append('\n');

		for (String modelMethod : modelNameList) {
			outputMessageName = modelMethod + "Out";
			sb.append(createMessage(outputMessageName, outputFields, outdataTypeList, true));
			sb.append('\n');
		}

		sb.append(createMessage(inputMessageName, inputFields, dataTypeList, false));
		sb.append('\n');
		sb.append("message DataFrame { \nrepeated DataFrameRow rows = 1;\n }");

		sb.append('\n');
		sb.append(createProtoFooter());

		System.out.println(sb.toString());
		File p = new File("default.proto");

		FileWriter proto = new FileWriter(p);
		proto.write(sb.toString());
		proto.close();

		return p;
	}

	public File writeToProto(String modelName, List<String> modelNameList, List<String> dataTypeList,
			List<String> outdataTypeList, List<String> inputFields, List<String> matchingMethodList)
			throws FileNotFoundException, IOException {

		List<String> outputFields = new ArrayList<>();

		for (int i = 0; i < matchingMethodList.size(); i++) {
			outputFields.add(matchingMethodList.get(i) + "Output");
		}

		// Creating Proto....!
		StringBuffer sb = new StringBuffer();
		sb.append(createProtoHeader());
		sb.append('\n');

		// String svcName = modelName + "Service";
		final String inputMessageName = "DataFrameRow";
		String outputMessageName = null;

		sb.append("option java_package = \"com.google.protobuf\";");
		sb.append('\n');
		sb.append("option java_outer_classname = \"DatasetProto\";");
		sb.append('\n');

		sb.append(createService(modelName, "DataFrame", outputMessageName, modelNameList));
		sb.append('\n');

		for (String modelMethod : modelNameList) {
			outputMessageName = modelMethod + "Out";
			sb.append(createMessage(outputMessageName, outputFields, outdataTypeList, true, modelMethod));
			sb.append('\n');
		}

		sb.append(createMessage(inputMessageName, inputFields, dataTypeList, false));
		sb.append('\n');
		sb.append("message DataFrame { \nrepeated DataFrameRow rows = 1;\n }");

		sb.append('\n');
		sb.append(createProtoFooter());

		System.out.println(sb.toString());
		File p = new File("default.proto");

		FileWriter proto = new FileWriter(p);
		proto.write(sb.toString());
		proto.close();

		return p;
	}

	/*
	 * private static String translatePmmlTypeToProtoType(final String pmmlType) {
	 * // Some have the same name if ("string".equalsIgnoreCase(pmmlType) ||
	 * "float".equalsIgnoreCase(pmmlType) || "double".equalsIgnoreCase(pmmlType))
	 * return pmmlType; if ("boolean".equalsIgnoreCase(pmmlType)) return "bool"; if
	 * ("integer".equalsIgnoreCase(pmmlType) ||
	 * "timeSeconds".equalsIgnoreCase(pmmlType) ||
	 * pmmlType.startsWith("dateDaysSince") ||
	 * pmmlType.startsWith("dateTimeSecondsSince")) return "int32"; throw new
	 * IllegalArgumentException("Unhandled PMML data type: " + pmmlType); }
	 */
}