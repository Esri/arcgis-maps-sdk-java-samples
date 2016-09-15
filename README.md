# ArcGIS Runtime Java SDK Samples
### Quartz Beta 2
This project includes a set of samples demonstrating how to accomplish various mapping and GIS tasks with the ArcGIS Runtime SDK for Java.

These samples are built with Gradle. If you want to learn more about Gradle, learn more from [Gradle's guide]
(https://docs.gradle.org/current/userguide/userguide.html). The samples project has a Gradle multi-project structure.

This guide has instructions for running samples using the command line or with the Eclipse and IntelliJ IDEA IDEs.

For developers new to Git, please see the wiki page for how to [clone this repository](https://github.com/Esri/arcgis-runtime-samples-java/wiki/working-with-git).

## Running the Samples
To run the samples in a terminal, `cd` into the project and call the gradle `run` task for the sample:

On Linux/Mac
```
$ ./gradlew :display-information:show-callout:run
```

On Windows
```
> gradlew.bat :display-information:show-callout:run
```

There is no need to install Gradle to run the samples.

## Offline sample data
Some samples require offline data. The first time you refresh the Gradle project in an IDE, or run the `build` or 
`run` tasks, the `downloadData` task will automatically download a local data folder called samples-data into the 
directory with arcgis-java-runtime-samples. To get the latest data, delete the old `samples-data` folder, and then run
`downloadData`.

## Importing into an IDE
We will step through how to import the Samples project into Eclipse and IntelliJ IDEA. In both IDEs you can choose to 
import all the samples, just a category of samples, or a single sample.

### IntelliJ IDEA
After cloning the samples, open IntelliJ IDEA and follow these steps:

1. Click *Import Projects* from the Welcome Screen or select *File > New > Project from Existing Sources*.
2. In the select path dialog, select the root `build.gradle` file in the `arcgis-runtime-samples-java` directory. If you only want to import a category of samples, select the `build.gradle` file in that category directory. To import just one sample, select the `build.gradle` file from the sample's directory. Click *OK* after specifying the `build.gradle` file.
3. Click *OK* at the next dialog to complete the import.

<img src="./intellij_proj.png" alt="IntelliJ IDEA project structure" height="200">

To view all of the gradle tasks including the Run task, go to *View > Tool Windows > Gradle*. Select the Run task in the sample's task list to run the sample.

Alternatively, you can open the sample's main class, right-click, and select *Run* from the dropdown menu.

### Eclipse
To import the samples with Eclipse's default gradle plugin, follow these steps:

1. Open Eclipse and select *File > Import*.
2. In the import wizard, choose *Gradle > Gradle Project*, then click Next.
3. Select the `arcgis-runtime-samples-java` directory as the project root directory. If you choose a category or sample directory as the root, only those samples will be imported.
4. Click *finish* to complete the import.

By default, Eclipse shows each sub-project as a separate project in the workspace. If you want to see the samples in a nested folder view, follow these steps:

1. Open the Project Explorer view by selected *Window > Show view > Project Explorer*.
2. Click the down-arrow in the top-right corner of the Project Explorer window OR press Ctrl + F10 with the Project 
Explorer window selected.
3. In the dropdown menu that appears, select *Projects Presentation > Hierarchical*. Eclipse should now show the 
samples as a single, nested project. You may need to restart Eclipse for the change to occur.

<img src="./eclipse_proj.png" alt="Eclipse project structure" height="200">

###Other IDEs###
Other IDEs may support Gradle too. Please consult their documentation for importing Gradle projects.

##Resources##
* [ArcGIS Runtime SDK for Java](https://developers.arcgis.com/java/)  
* [ArcGIS Blog](https://blogs.esri.com/esri/arcgis/)  
* [Esri Twitter](https://twitter.com/esri)  

##Contributing##
Esri welcomes contributions from anyone and everyone. Please see our [guidelines for contributing]
(https://github.com/esri/contributing).

Find a bug or want a new feature? Please let us know by submitting an issue.

##Licensing##
Copyright 2016 Esri

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
