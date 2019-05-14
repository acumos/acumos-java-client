.. ===============LICENSE_START=======================================================
.. Acumos
.. ===================================================================================
.. Copyright (C) 2017-2018 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
.. ===================================================================================
.. This Acumos documentation file is distributed by AT&T and Tech Mahindra
.. under the Creative Commons Attribution 4.0 International License (the "License");
.. you may not use this file except in compliance with the License.
.. You may obtain a copy of the License at
..
..      http://creativecommons.org/licenses/by/4.0
..
.. This file is distributed on an "AS IS" BASIS,
.. WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
.. See the License for the specific language governing permissions and
.. limitations under the License.
.. ===============LICENSE_END=========================================================

==================================
Acumos Java Client Developer Guide
==================================

Overview
========
- The Acumos Java client is part of the Acumos Tools for H2o.ai and Generic Java models.
- The Acumos Java client is a command line utility that Data Scientist runs on his local machine or wherever he has the model to onboard it into Acumos.
- Both of them, model and Acumos Java cient together, provide a way to use H2o.ai and Generic Java in the Acumos Platform.

Architecture and Design
=======================

Java Client Library:
--------------------
Allows the H2o or Generic Java model and other artifacts to become available in the onboarding server for the H2o Model runner to be able to use them.

- The Data Scientist creates his model in H2o and exports it in the MOJO model format (.zip file) using any interface (eg.Python, Flow, R) provided by H2o
- For Generic Java, the Data scientist creates his model and exports it in the .jar format.
- Data scientist runs the JavaClient jar (Available in Nexus https://nexus.acumos.org/#nexus-search;quick~java_client), which creates a Protobuf (default.proto) file for the Model, creates the required metadata.json file and an artifact called modelpackage.zip.
- Data scientist can manually upload these generated artifacts to the Acumos Marketplace via its Web interface, this is WEB on-boarding.
- Or Data scientist can use the Acumos java client to onboard  model onto the on-boarding server by providing the on-boarding server URL, this is CLI on-boarding.

Model Runner:
-------------

Allows the on-boarded Model to be run as containerized microservice and allows other external applications to use the on-boarded Model for predictions.

- Essentially, provides a wrapper around the ML model, packages it as a containerized microservice and exposes a predict method as a rest endpoint.
- When the model is on-boarded and deployed, this method (REST endpoint) can then be called by other external applications to request model's predictions.

Technology and Frameworks
=========================

- Language : Java
- Other Technologies: Google Protocol buffers, H2o.ai
- Framework : Junit

Project Resources
=================
- Gerrit repo : `acumos-java-client <https://gerrit.acumos.org/r/#/admin/projects/acumos-java-client>`_
- `Jira <https://jira.acumos.org>`_

Development Setup
=================

To run the client project,you will need the following installed on your machine.

- Java (jdk) 8 or 9
- Protoc compiler 3.4.0
- Maven
- Protobuf Java runtime 3.4.0

Data scientist can download the latest version of the Java Client jar from Nexus : https://nexus.acumos.org/#nexus-search;quick~java-client

Data scientist can download the latest version of the h2o-genericjava-modelrunner jar from  Nexus : https://nexus.acumos.org/#nexus-search;quick~runner

To clone the client library project: git clone https://gerrit.acumos.org/r/acumos-java-client

To clone the model runner project : git clone https://gerrit.acumos.org/r/generic-model-runner

To build the project, you can use : mvn clean install

To build the model runner project, refer to instructions provided in generic-model-runner folder This will give you the same h2o-genericjava-model runner mentioned earlier.

It is a Maven Project. You can clean, install, test as with any Maven project.
