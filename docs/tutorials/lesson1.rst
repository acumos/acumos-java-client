For Generic Java models:
If modeler wants to change the name of the model.

1.	Change the name of the model.zip file
2.	Change the model name in the application.properties file at below place
# Linux version
rel_model_zip=/models/ModelName
3.	Change the model name at service message in default.proto file
service ModelName {
  rpc transform (DataFrame) returns (Prediction);
}
4.	And pass the model name as 4th argument in the command