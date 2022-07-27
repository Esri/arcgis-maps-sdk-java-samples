# Create mobile geodatabase

Create a mobile geodatabase.

![CreateMobileGeodatabase](CreateMobileGeodatabase.png)

## Use case

A mobile geodatabase is a collection of various types of GIS datasets contained in a single file (.geodatabase) on disk that can store, query, and manage spatial and non-spatial data. Mobile geodatabases are stored in a SQLite database and can contain up to 2 TB of portable data. Users can create, edit and share mobile geodatabases across ArcGIS Pro, ArcGIS Runtime, or any SQL software. These mobile geodatabases support both viewing and editing and enable new offline editing workflows that don’t require a feature service.

For example, a user would like to track the location of their device at various intervals to generate a heat map of the most visited locations. The user can add each location as a feature to a table and generate a mobile geodatabase. The user can then instantly share the mobile geodatabase to ArcGIS Pro to generate a heat map using the recorded locations stored as a geodatabase feature table.

## How to use the sample

Tap on the map to add a feature symbolizing the user's location. Tap "View Table" to view the contents of the geodatabase feature table. Once you have added the location points to the map, click on "Create Mobile Geodatabase" to retrieve the .geodatabase file which can then be imported into ArcGIS Pro or opened in an ArcGIS Runtime application.

## How it works

1. Create the Geodatabase from the mobile geodatabase location on file using `Geodatabase.createAsync()`.
2. Create a new TableDescription and add the list of FieldDescriptions to the table description.
3. Create a GeodatabaseFeatureTable in the geodatabase from the TableDescription using `Geodatabase.createTableAsync()`.
4. Create a feature on the selected map point using `GeodatabaseFeatureTable.createFeature(featureAttributes, normalizedMapPoint)`.
5. Add the feature to the table using `GeodatabaseFeatureTable.addFeatureAsync(feature)`.
6. Each feature added to the GeodatabaseFeatureTable is committed to the mobile geodatabase file.
7. Close the mobile geodatabase to save the ".geodatabase" file using `Geodatabase.close()`.

## Relevant API

* FeatureLayer
* FeatureTable
* FieldDescription
* Geodatabase
* GeodatabaseFeatureTable
* TableDescription

## Tags

arcgis pro, database, feature, feature table, geodatabase, mobile geodatabase, sqlite, FeatureLayer, FeatureTable, FieldDescription, Geodatabase, GeodatabaseFeatureTable, TableDescription
