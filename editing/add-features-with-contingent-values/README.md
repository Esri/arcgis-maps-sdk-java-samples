# Add features with contingent values

Create and add features whose attribute values satisfy a predefined set of contingencies.

![Image of add features with contingent values](AddFeaturesWithContingentValues.png)

## Use case

Contingent values are a data design feature that allow you to make values in one field dependent on values in another field. Your choice for a value on one field further constrains the domain values that can be placed on another field. In this way, contingent values enforce data integrity by applying additional constraints to reduce the number of valid field inputs.

For example, a field crew working in a sensitive habitat area may be required to stay a certain distance away from occupied bird nests, but the size of that exclusion area differs depending on the bird's level of protection according to presiding laws. Surveyors can add points of bird nests in the work area and their selection of the size of the exclusion area will be contingent on the values in other attribute fields.

## How to use the sample

Click on the map to add a feature symbolizing a bird's nest. Then choose values describing the nest's status, protection, and buffer size. Notice how different values are available depending on the values of preceding fields. Once the contingent values are validated, click "Save Feature" to add the feature to the map. To delete the feature as you are in progress, click "Discard Feature".

## How it works

1. Create and load the `Geodatabase` from the mobile geodatabase location on file.
2. Load the `GeodatabaseFeatureTable` from the geodatabase.
3. Create a new `FeatureLayer` from the geodatabase feature table and add it to the map.
4. Create a new `ArcGISFeature` using `geodatabaseFeatureTable.createFeature()`
5. Load the `ContingentValuesDefinition` from the feature table.
6. Get the required geodatabase feature table field by name using `.getField(String fieldName)`.
7. Then get the `CodedValueDomain` of the field with `field.getDomain()`.
8. Get the coded value domain's `CodedValue`s with `codedValueDomain.getCodedValues()`.
9. After selecting a value from the initial coded values for the first field, retrieve the remaining valid contingent values for each field as you select the values for the attributes.
   i. Get the `ContingentValueResult`s by using `geodatabaseFeatureTable.getContingentValues(Feature feature, String field)` with the feature and the target field by name.
   ii. Get an array of valid `ContingentValue`s from `contingentValuesResult.getContingentValuesByFieldGroup().get(String fieldGroupName)`  with the name of the relevant field group.
   iii. Iterate through the array of valid contingent values to create an array of `ContingentCodedValue` names or the minimum and maximum values of a `ContingentRangeValue` depending on the type of `ContingentValue` returned.
10. Validate the feature's contingent values by using `validateContingencyConstraints(Feature feature)` with the current feature. If the resulting array is empty, the selected values are valid.

## Relevant API

* CodedValue
* CodedValueDomain
* ContingencyConstraintViolation
* ContingentCodedValue
* ContingentRangeValue
* ContingentValue
* ContingentValuesDefinition
* ContingentValuesResult
* GeodatabaseFeatureTable

## Offline data

This sample uses the [Contingent values birds nests](https://arcgis.com/home/item.html?id=e12b54ea799f4606a2712157cf9f6e41) mobile geodatabase and the [Fillmore topographic map](https://arcgis.com/home/item.html?id=b5106355f1634b8996e634c04b6a930a) vector tile package for the basemap.

## About the data

The mobile geodatabase contains birds nests in the Fillmore area, defined with contingent values. Each feature contains information about its status, protection, and buffer size.

## Additional information

Learn more about contingent values and how to utilize them on the [ArcGIS Pro documentation](https://pro.arcgis.com/en/pro-app/latest/help/data/geodatabases/overview/contingent-values.htm).

## Tags

coded values, contingent values, feature table, geodatabase
