# Corona Detection Android App

This app is for detect corona virus in an xray image.

## Tools

- Android Studio
- Java

## Installation

- Clone this project to your computer.
- Open project in Android Studio.


## Directory Structure

|Directory       |Purpose                          
|----------------|-------------------------------
|activities      |It contains all activities with only UI code           
|repositories    |It contains Java classes with actual business logic functions
|retrofit        |It contains further two directories Clients and Objs
|Clients         |It contains actual request making Interfaces
|utils           |It contains helping classes
|viewmodels      |It contains View Models for all the activities. Its functions are get called by UI and it further calls relavent repository class' function.

## Note

Make sure to make debuggable false in Gradle Module before publishing app to PlayStore.

```
buildTypes {
        release {
            debuggable false
        }
    }
```