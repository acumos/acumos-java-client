.. ===============LICENSE_START=======================================================
.. Acumos CC-BY-4.0
.. ===================================================================================
.. Copyright (C) 2017-2018 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
.. ===================================================================================
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
.. ===============LICENSE_END=========================================================


========== 
Tutorial 1 
==========

For Generic Java models:
If modeler wants to change the name of the model.

1.	Change the name of the model.zip file
2.	Change the model name in the application.properties file at below place

# Linux version
rel_model_zip=/models/ModelName

3.	Change the model name at service message in default.proto file service ModelName {  rpc transform (DataFrame) returns (Prediction);}
4.	And pass the model name as 4th argument in the command
