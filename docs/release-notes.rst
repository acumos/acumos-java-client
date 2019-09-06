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

================================
Acumos Java Client Release Notes
================================

Version 3.0.0 9-Sept 2019 
-------------------------------
* Modify or create new java client for MLlib : `ACUMOS-3129 <https://jira.acumos.org/browse/ACUMOS-3129/>`_

Version 2.2.0 11-June 2019
-------------------------------
* H2O Model dump is not getting created with latest deployed JavaClient : `ACUMOS-2998 <https://jira.acumos.org/browse/ACUMOS-2998/>`_

Version 2.1.0 23-May 2019
-------------------------------
* Java Client needs to use the modeler's default.proto to onboad generic java model : `ACUMOS-1881 <https://jira.acumos.org/browse/ACUMOS-1881/>`_
* Support multiple java methods other than one single predict method : `ACUMOS-1543 <https://jira.acumos.org/browse/ACUMOS-1543/>`_

Version 2.0.0 04-April 2019
-------------------------------
* Modify acumos-java-client in accordance with task 2262 (create microcervice parameter) : `ACUMOS-2264 <https://jira.acumos.org/browse/ACUMOS-2264/>`_

Version 1.14.0 15-March 2019
-------------------------------
* Modify acumos-java-client in accordance with task 2262 (create microcervice parameter) : `ACUMOS-2264 <https://jira.acumos.org/browse/ACUMOS-2264/>`_
* Modify acumos-java-client to take into account license file : `ACUMOS-2277 <https://jira.acumos.org/browse/ACUMOS-2277/>`_

Version 1.11.1 20-November 2018
-------------------------------
* API token authentication not working for java model when onboarded through CLI : `ACUMOS-1916 <https://jira.acumos.org/browse/ACUMOS-1916/>`_

Version 1.11.0 28-September 2018
--------------------------------
* add licenses to code and docs : `ACUMOS-1337 <https://jira.acumos.org/browse/ACUMOS-1337/>`_
* Fix RST compile warnings : `ACUMOS-1312 <https://jira.acumos.org/browse/ACUMOS-1312/>`_

Version 1.10.0 5-June 2018
--------------------------
* Move protobuf library version to config out of code : `ACUMOS-909 <https://jira.acumos.org/browse/ACUMOS-909/>`_
* Password displayed at command line while onboarding H2O model : `ACUMOS-954 <https://jira.acumos.org/browse/ACUMOS-954/>`_

Version 1.0.9 22-May 2018
-------------------------
* Clean windows-specific code that constructs file paths `ACUMOS-818 <https://jira.acumos.org/browse/ACUMOS-818/>`_

Version 1.0.8 March 2018
------------------------
* Generates the correct output datatype for output message based on model inspection (More datatypes will be supported in the future)

Version 1.0.7 March 2018
------------------------
*  Bug fix in Web based onboarding. Wrong arguments were being read.

Version 1.0.6 March 2018
------------------------
* More test cases added

Version 1.0.5 March 2018
------------------------
* Fix : Now authentication url needed for web based onboarding

Version 1.0.4 March 2018
------------------------
* Fix for building fat jar and rename packages

Version 1.0.3 March 2018
------------------------
* Protobuf autogeneration implemented for H2o

Version 1.0.2 March 2018
------------------------
* Accepts csv file for protobuf generation for Generic java models

Version 1.0.1 January 2018
--------------------------
* Hardening
* Integration with Onboarding

Version 1.0.0 December 2017
---------------------------
* Initial Release
