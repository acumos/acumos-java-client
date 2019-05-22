.. ===============LICENSE_START============================================================
.. Acumos CC-BY-4.0
.. ========================================================================================
.. Copyright (C) 2017-2018 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
.. ========================================================================================
.. This Acumos documentation file is distributed by AT&T and Tech Mahindra
.. under the Creative Commons Attribution 4.0 International License (the "License");
.. you may not use this file except in compliance with the License.
.. You may obtain a copy of the License at
..
.. http://creativecommons.org/licenses/by/4.0
..
.. This file is distributed on an "AS IS" BASIS,
.. WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
.. See the License for the specific language governing permissions and
.. limitations under the License.
.. ===============LICENSE_END====================================================================
.. NOTE: THIS FILE IS LINKED TO FROM THE DOCUMENTATION PROJECT
.. IF YOU CHANGE THE LOCATION OR NAME OF THIS FILE, YOU MUST UPDATE THE INDEX IN THE DOCS PROJECT

============================
Java Model on-boarding guide
============================

The Acumos Java Client Library command line utility is used to on-board H2o.ai and Generic Java models. This library creates artifacts from an H2o or Generic Java model and pushes the artifacts to the on-boarding server for the H2o Model runner to be able to use them.

High-Level Flow
===============

#) The Modeler creates a model in H2o and exports it in the MOJO model format (.zip file) using any interface (eg.Python, Flow, R) provided by H2o. For Generic Java, the Modeler creates a model and exports it in the .jar format.
#) The Modeler runs the JavaClient jar, which creates a Protobuf (default.proto) file for the Model, creates the required metadata.json file and an artifact called modelpackage.zip.
#) Depending on the choice of the Modeler, he can manually upload these generated artifacts to the Acumos Marketplace via its Web interface. This is Web-based on-boarding. We will see how to do this in this article.
#) Or the Java client library itself, on-boards the model onto the on-boarding server if the modeler provides the on-boarding server URL. This is CLI-based on-boarding.

The Model Runner provides a wrapper around the ML model, packages it as a containerized microservice and exposes a predict method as a REST endpoint. When the model is onboarded and deployed, this method (REST endpoint) can then be called by other external applications to request predictions off of the model.


Prerequisites
=============

- Java 8 or Java 9
- Download the following Released components:

    - `Java Client <https://nexus.acumos.org/#nexus-search;quick~java-client>`_ v2.0.0 (java_client-2.0.0.jar)
    - `Generic Model Runner <https://nexus.acumos.org/#nexus-search;h2o-genericjava-modelrunner>`_ v2.2.3 (h2o-genericjava-modelrunner-2.2.3.jar)


Preparing to On-Board your H2o or a Generic Java Model
======================================================
a. Place the Java Client jar in one folder locally. This is the folder from which you intend to run the jar. After the jar runs, the created artifacts will also be available in this folder. You will use some of these artifacts if you are doing Web-based onboarding. We will see this later.

b. Prepare a supporting folder with the following contents. This folder will contain items that will  be used as input for the java client jar.

    #. Models - In case of H2o, your model will be a MOJO zip file.  In case of Generic Java, the model will be a jar file.
    #. Model runner or Service jar - For H2O rename h2o-genericjava-modelrunner-2.2.3.jar (previously downloaded) to H2OModelService.jar for H20 or to GenericModelService.jar for Java model and Place it in the supporting folder.
    #. CSV file used for training the model - Place the csv file (with header having the same column names used for training but without the quotes (“ ”) ) you used for training the model here. This is used for autogenerating the .proto file. If you don’t have the .proto file, you will have to supply the .proto file yourself in the supporting folder. Make sure you name it default.proto.
    #. default.proto - This is only needed  If you don't have sample csv data for training, then you will have to provide the proto file yourself. In this case, Java Client cannot autogenerate the .proto file. You will have to supply the .proto file yourself in the supporting folder. Make sure you name it default.proto Also make sure, the default.proto file for the model is in the following format. You need to appropriately replace the data and datatypes under DataFrameRow and Prediction according to your model.

        .. code-block:: python

           syntax = "proto3";
           option java_package = "com.google.protobuf";
           option java_outer_classname = "DatasetProto";

           message DataFrameRow {
           string sepal_len = 1;
           string sepal_wid = 2;
           string petal_len = 3;
           string petal_wid = 4;
           }
           message DataFrame {
                       repeated DataFrameRow rows = 1;
           }
           message Prediction {
                       repeated string prediction= 1;
           }

           service Model {
           rpc transform (DataFrame) returns (Prediction);
           }

    #. application.properties file - Mention the port number on which the service exposed by the model will finally run on. The push_url and auth_url are used only by CLI on-boarding and depend of your own Acumos installation, you can retrieve them on your Acumos portal in the ON-BOARDING MODEL page.

        .. code-block:: python

           ###
           # ===============LICENSE_START=======================================================
           # Acumos
           # ===================================================================================
           # Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
           # ===================================================================================
           # This Acumos software file is distributed by AT&T and Tech Mahindra
           # under the Apache License, Version 2.0 (the "License");
           # you may not use this file except in compliance with the License.
           # You may obtain a copy of the License at
           #
           #      http://www.apache.org/licenses/LICENSE-2.0
           #
           # This file is distributed on an "AS IS" BASIS,
           # WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
           # See the License for the specific language governing permissions and
           # limitations under the License.
           # ===============LICENSE_END=========================================================
           ###

           server.contextPath=/modelrunner
           server.port=8336

           spring.http.multipart.max-file-size=100MB
           spring.http.multipart.max-request-size=100MB

           # Linux version
 
           #default_model=/models/model.jar
           default_model=/models/Generic15.jar
           default_protofile=/models/default.proto

           logging.file = ./logs/modelrunner.log 

           # The value of model_type can be H or G
           # if model_type is H, then the /predict method will use H2O model; otherwise, it will use generic Model
           # if model_type is not present, then the default is H

           model_type=H
           model_config=/models/modelConfig.properties
           isMicroservice=true

           push_url = http://cognita-dev1-vm01-core.eastus.cloudapp.azure.com:8090/onboarding-app/v2/models
           auth_url = http://cognita-dev1-vm01-core.eastus.cloudapp.azure.com:8090/onboarding-app/v2/auth
           token_type = jwttoken
           #token_file = D:/js00353493/ATT/Cognita/model/H2O/model/tokenfile.txt
           token_file = D:/Cognita/model/JavaGeneric/supporting
           dump_path = D:/Cognita/model/JavaGeneric/dump
           isMicroservice = true

           # Linux some properties are specific to java generic models

           # The plugin_root path has to be outside of ModelRunner root or the code won't work 
           # Default proto java file, classes and jar
           # DatasetProto.java will be in $plugin_root\src
           # DatasetProto$*.classes will be in $plugin_root\classes
           # pbuff.jar will be in $plugin_root\classes

           plugin_root=/tmp/plugins


    #. modelConfig.properties - Add this file only in case of Generic Java model onboarding. This file contains the modelMethod and modelClassName of the model.

        .. code-block:: python

            modelClassName=org.acumos.ml.XModel
            modelMethod=predict

    #. License File - If you have a license associated with your model, Add it in the supporting folder in the following form : license.json. If the license file extension is not 'json' the license on-boarding will not be possible and if the name is not 'license' Acumos will rename your license file as license.json and you will see your license file named as license.json in the artifacts table. If you upload a new version of your license after on-boarding, a number revision will be added to the name of your license file like : "license-2.json". To help user create the license file expected by Acumos a license editor is available on the web : `Acumos license editor <https://acumos-license-editor.stackblitz.io/#/>`_


Create your modeldump.zip file
==============================

It exists two ways to onboard a model, by CLI (command Line Interface) and by Web (drag and drop directly on the Acumos portal Web onboarding page). If you used CLI you need to be authenticated, currently it exists two ways to be authenticated : authentication by jwt token or authentication by api token. The jwt token is provided by the auth_url API while the api token is available on the acumos portal in the user setings. We strongly recommend to use api token as the jwt token method will be disable.

Changes in application.properties file

1.	Pass the model file name
2.	Model Type - H or G  (H for H2O model and G for Generic java model)
3.	push_url – respective url on which user wants to onboard the model
4.	auth_url – auth url  (for jwt token authentication)
5.	token_type – apitoken (for api based token authentication),jwttoken(for jwt token based authentication)
6.	token_file – Path where token file is present
7.	dump_path – path where modeldump needs to be save
8.      isMicroservice - True/False based on user's choice to generate microservice

Pass the following argument as an input to run the JavaClient.jar file

1.	modelType – H for H2O model and G for Generic java model
2.	SupportingFolderPath – pass the path where modelrunner, model file, application.properties, license.json and data file are present
3.	ModelName – The name of model file
4.	DataFile – name of the data file present in supporting folder(optional in case if you have .proto file)
5.	OnboardingType - pass "WebOnboard" if needed modeldump for webbased onboarding. For onboarding through client keep it blank(Optional)

    java -jar java_client-2.0.0.jar <modelType> <SupportingFolderPath> <ModelName> <inputCSVFile> <OnboardingType>

If you used CLI-based onboarding, you don't need to perform the steps outlined just below. The Java client has done it for you. You will see a message on the terminal that states the model onboarded successfully.

Onboarding to the Acumos Portal
===============================

- If you used CLI-based onboarding, you don't need to perform the steps outlined just below. The Java client has done it for you. You will see a message on the terminal that states the model onboarded successfully.
- If you use Web-based onboarding, you must complete the following steps:

#. After you run the client, you will see a modeldump.zip file generated in the same folder where we ran the Java Client for.
#. Upload this file in the Web based interface (drap and drop).
#. You will be able to see a success message in the Web interface. you will be able to see a success method in the Web interface.

The needed TOSCA artifacts and docker images are produced when the model is
onboarded to the Portal. You and your teammates can now see, rate, review,
comment, collaborate on your model in the Acumos marketplace. When requested
and deployed by a user, your model runs as a dockerized microservice
on the infrastructure of your choice and exposes a predict method as a REST
endpoint. This method can be called by other external applications to request
predictions off of your model.

