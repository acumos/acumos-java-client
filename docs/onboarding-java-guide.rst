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

=============================
Acumos Java Client User Guide
=============================

The Acumos Java Client Library command line utility is used to on-board H2o.ai, Generic Java and
Spark Java models. This library creates necessary artifacts and pushes them to the on-boarding server
for model runners able to use them.

High-Level Flow
===============

#) The Modeler creates a model in H2o and exports it in the MOJO model format (.zip file). For Generic Java and Spark the Modeler creates a model and exports it in the .jar format.
#) The Modeler runs the JavaClient jar, which creates a Protobuf (default.proto) file for the Model, creates the required metadata.json file and an artifact called modelpackage.zip.
#) Depending on the choice of the Modeler, he can manually upload these generated artifacts to the Acumos Marketplace via its Web interface. This is Web-based on-boarding. We will see how to do this in this article.
#) Or the Java client library itself, on-boards the model into the on-boarding server if the modeler provides the on-boarding server URL. This is CLI-based on-boarding.

The Model Runner provides a wrapper around the ML model, packages it as a containerized microservice and
exposes a predict method as a REST endpoint. When the model is onboarded and deployed, this method (REST
endpoint) can then be called by other external applications to request predictions off of the model.

Please refer to the `Acumos Java Client Installation and Maintenance Guide <instalation-and-maintenance-guide.html>`_ prior to the followings.

Create your modeldump.zip file & use CLI on-boarding
====================================================

It exists two ways to onboard a model, by CLI (command Line Interface) and by Web (drag and drop directly
on the Acumos portal Web onboarding page). If you used CLI you need to be authenticated thanks to the apitoken
available on the user settings in the acumos portal. this token is in the form : f4dfd3z31fz3f1z3fff. You need
to used it combined with your acumos login like that : "your_acumos_login:f4dfd3z31fz3f1z3fff". This string 
needs to copied in a token file(txt file) under supporting foler.

Changes in application.properties file

1.	Pass the model file name
2.	Model Type - H/G/S  (H for H2O model, G for Generic java model, S for Java Spark)
3.	push_url – respective url on which user wants to onboard the model
5.	token_type – apitoken (for api based token authentication)
6.	token_file – Path where token file is present
7.	dump_path – path where modeldump needs to be save
8.	isMicroservice - True/False based on user's choice to generate microservice
9.	h2oModelMethod = predict,classify (modeler can pass mulptiple method methods for H2O model)

For push_url, please refer to `on-boarding API user guide <../../on-boarding/docs/api-docs.html>`_

Pass the following argument as an input to run the JavaClient.jar file

.. code-block:: python

       java -jar java_client-5.0.1.jar <SupportingFolderPath> <ModelName> <inputCSVFile> <OnboardingType> <deploy>

1.	SupportingFolderPath – pass the path where model file, application.properties, license.json and data file are present
2.	ModelName – The name of model file
3.	inputCSVFile – name of the data file present in supporting folder, optional in case if you have .proto file(OR moderler can generate
        proto file for Java Generic and Java Spark models by inspecting the model)
4.	OnboardingType - pass "WebOnboard" if needed modeldump for webbased onboarding. For onboarding through client keep it blank
5.      deploy(Optional) - pass "true" if you want to deploy your model automatically after the microservice creation. Only available since
        Elpis release. This functionality (automatic deployement) required Acumos internal setting and External settings on your own Jenkins server
        please refers to `Model deployment page <https://wiki.acumos.org/display/MM/Model+Deployment+project>`

If you used CLI-based onboarding, you don't need to perform the steps outlined just below. The Java client has done it for you.
You will see a message on the terminal that states the model onboarded successfully. This message will give you Acumos docker
URI of your model, that you can use to load the Acumos docker image in your own docker registry.

Onboarding to the Acumos Portal : Web On-boarding
=================================================

If you have set the "OnboardingType" parameter to "WebOnboard", you must complete the following steps:

#. After you run the client, you will see a modeldump.zip file generated in the same folder where we ran the Java Client for.
#. Upload this file in the Web based interface (drap and drop).
#. You will be able to see a success message in the Web interface. you will be able to see a success method in the Web interface.

The needed TOSCA artifacts and docker images are produced when the model is onboarded to the Portal.
You can now see, rate, review, comment, collaborate on your model in the Acumos marketplace. When
requested and deployed by a user, your model runs as a dockerized microservice on the infrastructure
of your choice and exposes a predict method as a REST endpoint. This method can be called by other
external applications to request predictions of your model.

Tutorial
========

Please have a look on the "Soup to nuts" page under Acumos wiki

https://wiki.acumos.org/display/MM/Soup+to+Nuts+%3A+A+step+by+step+approach+to+on-board+your+model
