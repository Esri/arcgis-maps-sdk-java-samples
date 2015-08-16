package com.esri.sampleviewer.samples.editing;

import javafx.application.Application;
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
import com.esri.arcgisruntime.datasource.FeatureQueryResult;
import com.esri.arcgisruntime.datasource.QueryParameters;
import com.esri.arcgisruntime.datasource.QueryParameters.SpatialRelationship;
import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.FeatureLayer.SelectionMode;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;



public class DeleteFeatures extends Application {

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
    stage.setTitle("Delete features : Click on map to select features and press the delete button!");
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

          //select features at this point
          selectFeature(mapPoint);
        }
      });
      
      mapView.setMap(map);
      
      //set up some buttons etc

      
      Button btnDeleteFeatures = new Button("Delete selected features");
      
      btnDeleteFeatures.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          deleteFeatures();
        }
      });
      

      
      
      //hbox to contain buttons
      HBox buttonBox = new HBox();
      buttonBox.getChildren().add(btnDeleteFeatures);
      
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
  



  public static void main(String[] args) {
    Application.launch(args);
  }
}