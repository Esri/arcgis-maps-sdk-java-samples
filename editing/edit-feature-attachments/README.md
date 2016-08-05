#Edit Feature Attachments#
Demonstrates how you can add, delete, and fetch an attachment from a Feature in a FeatureLayer. 

##How to use the sample##
To add an attachment.
  - click on a feature and then click add attachment button in panel
  
To delete an attachment.
  - click on a feature, select an attachment from attachment list, and click delete attachment button

![](EditFeatureAttachments.png)

##How it works##
To get a `Feature` from a `ServiceFeatureTable` and add or delete `Attachment`:

1. Create a service feature table from a URL.
2. Create a `FeatureLayer` from the service feature table.
3. Select features from the feature layer, `FeatureLayer.selectFeatures()`.
4. To fetch the feature's attachments, cast to an `ArcGISFeature` and use`ArcGISFeature.fetchAttachmentsAsync()`.
5. To add an attachment to the selected ArcGISFeature, create an attachment and use `ArcGISFeature.addAttachmentAsync()`.
6. To delete an attachment from the selected ArcGISFeature, use the `ArcGISFeature.deleteAttachmentAsync()`.
7. After a change, apply the changes to the server using `ServiceFeatureTable.applyEditsAsync()`.

##Tags
- ArcGISFeature
- ArcGISMap
- Attachment
- Feature
- FeatureLayer
- MapView
- ServiceFeatureTable
