**Table of contents**
- [Web application II Group10](#web-application-ii-group10)
- [Introduction](#introduction)
- [Running the application](#running-the-application)
  - [Run the server](#run-the-server)
- [Benchmark instructions](#benchmark-instructions)
  - [Install benchmark dependencies](#install-benchmark-dependencies)
  - [Run benchmark application](#run-benchmark-application)
- [Scalability evaluation](#scalability-evaluation)
  
# Web application II group10
Members:
 - Riccardo Gracis (S287961)
 - Alessia Messina (s)
 - Alessandro  (s)
 - Erasmo (s)

# Introduction
The repository contains the code produced by *Group 10* in order to address the requirements expressed in the Lab2 related to the *Web Application II* course from *Politecnico di Torino* (2021/2022).
It is divided into two module:
  - server: contains the BackEnd Spring Application (Kotlin)
  - benchamrk: contains report and script in order to execute scalability testing by means of *npm - loadtest* library. 

# Running the application
The entire application is developed using the *Spring Framework* supported by *Gradle* package manager and it is runnable and testable using an IDE as IntelliJ-IDEA, Eclipse etc...
## Run The Server
Once the repository is imported into the project, use Gradle in order to install all the dependecies. Now the project is ready to be runned by means of the *Application Component* present in the file ```/server/src/main/kotlin/com/example/server/ServerApplication.kt```. Be sure port 8080 is not used otherwise server will not start.

# Benchmark instructions
In the benchmark module is present a simple JS script in order to get data to evaluate the scalability feature of the web application.

## Install benchmark dependencies
In order to execute correctly the script it is required to run the command ```npm install``` into the module ```/benchmark``` to intall all the *npm* dependencies. Now is possible to use uncomment the function in the script and modify the parameter in order to execute the tests.

## Run benchmark application
In order to run the ```benchmark.js``` file, *NODE* interpreter is required. The command to run the script is ```node benchmark.js```.
The execution will create a report file with the result. 
Alternatively is convinent to use the CL functionalities provide by the loadtest library:
  - ```loadtest <http://url> -n 1000 -c 4 -T application/json -H accept:application/json -P '{"key" : "value"}'```

For more information visit the offical loadtest npm page: https://www.npmjs.com/package/loadtest

# Scalability evaluation
Based on the data collected by the benchmark apporach discussed before, we have generated a report using the *usl4j* library in order to evaluate the system behavior in a real web environment. The report consderations are stored in the ```/benchmark/USL_report.pdf file```. 
 
