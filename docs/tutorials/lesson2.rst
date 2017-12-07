Command and Parameters:

Sample command to run Java based models is – 

java -jar JavaClient.jar http://cognita-dev1-vm01-core.eastus.cloudapp.azure.com:8090/onboarding-app/v2/models H D:\js00353493\ATT\Cognita\Sprint4\e911_h2oModel\supporting E911H2OModel jabeen test@123

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
https://bitbucket.org/cognita_dev/cognita-java-client/src/b8d60346b6c23f7a2cd795bc2fe512a441157c8b/java_client/?at=master

If you are using H2o, you will need default.proto, model, application.properties, ModelConfig.properties and protobuf-java-3.4.0.jar files.
These Files are at below location:
https://bitbucket.org/cognita_dev/cognita-java-client/src/68149b2c848afdbc1d82122c5576c61727ff8656/h2o_distributable/?at=master

To get the model service jar file build the below project code
https://bitbucket.org/cognita_dev/generic-model-runner

If you are using Generic Java, you will need default.proto, model, application.properties, ModelConfig.properties and protobuf-java-3.4.0.jar files.
These Files are at below location:
https://bitbucket.org/cognita_dev/cognita-java-client/src/68149b2c848afdbc1d82122c5576c61727ff8656/generic_distributable/?at=master


To get the model service jar file build the below project code
https://bitbucket.org/cognita_dev/generic-model-runner

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