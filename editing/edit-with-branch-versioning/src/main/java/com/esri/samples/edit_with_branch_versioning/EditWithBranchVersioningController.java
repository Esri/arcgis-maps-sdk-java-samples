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

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Alert;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import com.esri.arcgisruntime.arcgisservices.ServiceVersionInfo;
import com.esri.arcgisruntime.arcgisservices.ServiceVersionParameters;
import com.esri.arcgisruntime.arcgisservices.VersionAccess;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.data.ServiceGeodatabase;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.security.UserCredential;

public class EditWithBranchVersioningController {

  @FXML
  private MapView mapView;
  @FXML
  private Label currentVersionLabel;
  @FXML
  private Button createVersionButton;
  @FXML
  private Button switchVersionButton;
  @FXML
  private ProgressIndicator progressIndicator;
  @FXML
  private VBox createVersionVBox;
  @FXML
  private TextField name;
  @FXML
  private TextArea description;
  @FXML
  private ComboBox<VersionAccess> accessComboBox;
  private ServiceGeodatabase serviceGeodatabase;
  private ServiceFeatureTable serviceFeatureTable;
  private FeatureLayer featureLayer;
  private String userCreatedVersion;
  private String defaultVersion;

  public void initialize() {

    try {

      // create a map with the imagery basemap
      ArcGISMap map = new ArcGISMap(Basemap.createStreetsVector());

      // create a map view and set its map
      mapView.setMap(map);

      createVersionButton.setDisable(true);
      switchVersionButton.setVisible(false);

      // add the lighting modes to the combo box
      accessComboBox.getItems().addAll(VersionAccess.PUBLIC, VersionAccess.PROTECTED, VersionAccess.PRIVATE);


      // create and load a ServiceGeodatabase
      serviceGeodatabase = new ServiceGeodatabase("https://sampleserver7.arcgisonline.com/arcgis/rest/services/DamageAssessment/FeatureServer");
      serviceGeodatabase.setCredential(userCredential);
      serviceGeodatabase.loadAsync();
      serviceGeodatabase.addDoneLoadingListener(() -> {

        if (serviceGeodatabase.getLoadStatus() == LoadStatus.LOADED) {

          //for testing remove
          printOutAllVersions();

          //set the default version to the default version name
          defaultVersion = serviceGeodatabase.getDefaultVersionName();

          // when the service geodatabase loads, create a service feature table
          if (serviceGeodatabase.getTable(0) != null) {
            serviceFeatureTable = serviceGeodatabase.getTable(0);
            // create a feature layer from the service feature table and add to the map
            featureLayer = new FeatureLayer(serviceFeatureTable);
            map.getOperationalLayers().add(featureLayer);
            // set the map view to the feature layer full extent
            featureLayer.addDoneLoadingListener(() -> {
              if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
                // remove progress indicator when the feature layer has loaded
                progressIndicator.setVisible(false);
                mapView.setViewpointAsync(new Viewpoint(featureLayer.getFullExtent()));
                createVersionButton.setDisable(false);
                // display the current branch version
                switchVersion(defaultVersion);
                currentVersionLabel.setText("Current version: " + defaultVersion);
              } else {
                new Alert(Alert.AlertType.ERROR, "Feature layer failed to load.").show();
              }
            });

          } else {
            new Alert(Alert.AlertType.ERROR, "Service geodatabase failed to load.").show();
          }
        }
      });
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
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
    switchVersionButton.setVisible(true);
    createdVersion.addDoneListener(() -> {
      try {
        //for testing, remove once done
        printOutAllVersions();

        //hide form from UI as sample only allows 1 version to be created
        createVersionVBox.setVisible(false);

        //get the name of the created version and switch to it
        ServiceVersionInfo createdVersionInfo = createdVersion.get();
        userCreatedVersion = createdVersionInfo.getName();
        switchVersion(userCreatedVersion);

      } catch (Exception ex) {
        new Alert(Alert.AlertType.ERROR, "Unable to create new version.").show();
      }
    });
  }

  public void switchVersionButtonEvent(MouseEvent mouseEvent) {
    if (serviceGeodatabase.getVersionName().equals(defaultVersion)) {
      switchVersion(userCreatedVersion);
    } else {
      switchVersion(defaultVersion);
    }
  }

  public void switchVersion(String versionName){
    if (!serviceGeodatabase.hasLocalEdits()) {
      ListenableFuture<Void> switchVersionResult = serviceGeodatabase.switchVersionAsync(versionName);
      switchVersionResult.addDoneListener(() -> {
        try {
          currentVersionLabel.setText("Current version: " + versionName);
        } catch (Exception ex){
          ex.printStackTrace();
        }
      });
    } else {
      System.out.println("local edits exist");
    }
  }

  public void printOutAllVersions(){
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
