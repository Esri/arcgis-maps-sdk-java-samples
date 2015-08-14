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

package com.esri.sampleviewer.samples.editing;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.datasource.Feature;
import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;

public class AddFeatures extends Application {

  private MapView mapView;
  private Map map;
  private ServiceFeatureTable damageTable;
  private FeatureLayer damageFeatureLayer;
  
  @Override
  public void start(Stage stage) throws Exception {
    // create a border pane
    BorderPane borderPane = new BorderPane();
    Scene scene = new Scene(borderPane);

    // size the stage and add a title
    stage.setTitle("Add features : Click on map to add features");
    stage.setWidth(700);
    stage.setHeight(800);
    stage.setScene(scene);
    stage.show();

    // create a Map which defines the layers of data to view
    try {
      map = new Map(Basemap.createStreets());
      
      // create the MapView JavaFX control and assign its map
      mapView = new MapView();
      
      mapView.setOnMouseClicked(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          //create a screen point from the mouse event
          Point2D pt = new Point2D(event.getX(), event.getY());
          
          //convert this to a map coordinate
          Point mapPoint = mapView.screenToLocation(pt);

          //add a feature at this point
         addFeature(mapPoint);

        }
      });
      
      mapView.setMap(map);
      
      // add the MapView
      borderPane.setCenter(mapView);
      
      // initiate drawing of the map control
      mapView.resume();
      
      //generate feature table from service
      damageTable = new ServiceFeatureTable("http://sampleserver6.arcgisonline.com/arcgis/rest/services/DamageAssessment/FeatureServer/0");
      //create feature layer from the table
      damageFeatureLayer = new FeatureLayer(damageTable);
      
      //add the layer to the map
      map.getOperationalLayers().add(damageFeatureLayer);
      
    } catch (Exception e) {
      System.out.println("can't see the map");
      e.printStackTrace();
    }
  }

  @Override
  public void stop() throws Exception {
    // release resources when the application closes
    mapView.dispose();
    map.dispose();
    System.exit(0);
  };
  
  private void addFeature(Point point) {
    System.out.println("adding feature");
    
    //create the attributes for the feature
    java.util.Map<String, Object> attributes = new HashMap<String, Object>();
    attributes.put("typdamage", "Minor");
    attributes.put("primcause" , "Earthquake");
    
    //create a new feature from the attributes and the point
    Feature feature = damageTable.createFeature(attributes, point);
    
    //add the new feature
    final ListenableFuture<Boolean> result = damageTable.addFeatureAsync(feature);
    
    result.addDoneListener(new Runnable() {
      
      @Override
      public void run() {
        //was it successful?
        try {
          if (result.get() == true) {
            System.out.println("Feature added!");
          }
        } catch (InterruptedException e) {
          // Code to catch exception
          e.printStackTrace();
        } catch (ExecutionException e) {
          // Code to catch exception
          e.printStackTrace();
        }
      }
    });
    
    //apply edits to the server
    damageTable.applyEditsAsync();
  }
  
  public static void main(String[] args) {
    Application.launch(args);
  }
}