# arcgis-runtime-samples-java

**For Quartz Beta 1**

Here are a set of simple samples that show you how to accomplish different mapping and GIS tasks with the ArcGIS Runtime SDK for Java. The samples use ArcGIS Online basemaps and services.  Learn more [here](http://www.arcgis.com/about/).


## Features
* Basemaps - Set different basemaps
* Feature editing - Edit online feature services
* Graphics overlays - Add temporary data to your map view

# Developer Instructions

##  Fork the repo
If you haven't already, fork the [this repo](https://github.com/Esri/arcgis-runtime-samples-java/fork).

## Clone the repo

### Command line Git
[Clone the ArcGIS Java SDK Samples](https://help.github.com/articles/fork-a-repo#step-2-clone-your-fork)

Open your terminal, navigate to your working directory, use ```git clone``` to get a copy of the repo.

```
# Clones your fork of the repository into the current directory in terminal
$ git clone https://github.com/YOUR-USERNAME/arcgis-runtime-samples-java.git
```

## Configure remote upstream for your fork
To sync changes you make in a fork with this repository, you must configure a remote that points to the upstream repository in Git.

- Open a terminal (Mac users) or command prompt (Windows & Linux users)
- List the current configured remote repository for your fork

```
$ git remote -v
origin	https://github.com/YOUR_USERNAME/arcgis-runtime-samples-java.git (fetch)
origin
```

- Specify a new remote upstream repository

```
$ git remote add upstream https://github.com/Esri/arcgis-runtime-samples-java.git
```

- Verify the new upstream repository

```
$ git remote -v

origin	https://github.com/YOUR_USERNAME/arcgis-runtime-samples-java.git (fetch)
origin	https://github.com/YOUR_USERNAME/arcgis-runtime-samples-java.git (push)
upstream https://github.com/Esri/arcgis-runtime-samples-java.git (fetch)
upstream https://github.com/Esri/arcgis-runtime-samples-java.git (push)
```

## Sync your fork
Once you have set up a remote upstream you can keep your fork up to date with our samples repository by syncing your fork.

- Open a terminal (Mac users) or command prompt (Windows & Linux users)
- Change to the current working directory of your local repository
- Fetch the branches and commits from the upstream repository.  Commits to ```master``` will be stored in a local branch, ```upstream/master```.

```
$ git fetch upstream
```

- Check out your forks local ```master``` branch

```
$ git checkout master
```

- Merge changes from ```upstream/master``` into  your local ```master``` branch which syncs your forks ```master``` branch with our samples repository.

```
$ git merge upstream/master
```

## Resources

* [ArcGIS Runtime SDK for Java](https://developers.arcgis.com/java/)
* [ArcGIS Blog](http://blogs.esri.com/esri/arcgis/)
* [twitter@esri](http://twitter.com/esri)

## Issues

Find a bug or want to request a new feature?  Please let us know by submitting an issue.

## Contributing

Esri welcomes contributions from anyone and everyone. Please see our [guidelines for contributing](https://github.com/esri/contributing).

## Licensing
Copyright 2015 Esri

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

A copy of the license is available in the repository's license.txt  file.

[](Esri Tags: ArcGIS Runtime Java JavaFX)
[](Esri Language: JavaScript)​
