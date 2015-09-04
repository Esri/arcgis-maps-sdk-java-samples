/* Copyright 2015 Esri.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
limitations under the License.  */

package main.java.com.esri.sampleviewer.samples.editing;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.datasource.Feature;
import com.esri.arcgisruntime.datasource.FeatureQueryResult;
import com.esri.arcgisruntime.datasource.QueryParameters;
import com.esri.arcgisruntime.datasource.QueryParameters.SpatialRelationship;
import com.esri.arcgisruntime.datasource.arcgis.FeatureEditResult;
import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.FeatureLayer.SelectionMode;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;

/**
 * This sample shows how to edit geometry on features and commit the changes back to the feature service.
 */

public class EditGeometry extends Application {

  private MapView mapView;
  private Map map;
  private ServiceFeatureTable damageTable;
  private FeatureLayer damageFeatureLayer;
  private Button btnUpdateGeometry;
  private FeatureQueryResult selectedFeatures;

  @Override
  public void start(Stage stage) throws Exception {
    // create a border pane
    BorderPane borderPane = new BorderPane();
    Scene scene = new Scene(borderPane);

    // size the stage and add a title
    stage.setTitle("Edit feature geometry : click to select a feature and press the button to nove it North.");
    stage.setWidth(700);
    stage.setHeight(800);
    stage.setScene(scene);
    stage.show();

    // create a Map which defines the layers of data to view
    try {
      map = new Map(Basemap.createStreets());
      
      // create the MapView JavaFX control and assign its map
      mapView = new MapView();
      mapView.setMap(map);
      
      // listen into click events for selecting features
      mapView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          // Respond to primary (left) button only
          if (event.getButton() == MouseButton.PRIMARY)
          {
            //create a screen point from the mouse event
            Point2D pt = new Point2D(event.getX(), event.getY());
            
            //convert this to a map coordinate
            Point mapPoint = mapView.screenToLocation(pt);
  
            //add a feature to be updated
            selectFeature(mapPoint);
          }
        }
      });
      
      // button to update geometry
      btnUpdateGeometry = new Button("Update geometry");
      btnUpdateGeometry.setDisable(true);
      
      // click event for button
      btnUpdateGeometry.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          //update the selected attributes
          updateGeometry();
        }
      });
      
      //hbox to contain buttons
      HBox buttonBox = new HBox();
      buttonBox.getChildren().add(btnUpdateGeometry);
      
      // set background primary color blue
      buttonBox.setStyle("-fx-background-color: #2196F3");
      
      // add the MapView
      borderPane.setCenter(mapView);
      borderPane.setTop(buttonBox);
      
      //generate feature table from service
      damageTable = new ServiceFeatureTable("http://sampleserver6.arcgisonline.com/arcgis/rest/services/DamageAssessment/FeatureServer/0");
      
      //create feature layer from the table
      damageFeatureLayer = new FeatureLayer(damageTable);
      
      //add the layer to the map
      map.getOperationalLayers().add(damageFeatureLayer);
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void stop() throws Exception {
    // release resources when the application closes
    mapView.dispose();
    map.dispose();
    Platform.exit();
    System.exit(0);
  }
  
  private void selectFeature(Point point) {
    //create a buffer from the point which is based on 10 pixels at the current zoom scale
    Polygon searchGeometry = GeometryEngine.buffer(point, mapView.getUnitsPerPixel() * 10);
    
    //create a query
    QueryParameters queryParams = new QueryParameters();
    queryParams.setGeometry(searchGeometry);
    queryParams.setSpatialRelationship(SpatialRelationship.WITHIN);
    
    //select based on the query
    ListenableFuture<FeatureQueryResult> result =  damageFeatureLayer.selectFeatures(queryParams, SelectionMode.NEW);
    
    try {
      //save the selected features
      selectedFeatures = result.get();
      
      //see if there is anything in the list and null it if empty
      if (selectedFeatures.iterator().hasNext()== false) {
        selectedFeatures = null;
        btnUpdateGeometry.setDisable(true);
      } else {
        // we have features so enable the button
        btnUpdateGeometry.setDisable(false);
      }
      
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    
  }
  
  private void updateGeometry() {

    //are there any selected features to update?
    if (selectedFeatures != null) {
      //loop through selected features
      for (Feature feature : selectedFeatures) {
        System.out.println("updating feature");
        
        //move it north a little (20 pixels at this map scale)
        Point currentLoc = (Point) feature.getGeometry();
        Point updatedLoc = new Point(currentLoc.getX(), currentLoc.getY() + (mapView.getUnitsPerPixel() * 20 ), mapView.getSpatialReference());
        feature.setGeometry(updatedLoc);
        
        //update the feature
        ListenableFuture<Boolean> result = damageTable.updateFeatureAsync(feature);
        
        //apply the results to the server if it worked
        result.addDoneListener(new Runnable() {
      
          @Override
          public void run() {
            //apply edits to the server
            applyEdits();
          }});
      }
    }
  }
  
  private void applyEdits() {
    final ListenableFuture<List<FeatureEditResult>> result = damageTable.applyEditsAsync();
    
    result.addDoneListener(new Runnable() {

      @Override
      public void run() {
        //attempt to get the edit results
        try {
          List<FeatureEditResult> editResults = result.get();
          
          //code goes here to examine the edit results
          System.out.println("Results applied to service");
          
        } catch (InterruptedException | ExecutionException e) {
          e.printStackTrace();
        }
        
      }});
  }

  public static void main(String[] args) {
    Application.launch(args);
  }
}
