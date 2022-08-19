# ArcGIS Runtime API for Java Samples

[![Link: ArcGIS Developers home](https://img.shields.io/badge/ArcGIS%20Developers%20Home-633b9b?style=flat-square)](https://developers.arcgis.com)
[![Link: Documentation](https://img.shields.io/badge/Documentation-633b9b?style=flat-square)](https://developers.arcgis.com/java/)
[![Link: API Reference](https://img.shields.io/badge/API%20Reference-633b9b?style=flat-square)](https://developers.arcgis.com/java/api-reference/reference/index.html)
[![Link: Tutorials](https://img.shields.io/badge/Tutorials-633b9b?style=flat-square)](https://developers.arcgis.com/documentation/mapping-apis-and-services/tutorials/)
[![Link: Demos](https://img.shields.io/badge/Demos-633b9b?style=flat-square)](https://github.com/Esri/arcgis-runtime-java-demos)
[![Link: Toolkit](https://img.shields.io/badge/Toolkit-633b9b?style=flat-square)](https://github.com/Esri/arcgis-runtime-toolkit-java)

[![Link: Esri Community](https://img.shields.io/badge/Esri%20Community%20Questions-2d2670?style=flat-square)](https://community.esri.com/t5/arcgis-runtime-sdk-for-java-questions/bd-p/arcgis-runtime-sdk-for-java-questions)
![ArcGIS Developers Twitter](https://img.shields.io/twitter/url?label=ArcGIS%20Developers&logoColor=2d2670&url=https%3A%2F%2Ftwitter.com%2FArcGISDevs)
[![Link: ArcGIS Runtime SDKs Blog](https://img.shields.io/badge/ArcGIS%20Runtime%20SDKS%20Blog-2d2670?style=flat-square)](https://community.esri.com/t5/arcgis-runtime-sdk-for-java-questions/bd-p/arcgis-runtime-sdk-for-java-questions)
[![Link: ArcGIS Blog for Developers](https://img.shields.io/badge/ArcGIS%20Blog%20for%20Developers-2d2670?style=flat-square)](https://community.esri.com/t5/arcgis-runtime-sdk-for-java-questions/bd-p/arcgis-runtime-sdk-for-java-questions)

![Gradle build](https://github.com/Esri/arcgis-runtime-samples-java/workflows/Java%20CI%20with%20Gradle/badge.svg)

Welcome to the home of the ArcGIS Runtime API for Java samples Github page! This repo contains a set of sample projects demonstrating how to accomplish various mapping and geospatial tasks with the ArcGIS Runtime API for Java.

![Choose Camera Controller sample preview](https://user-images.githubusercontent.com/36415565/185649571-7d6feb6f-f0c2-42cb-9139-9d4541dd1da8.png)

Browse the category directories to explore the samples. Each sample is an individual [Gradle](https://docs.gradle.org/current/userguide/userguide.html) project that can be run standalone. The Gradle buildscripts have tasks for running the application, building a jar, and distributing the app as a zip.

Installing Gradle is not necessary since each sample includes the Gradle wrapper.

Accessing Esri location services, including basemaps, routing, and geocoding, requires authentication using either an ArcGIS identity or an API Key:
 1. ArcGIS identity: An ArcGIS named user account that is a member of an organization in ArcGIS Online or ArcGIS Enterprise.
 2. API key: A permanent key that gives your application access to Esri location services. Visit your [ArcGIS Developers Dashboard](https://developers.arcgis.com/dashboard) to create a new API key or access an existing API key.

Note: *in the following instructions for setting the API key, if a `gradle.properties` file does not already exist in the `/.gradle` folder within your home directory, a Gradle task in the samples build.gradle file will generate one for you.*

## Instructions

### IntelliJ IDEA

1. Open IntelliJ IDEA and select _File > Open..._.
2. Choose one of the sample project directories (not the category folder) and click _OK_.
3. Select _File > Project Structure..._ and ensure that the Project SDK and language level are set to use Java 11.
4. Store your API key in the `gradle.properties` file located in the `/.gradle` folder within your home directory. The API key will be set as a Java system property when the sample is run.
   ```
   apiKey = yourApiKey
   ```
5. Open the Gradle view with _View > Tool Windows > Gradle_.
6. In the Gradle view, double-click the `run` task under _Tasks > application_ to run the app.

Note: *if you encounter the error `Could not get unknown property 'apiKey' for task ':run' of type org.gradle.api.tasks.JavaExec.` you may have to set the Gradle user home in the IntelliJ Gradle settings to the `/.gradle` folder in your home directory.*

### Eclipse

1. Open Eclipse and select _File > Import_.
2. In the import wizard, choose _Gradle > Existing Gradle Project_, then click _Next_.
3. Choose one of the sample project directories (not the category folder) as the project root directory.
4. Click _Finish_ to complete the import.
5. Store your API key in the `gradle.properties` file located in the `/.gradle` folder within your home directory. The API key will be set as a Java system property when the sample is run.
   ```
   apiKey = yourApiKey
   ```
6. Open the Gradle Tasks view with _Window > Show View > Other... > Gradle > Gradle Tasks_.
7. In the Gradle Tasks view, double-click the `run` task under _{project_name} > application_ to run the app.

### Terminal

1. `cd` into one of the sample project directories (not the category folder).
2. Run `gradle wrapper` to create the Gradle Wrapper
3. Store your API key in the `gradle.properties` file located in the `/.gradle` folder within your home directory. The API key will be set as a Java system property when the sample is run.
4. Run `./gradlew run` on Linux/Mac or `gradlew.bat run` on Windows to run the app.

### Java 11
Java 11 users may find exceptions when running the project if their library path is still set for Oracle JDK 1.8 (see the [OpenJavaFX docs](https://openjfx.io/openjfx-docs/) for more information). A workaround for this is to add the following argument in the `run` task of the Gradle buildscript:
```
systemProperty "java.library.path", "C:\tmp"
```

### Offline sample data
Some samples require offline data. A `samples-data` directory will automatically download to the project root when the Gradle project is configured/imported.

## Requirements

See the ArcGIS Runtime API's [system requirements](https://developers.arcgis.com/java/reference/system-requirements/).

## Issues

Find a bug or want to request a new feature?  Please let us know by submitting an issue.

## Contributing

Esri welcomes contributions from anyone and everyone. Please see our [guidelines for contributing](https://github.com/esri/contributing).

New to Git? Check out our [Working with Git](https://github.com/Esri/arcgis-runtime-samples-java/blob/master/WorkingWithGit.md) guide.

## Licensing

Copyright 2022 Esri

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy
of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations
under the License.

A copy of the license is available in the repository's license.txt file.
