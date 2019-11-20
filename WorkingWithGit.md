### Fork the repository
To view and make changes to your own copy of the repository, [fork the repo](https://github.com/Esri/arcgis-runtime-samples-java/fork).

### Clone your fork

Open your terminal and navigate to the directory you want to put the repository directory in. Then [clone your fork](https://help.github.com/articles/fork-a-repo#step-2-clone-your-fork):
```
$ git clone https://github.com/YOUR-USERNAME/arcgis-runtime-samples-java.git
```

### Configure your fork for syncing
To sync changes you make in a fork with this repository, you must configure a remote that points to the upstream repository in Git. Go into the project directory you just cloned. Run the following to add the upstream remote:
```
$ git remote add upstream https://github.com/Esri/arcgis-runtime-samples-java.git
```
Verify the changes by running
```
$ git remote -v
```
You should see something like the following:
```
origin	https://github.com/YOUR_USERNAME/arcgis-runtime-samples-java.git (fetch)
origin	https://github.com/YOUR_USERNAME/arcgis-runtime-samples-java.git (push)
upstream https://github.com/Esri/arcgis-runtime-samples-java.git (fetch)
upstream https://github.com/Esri/arcgis-runtime-samples-java.git (push)
```

### Sync your fork
If this repository gets updated while you are working on your fork, you can add the changes to your fork by syncing it.

To check for any changes made to this repository, `cd` into your local copy of the repo and run
```
$ git fetch upstream
```

Make sure to checkout whatever local branch you want the remote changes to be added to:
```
$ git checkout master
```

Finally, merge the upstream changes into your branch with
```
$ git merge upstream/master
```

