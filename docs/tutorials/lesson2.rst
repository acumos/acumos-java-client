Command and Parameters:

Sample command to run Java based models is – 

java -jar JavaClient.jar <ServerUrl> H <PathOfSupportingFolder> <modelname> <username> <password>

The parameters to run the client jar are: 

1. Onboarding url / Folder path 
2. Model Type - either H for H2o or G for Generic Java 
3. Folder path having default.proto, your model, application.properties file, ModelConfig.properties and protobuf-java-3.4.0.jar files
4. Name of the model (This must match the name of your model file. Exclude the file extension)
5. Username
6. Password

Note: In case of web based on boarding do not pass the username and password. And in First parameter rather than passing the url, pass the folder path at which you want to dump the model files.

File Location: 

Create a runnable client jar from below project:
https://gerrit.acumos.org/r/acumos-java-client/

If you are using H2o, you will need default.proto, model, application.properties, ModelConfig.properties files.
These Files are at below location:
https://gerrit.acumos.org/r/generic-model-runner/h2o_distributable

To get the model service jar file build the below project code
https://gerrit.acumos.org/r/generic-model-runner

If you are using Generic Java, you will need default.proto, model, application.properties, ModelConfig.properties files.
These Files are at below location:
https://gerrit.acumos.org/r/generic-model-runner/generic_distributable


To get the model service jar file build the below project code
https://gerrit.acumos.org/r/generic-model-runner

Place the client jar along with your modelService jar file.
Place the default.proto, model, application.properties, ModelConfig.properties and protobuf-java-3.4.0.jar files in a supporting folder. 

Points to Note:
If modeler want to change the name of the model.

H2o:
1.	Change the name of the model.zip file
2.	Change the name of modelService.jar file (ex – If model name is “abc”, the modelService name should be “abcSevice.jar”)
3.	Change the model name in the application.properties file at below place
# Linux version
rel_model_zip=/models/ModelName
4.	Change the model name at service message in default.proto file
service ModelName {
  rpc transform (DataFrame) returns (Prediction);
}
5.	And pass the model name as 4th argument in the command

Generic:
1.	Change the name of the model.zip file
2.	Change the model name in the application.properties file at below place
# Linux version
rel_model_zip=/models/ModelName
3.	Change the model name at service message in default.proto file
service ModelName {
  rpc transform (DataFrame) returns (Prediction);
}
4.	And pass the model name as 4th argument in the command