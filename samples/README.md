#Java SE SDK Samples#
This project includes a set of samples demonstrating how to accomplish various 
mapping and GIS tasks with the ArcGIS Runtime SDK for Java.

These samples are built with Gradle. If you want to learn more about Gradle, 
learn more from [Gradle's guide](https://docs.gradle
.org/current/userguide/userguide.html). The samples project follows a standard 
Gradle multi-project structure.

This guide has instructions for running samples using the command line or 
with the IDEs Eclipse and IntelliJ IDEA.

##Running the Samples##
To run the samples using the command line, open a terminal and cd into the 
`java-se-sdk/samples` directory. First run
```
$ ./gradlew build
```
to ensure the project builds are up to date.

To run a sample, simply call the gradle run task for the sample:

On Linux/Mac
```
$ ./gradlew :display-information:show-callout:run
```

On Windows
```
> gradlew.bat :display-information:show-callout:run
```

There is no need to install Gradle to run the samples.

If for any reason you want to run all the samples from a category sequentially, 
call the run task on the category:
```
$ ./gradlew :editing:run
```

To run all samples:
```
$ ./gradlew run
```

##Importing into an IDE##
We will step through how to import the Samples project into Eclipse and 
IntelliJ because they both have decent support for Gradle.

###IntelliJ IDEA##
After cloning the samples, open IntelliJ IDEA and follow these steps:

1. Click *Import Projects* from the Welcome Screen or select *File > New > 
Project from Existing Sources*  .
2. In the select path dialog, select the `build.gradle` file in the 
`java-se-sdk/samples` directory. If you only want to import a category of 
samples, select the `build.gradle` file in that category directory. Select 
the `build.gradle` file from the sample directory to import an individual 
sample. Click *OK* after specifying the `build.gradle` file.
3. Click *OK* at the next dialog to complete the import.

<img src="./intellij_proj.png" alt="IntelliJ IDEA project structure" 
height="200">

To view all of the gradle tasks including the Run task, go to *View > Tool 
Windows > Gradle*. Select the Run task in the sample's task list to run the 
sample.

Alternatively, you can open the sample's main class, right-click, and select 
*Run* from the dropdown menu.

###Eclipse###
If you do not already have the Gradle (STS) Integration for Eclipse plugin,

1. From the Eclipse toolbar, select *Help > Eclipse Marketplace*.
2. Search for "gradle" and click *Install* on the plugin with the 
elipse logo called Gradle (STS) Integration for Eclipse.

To import the samples, follow these steps:

1. Open Eclipse and select *File > Import*.
2. For the import wizard, choose *Gradle (STS) > Gradle (STS) Project*, then click Next.
3. For the root folder, browse to `java-se-sdk/samples`. Click *Build Model* to
 generate the Gradle build model.
4. Make sure the checkboxes for the samples project and all of its subprojects are checked. Also, check the box that 
says "Run before cleanEclipse eclipse". This will refresh the eclipse project files.
5. Click *finish* to complete the import.

By default, Eclipse shows each sub-project as a separate project in the 
workspace. If you want to see the samples in a nested folder view, follow 
these steps:

1. Open the Project Explorer view by selected *Window > Show view > Project 
Explorer*.
2. Click the down-arrow in the top-right corner of the Project Explorer window 
OR press Ctrl + F10 with the Project Explorer window selected.
3. In the dropdown menu that appears, select *Projects Presentation > 
Hierarchical*. Eclipse should now show the samples as a single, nested project.

<img src="./eclipse_proj.png" alt="Eclipse project structure" 
height="200">

NOTE: If Eclipse still shows the samples as individual projects after choosing the hierarchical view, try restarting 
Eclipse. If this still does not work, try importing again.

###Other IDEs###
Search for gradle plugin support for your IDE to import the samples.

##Resources##
* [ArcGIS Runtime SDK for Java](https://developers.arcgis.com/java/)  
* [ArcGIS Blog](https://blogs.esri.com/esri/arcgis/)  
* [Esri Twitter](https://twitter.com/esri)  

##Contributing##
Esri welcomes contributions from anyone and everyone. Please see our 
[guidelines for contributing](https://github.com/esri/contributing).

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


