/*
 * Copyright 2017 Esri.
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

package com.esri.samples.editing.edit_feature_attachments;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.apache.commons.io.IOUtils;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Attachment;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;

public class EditFeatureAttachmentsSample extends Application {

  private ListView<String> attachmentList;
  private Label attachmentsLabel;
  private Button addAttachmentButton;
  private Button deleteAttachmentButton;

  private MapView mapView;
  private FeatureLayer featureLayer;
  private ServiceFeatureTable featureTable;

  private ArcGISFeature selected;
  private List<Attachment> attachments;

  private static final String SERVICE_FEATURE_URL =
      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/DamageAssessment/FeatureServer/0";

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Edit Feature Attachments Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a control panel
      VBox vBoxControl = new VBox(6);
      vBoxControl.setMaxSize(180, 250);
      vBoxControl.getStyleClass().add("panel-region");

      // create a list to show selected feature's attachments
      attachmentList = new ListView<>();
      attachmentsLabel = new Label("Attachments: ");
      attachmentsLabel.getStyleClass().add("panel-label");
      attachmentList.getSelectionModel().selectedItemProperty().addListener((event) -> deleteAttachmentButton.setDisable(attachmentList.getSelectionModel().getSelectedIndex() == -1));

      // create add/delete buttons
      addAttachmentButton = new Button("Add Attachment");
      addAttachmentButton.setMaxWidth(Double.MAX_VALUE);
      addAttachmentButton.setDisable(true);

      deleteAttachmentButton = new Button("Delete Attachment");
      deleteAttachmentButton.setMaxWidth(Double.MAX_VALUE);
      deleteAttachmentButton.setDisable(true);

      // get image attachment
      byte[] image = IOUtils.toByteArray(getClass().getResourceAsStream("/symbols/destroyed.png"));

      // button click to add image attachment to selected feature
      addAttachmentButton.setOnAction(e -> addAttachment(image));

      // button click to delete selected attachment
      deleteAttachmentButton.setOnAction(e -> deleteAttachment(attachmentList.getSelectionModel().getSelectedIndex()));

      // add controls to the panel
      vBoxControl.getChildren().addAll(addAttachmentButton, deleteAttachmentButton, attachmentsLabel, attachmentList);

      // create a map with streets basemap
      ArcGISMap map = new ArcGISMap(Basemap.Type.STREETS, 40, -95, 4);

      // create service feature table from URL
      featureTable = new ServiceFeatureTable(SERVICE_FEATURE_URL);

      // create a feature layer from service feature table
      featureLayer = new FeatureLayer(featureTable);

      // selection options
      featureLayer.setSelectionColor(0xff0000ff);
      featureLayer.setSelectionWidth(10);

      // add the feature layer to the ArcGISMap
      map.getOperationalLayers().add(featureLayer);

      // create a view for this ArcGISMap
      mapView = new MapView();

      // set ArcGISMap to be displayed in the view
      mapView.setMap(map);

      mapView.setOnMouseClicked(event -> {
        if (event.isStillSincePress() && event.getButton() == MouseButton.PRIMARY) {
          // create a map point from a point
          Point2D point = new Point2D(event.getX(), event.getY());

          // clear previous results
          featureLayer.clearSelection();
          addAttachmentButton.setDisable(true);
          attachmentList.getItems().clear();

          // get the clicked feature
          ListenableFuture<IdentifyLayerResult> results = mapView.identifyLayerAsync(featureLayer, point, 1, false);
          results.addDoneListener(() -> {
            try {
              IdentifyLayerResult layer = results.get();
              List<GeoElement> identified = layer.getElements();
              if (identified.size() > 0) {
                GeoElement element = identified.get(0);
                // get selected feature
                if (element instanceof ArcGISFeature) {
                  selected = (ArcGISFeature) element;
                  featureLayer.selectFeature(selected);
                  selected.loadAsync();
                  selected.addDoneLoadingListener(() -> {
                    if (selected.getLoadStatus() == LoadStatus.LOADED) {
                      fetchAttachments(selected);
                    } else {
                      Alert alert = new Alert(Alert.AlertType.ERROR, "Element Failed to Load!");
                      alert.show();
                    }
                  });
                  addAttachmentButton.setDisable(false);
                }
              }
            } catch (InterruptedException | ExecutionException e) {
              displayMessage("Exception getting identify result", e.getCause().getMessage());
            }
          });
        }
      });

      // add map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, vBoxControl);
      StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Updates the UI with a list of a feature's attachments.
   */
  private void fetchAttachments(ArcGISFeature feature) {

    ListenableFuture<List<Attachment>> attachmentResults = feature.fetchAttachmentsAsync();
    attachmentResults.addDoneListener(() -> {
      try {
        attachments = attachmentResults.get();

        // update UI attachments list
        Platform.runLater(() -> {
          attachmentList.getItems().clear();
          attachments.forEach(attachment -> attachmentList.getItems().add(attachment.getName()));
          if (!attachments.isEmpty()) {
            attachmentsLabel.setText("Attachments: ");
          } else {
            attachmentsLabel.setText("No Attachments!");
          }
        });
      } catch (InterruptedException | ExecutionException e) {
        displayMessage("Exception getting feature attachments", e.getCause().getMessage());
      }
    });
  }

  /**
   * Adds an attachment to a Feature.
   * 
   * @param attachment byte array of attachment
   */
  private void addAttachment(byte[] attachment) {

    if (selected.canEditAttachments()) {
      ListenableFuture<Attachment> addResult = selected.addAttachmentAsync(attachment, "image/png",
          "symbols/destroyed.png");
      addResult.addDoneListener(() -> {
        // update feature table
        ListenableFuture<Void> tableResult = featureTable.updateFeatureAsync(selected);

        // apply update to server when new feature is added, and update the
        // displayed list of attachments
        tableResult.addDoneListener(() -> applyEdits(featureTable));
      });
    } else {
      displayMessage(null, "Cannot add attachment.");
    }
  }

  /**
   * Deletes a selected attachment from a Feature.
   */
  private void deleteAttachment(int attachmentIndex) {

    if (selected.canEditAttachments()) {
      ListenableFuture<Void> deleteResult = selected.deleteAttachmentAsync(attachments.get(attachmentIndex));
      deleteResult.addDoneListener(() -> {
        // update feature table
        ListenableFuture<Void> tableResult = featureTable.updateFeatureAsync(selected);
        // apply update to server when new feature is deleted
        tableResult.addDoneListener(() -> applyEdits(featureTable));
      });
    } else {
      displayMessage(null, "Cannot delete attachment");
    }
  }

  /**
   * Sends any edits on the ServiceFeatureTable to the server.
   *
   * @param featureTable service feature table
   */
  private void applyEdits(ServiceFeatureTable featureTable) {

    // apply the changes to the server
    ListenableFuture<List<FeatureEditResult>> editResult = featureTable.applyEditsAsync();
    editResult.addDoneListener(() -> {
      try {
        List<FeatureEditResult> edits = editResult.get();
        // check if the server edit was successful
        if (edits != null && edits.size() > 0 && edits.get(0).hasCompletedWithErrors()) {
          throw edits.get(0).getError();
        }
        // update the displayed list of attachments
        fetchAttachments(selected);
      } catch (InterruptedException | ExecutionException e) {
        displayMessage("Error applying edits on server ", e.getCause().getMessage());
      }
    });
  }

  /**
   * Shows a message in an alert dialog.
   *
   * @param title title of alert
   * @param message message to display
   */
  private void displayMessage(String title, String message) {

    Platform.runLater(() -> {
      Alert dialog = new Alert(Alert.AlertType.INFORMATION);
      dialog.setHeaderText(title);
      dialog.setContentText(message);
      dialog.showAndWait();
    });
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() throws Exception {

    // release resources when the application closes
    if (mapView != null) {
      mapView.dispose();
    }
  }

  /**
   * Opens and runs application.
   * 
   * @param args arguments passed to this application
   */
  public static void main(String[] args) {

    Application.launch(args);
  }

}
