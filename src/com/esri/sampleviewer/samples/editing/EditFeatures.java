package com.esri.sampleviewer.samples.editing;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.datasource.Feature;
import com.esri.arcgisruntime.datasource.FeatureQueryResult;
import com.esri.arcgisruntime.datasource.QueryParameters;
import com.esri.arcgisruntime.datasource.QueryParameters.SpatialRelationship;
import com.esri.arcgisruntime.datasource.arcgis.ArcGISFeature;
import com.esri.arcgisruntime.datasource.arcgis.AttachmentInfo;
import com.esri.arcgisruntime.datasource.arcgis.FeatureEditResult;
import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.FeatureLayer.SelectionMode;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.BasemapType;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.RgbColor;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;


public class EditFeatures extends Application {

  private MapView mapView;
  private Map map;
  private SpatialReference wgs84 = SpatialReference.create(4326);
  private ServiceFeatureTable damageTable;
  private FeatureLayer damageFeatureLayer;
  
  private ToggleGroup editGroup;
  
  private enum EDITMODE {ADD,SELECT};
  
  private EDITMODE editMode = EDITMODE.ADD;

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
      //map = new Map();
     
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
          
          //are we adding features
          switch (editMode) {
            case SELECT:
              selectFeature(mapPoint);
              break;
            case ADD:
              addFeature(mapPoint);
              break;
          }
        }
      });
      
      mapView.setMap(map);
      
      //set up some buttons etc
      editGroup = new ToggleGroup();
      RadioButton rdoAddFeature = new RadioButton("Add features");
      rdoAddFeature.setToggleGroup(editGroup);
      rdoAddFeature.setSelected(true);
      rdoAddFeature.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent arg0) {
          editMode = EDITMODE.ADD;
        }
      });
      
      RadioButton rdoSelectFeatures = new RadioButton("Select features");
      rdoSelectFeatures.setToggleGroup(editGroup);
      rdoSelectFeatures.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent arg0) {
          editMode = EDITMODE.SELECT;
        }
      });
      
      Button btnDeleteFeatures = new Button("Delete selected features");
      
      btnDeleteFeatures.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          deleteFeatures();
        }
      });
      
      Button btnUpdateGeometry = new Button("Update geometry");
      
      btnUpdateGeometry.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          updateGeometry();
        }
      });
      
      Button btnUpdateAttributes = new Button("Update attrubutes");
      
      btnUpdateAttributes.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          updateAttributes();
        }
      });
      
      Button btnAddAttachment = new Button("Add attachment");
      
      btnAddAttachment.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          addAttachment();
        }
      });
      
      
      //hbox to contain buttons
      HBox buttonBox = new HBox();
      buttonBox.getChildren().add(rdoAddFeature);
      buttonBox.getChildren().add(rdoSelectFeatures);
      buttonBox.getChildren().add(btnDeleteFeatures);
      buttonBox.getChildren().add(btnUpdateAttributes);
      buttonBox.getChildren().add(btnUpdateGeometry);
      buttonBox.getChildren().add(btnAddAttachment);
      
      // add the MapView
      borderPane.setCenter(mapView);
      borderPane.setTop(buttonBox);
      
      // initiate drawing of the map control - this is going to need to change!
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
            System.out.println("success");
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
  
  private void selectFeature(Point point) {
    
    //create a buffer from the point
    Polygon searchGeometry = GeometryEngine.buffer(point, 5000);
    
    //create a query
    QueryParameters queryParams = new QueryParameters();
    queryParams.setGeometry(searchGeometry);
    queryParams.setSpatialRelationship(SpatialRelationship.WITHIN);
    
    //select based on the query
    damageFeatureLayer.selectFeatures(queryParams, SelectionMode.NEW);
    
  }
  
  private void deleteFeatures() {
    //get a list of selected features
    final ListenableFuture<FeatureQueryResult> selected = damageFeatureLayer.getSelectedFeaturesAsync();
    
    selected.addDoneListener(new Runnable() {
      @Override
      public void run() {
        try {
          //delete features
          damageTable.deleteFeaturesAsync(selected.get());
          
          //commit delete operation
          damageTable.applyEditsAsync();
          
        } catch (Exception e) {
          // write error code here
          e.printStackTrace();
        }
      }
    });
  }
  
  private void updateGeometry() {
    
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
            //change the attribute
            feature.getAttributes().put("typdamage", "Inaccessible");
            
            //move it north a little
            Point currentLoc = (Point) feature.getGeometry();
            Point updatedLoc = new Point(currentLoc.getX(), currentLoc.getY() + 50000, mapView.getSpatialReference());
            feature.setGeometry(updatedLoc);
            
            //update the feature
            damageTable.updateFeatureAsync(feature);
            
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
  
  private void addAttachment() {
    //get a list of selected features
    final ListenableFuture<FeatureQueryResult> selected = damageFeatureLayer.getSelectedFeaturesAsync();
    
    selected.addDoneListener(new Runnable() {
      @Override
      public void run() {
        try {
          //loop through selected features
          for (Feature feature : selected.get()) {
            System.out.println("adding feature attachment");

            //get as an ArcGIS Feature so we can add attachments
            ArcGISFeature agsFeature = (ArcGISFeature) feature;
            
            
            
            final ListenableFuture<AttachmentInfo> attInfo = 
                agsFeature.addAttachmentAsync(
                    new File("C:\\Users\\mark4666\\Desktop\\Project.png"), "image/jpg", "assessment imaqe.png");
            
            
            //listen into the results
            attInfo.addDoneListener(new Runnable() {
              
              @Override
              public void run() {
                try {
                  //attachment uploaded
                  System.out.println("it was attached!");
                  
                  
                  
                } catch (Exception e) {
                  // upload failed
                  e.printStackTrace();
                } 
                
              }
            });
            
            //commit to service
            ListenableFuture<Boolean> result = damageTable.updateFeatureAsync(agsFeature);
            
            result.get();
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