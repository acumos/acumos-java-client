.. ===============LICENSE_START============================================================
.. Acumos CC-BY-4.0
.. ========================================================================================
.. Copyright (C) 2017-2018 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
.. ========================================================================================
.. This Acumos documentation file is distributed by AT&T and Tech Mahindra
.. under the Creative Commons Attribution 4.0 International License (the "License");
.. you may not use this file except in compliance with the License
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

=====================================================
Acumos Java Client Installation and Maintenance Guide
=====================================================

Prerequisites
=============

- Java 8 or Java 9
- Download the following Released components:

    - `Java Client <https://nexus.acumos.org/content/repositories/releases/org/acumos/acumos-java-client/java_client/>`_ download the latest version of the JavaClient jar
    - `Generic Model Runner <https://nexus.acumos.org/content/repositories/releases/org/acumos/generic-model-runner/h2o-genericjava-modelrunner/>`_ download the latest version of the h20-genericjava-modelrunner


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
           h2oModelMethod = predict,classify,transform

           # Linux some properties are specific to java generic models

           # The plugin_root path has to be outside of ModelRunner root or the code won't work 
           # Default proto java file, classes and jar
           # DatasetProto.java will be in $plugin_root\src
           # DatasetProto$*.classes will be in $plugin_root\classes
           # pbuff.jar will be in $plugin_root\classes

           plugin_root=/tmp/plugins


    #. modelConfig.properties - Add this file only in case of Generic Java model onboarding. This file contains the modelMethod and modelClassName of the model. Modeler can pass more thqan one model merhod in modelMethod field.

        .. code-block:: python

            modelClassName=org.acumos.ml.XModel
            modelMethod=predict,classify,transform

    #. License Profile File - If you have a license profile associated with your model, Add it in the supporting folder in the following form : license.json. If the license profile file extension is not 'json' the license on-boarding will not be possible and if the name is not 'license' Acumos will rename your license profile file as license.json and you will see your license profile file named as license.json in the artifacts table. If you upload a new version of your license profile after on-boarding, a number revision will be added to the name of your license profile file like : "license-2.json". To help user create the license profile file expected by Acumos a license user guide is available here : `License Profile Editor user guide <../../license-manager/docs/user-guide-license-profile-editor.html>`_

