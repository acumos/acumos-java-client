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
- It is 1 part of the Acumos Tools for H2o.ai and Generic Java models.
- The Java client is a command line utility that the Modeller/Onboarder/ ML expert/Data Scientist runs on his local machine or wherever he has the model to onboard it into Acumos.
- Both of them together provide a way to use H2o.ai and Generic Java in the Acumos Platform. 


 
 
Architecture and Design
=======================

Java Client Library:
--------------------
Allows the H2o or Generic Java model and other artifacts to become available in the onboarding server for the H2o Model runner to be able to use them.

- The Modeller/Onboarder/ ML expert/Data Scientist creates his model in H2o and exports it in the MOJO model format (.zip file) using any interface (eg.Python, Flow, R) provided by H2o
- For Generic Java, the Modeller/Onboarder/ ML expert creates his model and exports it in the .jar format.
- He runs the JavaClient jar (from Nexus https://nexus.acumos.org/#nexus-search;quick~java_client), which creates a Protobuf (default.proto) file for the Model, creates the required metadata.json file and an artifact called modelpackage.zip.
- Depending on the choice of the modeler, he can manually upload these generated artifacts to the Acumos Marketplace via its Web interface. This is Web-based onboarding. We will see how to do this in this article.
- Or the Java client library itself, onboards the model onto the onboarding server if the modeler provides the onboarding server URL. This is CLI-based onboarding. We will also see how to do this in this article.

Model Runner:

 Allows the onboarded Model to be run as containerized microservice and allows other external applications to use the onboarded Model for predictions.

- Essentially, provides a wrapper around the ML model, packages it as a containerized microservice and exposes a predict method as a rest endpoint.
- When the model is onboarded and deployed, this method (REST endpoint) can then be called by other external applications to request predictions off of the model.

     
Technology and Frameworks
=========================

- Languages: Java
- Other Technologies: Google Protocol buffers, H2o.ai
- Frameworks: Junit.
 
 
Project Resources
=================
- Gerrit repo: acumos-java-client
- `Jira <https://jira.acumos.org>`_  acumos-java-client


Development Setup
=================
----------------------------------------------------- 
For the Modeller/Onboarder/ ML expert/Data Scientist:
-----------------------------------------------------
You will need the jars from the above 2 projects:

You can download the Java Client (ie. executable jar of gerrit repo - acumos-java-client project) from Nexus. 
Go to https://nexus.acumos.org/#nexus-search;quick~java-client and download the latest version of the Java client jar.

You can download the h2o-genericjava-modelrunner (ie. executable jar of gerrit repo - generic-model-runner project) from Nexus. 
Go to https://nexus.acumos.org/#nexus-search;quick~runner and download the latest version of the h2o-genericjava-modelrunner jar.

To clone the client library project:

git clone https://gerrit.acumos.org/r/acumos-java-client


To run the client project,you will need the following installed on your machine.
- Java (jdk) 1.8
- Protoc compiler 3.4.0
- Maven
- Protobuf Java runtime 3.4.0

To build the project, you can use:

mvn clean install

This will give you the same Java Client jar mentioned earlier.

To clone the model runner project:

git clone https://gerrit.acumos.org/r/generic-model-runner

To build the model runner project, refer to instructions provided in generic-model-runner folder
This will give you the same h2o-genericjava-modelrunner mentioned earlier.


You must have the following installed on your machine-

- Java 1.8
- Maven

It is a Maven Project. You can clean, install, test as with any Maven project.
