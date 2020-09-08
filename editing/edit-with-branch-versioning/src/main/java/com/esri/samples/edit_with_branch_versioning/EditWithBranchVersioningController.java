/*
 * Copyright 2020 Esri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.esri.samples.edit_with_branch_versioning;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.geometry.Point2D;

import com.esri.arcgisruntime.arcgisservices.ServiceVersionInfo;
import com.esri.arcgisruntime.arcgisservices.ServiceVersionParameters;
import com.esri.arcgisruntime.arcgisservices.VersionAccess;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.FeatureTableEditResult;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.data.ServiceGeodatabase;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.security.AuthenticationManager;
import com.esri.arcgisruntime.security.DefaultAuthenticationChallengeHandler;
import com.esri.arcgisruntime.security.UserCredential;

public class EditWithBranchVersioningController {

  // elements from fxml
  @FXML public ComboBox<String> damageTypeComboBox;
  @FXML private MapView mapView;
  @FXML private Label currentVersionLabel;
  @FXML private Button createVersionButton;
  @FXML private Button switchVersionButton;
  @FXML private ProgressIndicator progressIndicator;
  @FXML private VBox createVersionVBox;
  @FXML private TextField name;
  @FXML private TextField description;
  @FXML private ComboBox<VersionAccess> accessComboBox;
  @FXML private VBox editFeatureVBox;

  private ServiceGeodatabase serviceGeodatabase; // keeps loadable in scope to avoid garbage collection
  private FeatureLayer featureLayer; // keeps loadable in scope to avoid garbage collection
  private ArcGISFeature selectedFeature; // keeps loadable in scope to avoid garbage collection
  private ServiceFeatureTable serviceFeatureTable;
  private String userCreatedVersion;
  private String defaultVersion;

  public void initialize() {

    try {

      // create a map with the streets vector basemap
      ArcGISMap map = new ArcGISMap(Basemap.createStreetsVector());

      // create a map view and set its map
      mapView.setMap(map);

      // configure the initial UI settings
      createVersionButton.setDisable(true);
      switchVersionButton.setDisable(true);
      editFeatureVBox.setDisable(true);

      // add the version access types to the combo box
      accessComboBox.getItems().addAll(VersionAccess.PUBLIC, VersionAccess.PROTECTED, VersionAccess.PRIVATE);

      // add the damage types to the combo box and handle selection
      damageTypeComboBox.getItems().addAll("Destroyed", "Inaccessible", "Major", "Minor", "Affected");
      damageTypeComboBox.getSelectionModel().selectedItemProperty().addListener((o, p, n) -> {
        if (!selectedFeature.getAttributes().get("typdamage").equals(n)) {
          try {
            updateAttributes(selectedFeature);
          } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Feature attributes failed to update.").show();
          }
        }
      });

      // set a user credential
      UserCredential userCredential = new UserCredential("editor01", "editor01.password");

      // handle authentication for the service geodatabase
      AuthenticationManager.setAuthenticationChallengeHandler(new DefaultAuthenticationChallengeHandler());

      // create and load a service geodatabase
      serviceGeodatabase = new ServiceGeodatabase("https://sampleserver7.arcgisonline" +
        ".com/arcgis/rest/services/DamageAssessment/FeatureServer");
      serviceGeodatabase.setCredential(userCredential);
      serviceGeodatabase.loadAsync();
      serviceGeodatabase.addDoneLoadingListener(() -> {

        if (serviceGeodatabase.getLoadStatus() == LoadStatus.LOADED) {

          //for testing remove
          printOutAllVersions();

          // when the service geodatabase has loaded get the default version
          defaultVersion = serviceGeodatabase.getDefaultVersionName();

          // get the service feature table from the service geodatabase
          if (serviceGeodatabase.getTable(0) != null) {
            serviceFeatureTable = serviceGeodatabase.getTable(0);

            // create a feature layer from the service feature table and add it to the map
            featureLayer = new FeatureLayer(serviceFeatureTable);
            map.getOperationalLayers().add(featureLayer);

            featureLayer.addDoneLoadingListener(() -> {
              if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {

                // when the feature layer has loaded remove the progress indicator and set the viewpoint
                progressIndicator.setVisible(false);
                mapView.setViewpointAsync(new Viewpoint(featureLayer.getFullExtent()));

                // update the UI
                createVersionButton.setDisable(false);
                currentVersionLabel.setText("Current version: " + serviceGeodatabase.getVersionName());

                // listener for mouse click events to select features
                mapView.setOnMouseClicked(event -> {

                  if (event.isStillSincePress() && event.getButton() == MouseButton.PRIMARY) {

                    // reset the UI
                    featureLayer.clearSelection();
                    editFeatureVBox.setDisable(true);

                    // create point where the user clicked
                    Point2D point = new Point2D(event.getX(), event.getY());

                    // check if a feature is selected
                    ListenableFuture<IdentifyLayerResult> results = mapView.identifyLayerAsync(featureLayer, point, 1
                      , false);
                    results.addDoneListener(() -> {
                      try {
                        IdentifyLayerResult layer = results.get();
                        List<GeoElement> identified = layer.getElements();
                        if (!identified.isEmpty()) {
                          GeoElement element = identified.get(0);
                          // get the selected feature
                          if (element instanceof ArcGISFeature) {
                            selectedFeature = (ArcGISFeature) element;
                            featureLayer.selectFeature(selectedFeature);
                            selectedFeature.loadAsync();
                            selectedFeature.addDoneLoadingListener(() -> {
                              if (selectedFeature.getLoadStatus() == LoadStatus.LOADED) {
                                // when a feature has been selected set the damage combo box to the feature's attribute value
                                damageTypeComboBox.getSelectionModel().select((String) selectedFeature.getAttributes().get("typdamage"));

                                // enable feature editing UI if not on default branch version
                                if (!serviceGeodatabase.getVersionName().equals(defaultVersion)) {
                                  editFeatureVBox.setDisable(false);
                                }
                              } else {
                                // if the selected feature failed to load display an error
                                new Alert(Alert.AlertType.ERROR, "Feature failed to load!").show();
                              }
                            });
                          }
                        }
                      } catch (InterruptedException | ExecutionException e) {
                        new Alert(Alert.AlertType.ERROR, "Exception getting identify result").show();
                      }
                    });
                  }

                  if (event.isStillSincePress() && event.getButton() == MouseButton.SECONDARY) {
                    if(selectedFeature != null && !serviceGeodatabase.getVersionName().equals(defaultVersion)){
                      Point2D secondaryClickPoint = new Point2D(event.getX(), event.getY());
                      Point mapPoint = mapView.screenToLocation(secondaryClickPoint);
                      selectedFeature.setGeometry(mapPoint);
                      updateAttributes(selectedFeature);
                    }
                  }
                });
              } else {
                new Alert(Alert.AlertType.ERROR, "Feature layer failed to load.").show();
              }
            });
          } else {
            new Alert(Alert.AlertType.ERROR, "Unable to get the service feature table.").show();
          }
        } else {
          new Alert(Alert.AlertType.ERROR, "Service geodatabase failed to load.").show();
        }
      });
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  private void updateAttributes(ArcGISFeature featureToUpdate) {
    // only allow editing if not on the default branch version and if identified feature can be edited
    if (!serviceGeodatabase.getVersionName().equals(defaultVersion) && serviceFeatureTable.canUpdate(featureToUpdate)) {
      featureToUpdate.getAttributes().put("typdamage", damageTypeComboBox.getValue());
      // update feature in the feature table
      ListenableFuture<Void> editResult = serviceFeatureTable.updateFeatureAsync(featureToUpdate);
      editResult.addDoneListener(() -> {
        new Alert(Alert.AlertType.INFORMATION, "Feature has been updated locally. Changes will be synced when you switch branch.").show();
        System.out.println("local edits " + serviceFeatureTable.hasLocalEdits());
      });
    } else {
      new Alert(Alert.AlertType.ERROR, "Feature cannot be updated.").show();
    }
  }

  /**
   * Disposes application resources.
   */
  void terminate () {
    if (mapView != null) {
      mapView.dispose();
    }
  }

  public void createVersion(MouseEvent mouseEvent) {
    ServiceVersionParameters newVersionParameters = new ServiceVersionParameters();
    newVersionParameters.setName(name.getText());
    newVersionParameters.setAccess(accessComboBox.getSelectionModel().getSelectedItem());
    newVersionParameters.setDescription(description.getText());
    ListenableFuture<ServiceVersionInfo> createdVersion = serviceGeodatabase.createVersionAsync(newVersionParameters);
    createVersionButton.setText("Creating version....");
    createVersionButton.setDisable(true);
    switchVersionButton.setDisable(false);
    createdVersion.addDoneListener(() -> {
      try {
        //for testing, remove once done
        printOutAllVersions();

        //hide form from UI as sample only allows 1 version to be created
        createVersionVBox.setDisable(true);
        createVersionButton.setText("Version Created");

        //get the name of the created version and switch to it
        ServiceVersionInfo createdVersionInfo = createdVersion.get();
        userCreatedVersion = createdVersionInfo.getName();
        switchVersion();

      } catch (Exception ex) {
        new Alert(Alert.AlertType.ERROR, "Unable to create new version.").show();
      }
    });
  }

  public void switchVersion() {
    // reset UI when version switched
    editFeatureVBox.setDisable(true);

    // apply edits at time of switch if the current version is not the default version
    if (serviceGeodatabase.hasLocalEdits() && !serviceGeodatabase.getVersionName().equals(defaultVersion)) {
      ListenableFuture<List<FeatureTableEditResult>> resultOfApplyEdits = serviceGeodatabase.applyEditsAsync();
      resultOfApplyEdits.addDoneListener(() -> {
        try {
          List<FeatureTableEditResult> edits = resultOfApplyEdits.get();
          // check if the server edit was successful
          if (edits != null && edits.size() > 0) {
            ListenableFuture<Void> switchVersionResult = serviceGeodatabase.switchVersionAsync(defaultVersion);
            switchVersionResult.addDoneListener(() -> {
              try {
                currentVersionLabel.setText("Current version: " + serviceGeodatabase.getVersionName());
              } catch (Exception ex){
                ex.printStackTrace();
              }
            });
          } else {
            new Alert(Alert.AlertType.ERROR, "Local edits have not been applied.").show();
          }
        } catch (InterruptedException | ExecutionException e) {
          new Alert(Alert.AlertType.ERROR, "Error applying edits on server.").show();
        }
      });
    } else if (!serviceGeodatabase.getVersionName().equals(defaultVersion)) {
      ListenableFuture<Void> switchVersionResult = serviceGeodatabase.switchVersionAsync(defaultVersion);
      switchVersionResult.addDoneListener(() -> {
        try {
          currentVersionLabel.setText("Current version: " + serviceGeodatabase.getVersionName());
        } catch (Exception ex){
          ex.printStackTrace();
        }
      });
    } else {
      ListenableFuture<Void> switchVersionResult = serviceGeodatabase.switchVersionAsync(userCreatedVersion);
      switchVersionResult.addDoneListener(() -> {
        try {
          currentVersionLabel.setText("Current version: " + serviceGeodatabase.getVersionName());
        } catch (Exception ex){
          ex.printStackTrace();
        }
      });
    }
  }

  //only for testing remove
  public void printOutAllVersions() {
    ListenableFuture<List<ServiceVersionInfo>> allVersions = serviceGeodatabase.fetchVersionsAsync();
    allVersions.addDoneListener(() -> {
      try {
        List<ServiceVersionInfo> allVersionsList = allVersions.get();
        for (ServiceVersionInfo serviceVersionInfo : allVersionsList) {
          System.out.println(serviceVersionInfo.getName());
          System.out.println(serviceVersionInfo.getAccess());
          System.out.println(serviceVersionInfo.getDescription());
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
}
