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

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.datasource.Feature;
import com.esri.arcgisruntime.datasource.FeatureQueryResult;
import com.esri.arcgisruntime.datasource.Field;
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
 * This sample shows how to edit attributes on features and commit the changes back to the feature service.
 */

public class EditAttributes extends Application {

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
    stage.setTitle("Edit features");
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
          //create a screen point from the mouse event
          Point2D pt = new Point2D(event.getX(), event.getY());
          
          //convert this to a map coordinate
          Point mapPoint = mapView.screenToLocation(pt);

          //add a feature to be updated
          selectFeature(mapPoint);
        }
      });
      
      Button btnUpdateAttributes = new Button("Update attrubutes");
      
      btnUpdateAttributes.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          //update the selected attributes
          updateAttributes();
        }
      });
      
      //hbox to contain buttons
      HBox buttonBox = new HBox();
      buttonBox.getChildren().add(btnUpdateAttributes);

      
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
  };

  
  private void selectFeature(Point point) {
    //create a buffer from the point which is based on 10 pixels at the current zoom scale
    Polygon searchGeometry = GeometryEngine.buffer(point, mapView.getUnitsPerPixel() * 10);
    
    //create a query
    QueryParameters queryParams = new QueryParameters();
    queryParams.setGeometry(searchGeometry);
    queryParams.setSpatialRelationship(SpatialRelationship.WITHIN);
    queryParams.getOutFields().clear();
    //queryParams.getOutFields().add("objectid");
    queryParams.getOutFields().add("*");
    
    //select based on the query
    damageFeatureLayer.selectFeatures(queryParams, SelectionMode.NEW);
    
    //TODO: remove this!
    for (Field fld : damageTable.getFields()) {
      System.out.println(" - field = " + fld.getName());
    }
    
  }
  
  private void updateAttributes() {
    //get a list of selected features
    final ListenableFuture<FeatureQueryResult> selected = damageFeatureLayer.getSelectedFeaturesAsync();
    
    selected.addDoneListener(new Runnable() {
      @Override
      public void run() {
        try {
          //loop through selected features
          for (Feature feature : selected.get()) {
            System.out.println("updating feature");
            
            //VIJAY this is where it's going wrong!
            
            for(String k : feature.getAttributes().keySet()) {
              System.out.println("  key = " + k);
            }
            
            //read the current value of the "typdamage" attribute, but it's never returned
            String currentTypDamage = (String) feature.getAttributes().get("typdamage");
            
            System.out.println("current val - " + currentTypDamage);
            
            //change the attribute
            //feature.getAttributes().put("typdamage", "Inaccessible");
            
            //apply edits to the server
            final ListenableFuture<List<FeatureEditResult>> applyResult =  damageTable.applyEditsAsync();
            
            //add a listener to say when it's done or failed
            applyResult.addDoneListener(new Runnable() {

              @Override
              public void run() {
                //get the result
                try {
                  List<FeatureEditResult> editResult = applyResult.get();
                  
                  //code goes here to examine the edit results
                  System.out.println("Results applied to service");
                } catch (InterruptedException | ExecutionException e) {
                  // Code to catch exception state as it didn't work
                  e.printStackTrace();
                } 
              }
            });
            
          }
          
          //commit update operation
          damageTable.applyEditsAsync();
          
        } catch (Exception e) {
          // write error code here
          e.printStackTrace();
        }
      }
    });
  }

  public static void main(String[] args) {
    Application.launch(args);
  }
}