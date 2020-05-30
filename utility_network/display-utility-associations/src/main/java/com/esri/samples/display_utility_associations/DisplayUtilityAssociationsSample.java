package com.esri.samples.display_utility_associations;/*
 * Copyright 2020 Esri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.UniqueValueRenderer;
import com.esri.arcgisruntime.utilitynetworks.UtilityAssociation;
import com.esri.arcgisruntime.utilitynetworks.UtilityAssociationType;
import com.esri.arcgisruntime.utilitynetworks.UtilityNetwork;
import com.esri.arcgisruntime.utilitynetworks.UtilityNetworkSource;

public class DisplayUtilityAssociationsSample extends Application {
  
  //Feature server for the utility network
  private static final String FeatureServerUrl = "https://sampleserver7.arcgisonline.com/arcgis/rest/services/UtilityNetwork/NapervilleElectric/FeatureServer";
  private MapView mapView;
  private GraphicsOverlay associationsOverlay;
  private UtilityNetwork utilityNetwork;
  
  // max scale at which to create graphics for the associations
  private int maxScale = 2000;
  
  /**
   * Opens and runs application.
   *
   * @param args arguments passed to this application
   */
  public static void main(String[] args) {
    
    Application.launch(args);
  }
  
  @Override
  public void start(Stage stage) {
    
    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      
      // set title, size, and add scene to stage
      stage.setTitle("Display Utility Association Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();
      
      // create a map with the Topographic Vector Basemap
      ArcGISMap map = new ArcGISMap(Basemap.createTopographicVector());
      
      // create a viewpoint from envelope
      Viewpoint viewPoint = new Viewpoint(41.8057655, -88.1489692, 23);
      
      // create the utility network
      utilityNetwork = new UtilityNetwork(FeatureServerUrl);
      
      // Load utility network async and get all of the edges and junctions in the network.
      utilityNetwork.loadAsync();
      utilityNetwork.addDoneLoadingListener(() -> {
        List<UtilityNetworkSource> networkSources = utilityNetwork.getDefinition().getNetworkSources();
        
        for (UtilityNetworkSource nSource : networkSources)
          // Check an add all edges that are not subnet lines to the map.
          if (nSource.getSourceType() == UtilityNetworkSource.Type.EDGE && nSource.getSourceUsageType() != UtilityNetworkSource.UsageType.SUBNET_LINE) {
            mapView.getMap().getOperationalLayers().add(new FeatureLayer(nSource.getFeatureTable()));
          }
          // Add all junctions to the map.
          else if (nSource.getSourceType() == UtilityNetworkSource.Type.JUNCTION) {
            mapView.getMap().getOperationalLayers().add(new FeatureLayer(nSource.getFeatureTable()));
          }
          
        // add association graphics at the initial view point
        addAssociation();
        // listen for navigation changes
        mapView.addNavigationChangedListener((event) -> {
          addAssociation();
        });
      });

      // create ImageView for teo symbol
      ImageView attachmentImageView = new ImageView();
      ImageView connectivityImageView = new ImageView();
      
      // Create a graphics overlay for associations.
      associationsOverlay = new GraphicsOverlay();
      
      // Create Labels for the preview.
      Label attachmentLabel = new Label("Attachment");
      Label connectivityLabel = new Label("Connectivity");
  
      // Create Symbols for the associations and preview.
      SimpleLineSymbol attachmentSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DOT, 0xFF00FF00, 5);
      SimpleLineSymbol connectivitySymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DOT, 0xFFFF0000, 5);
  
      // get the symbol from the attachmentSymbol
      ListenableFuture<Image> attachmentBitmap = attachmentSymbol.createSwatchAsync(0x00000000);
      attachmentBitmap.addDoneListener(() -> {
        try {
          // display the image view in the preview area
          attachmentImageView.setImage(attachmentBitmap.get());
        } catch (InterruptedException | ExecutionException e) {
          new Alert(Alert.AlertType.ERROR, "Error creating preview ImageView from provided attachmentSymbol" + e.getMessage()).show();
        }
      });
  
      // get the symbol from the connectivitySymbol
      ListenableFuture<Image> connectivityBitmap = connectivitySymbol.createSwatchAsync(0x00000000);
      connectivityBitmap.addDoneListener(() -> {
        try {
          // display the image view in the preview area
          connectivityImageView.setImage(connectivityBitmap.get());
        } catch (InterruptedException | ExecutionException e) {
          new Alert(Alert.AlertType.ERROR, "Error creating preview ImageView from provided connectivitySymbol" + e.getMessage()).show();
        }
      });
      
      
      // Create a renderer for the associations.
      UniqueValueRenderer.UniqueValue attachmentUV = new UniqueValueRenderer.UniqueValue("Attachment", "", attachmentSymbol, Arrays.asList(UtilityAssociationType.ATTACHMENT.toString()));
      UniqueValueRenderer.UniqueValue connectivityUV = new UniqueValueRenderer.UniqueValue("Connectivity", "", connectivitySymbol, Arrays.asList(UtilityAssociationType.CONNECTIVITY.toString()));
      associationsOverlay.setRenderer(new UniqueValueRenderer(Arrays.asList("AssociationType"), Arrays.asList(attachmentUV, connectivityUV), "", null));
      
      // set initial ArcGISMap extent
      map.setInitialViewpoint(viewPoint);
      // create a map view and set its map
      mapView = new MapView();
      mapView.setMap(map);
      mapView.getGraphicsOverlays().add(associationsOverlay);
      
      // create a controls box
      VBox controlsVBox = new VBox();
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(255,255,255,0.5)"), CornerRadii.EMPTY,
        Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setSpacing(5);
      controlsVBox.setMaxSize(100, 50);
      controlsVBox.setAlignment(Pos.BASELINE_LEFT);
      controlsVBox.getStyleClass().add("panel-region");
      controlsVBox.getChildren().addAll(attachmentLabel, attachmentImageView, connectivityLabel, connectivityImageView);
      
      // add the scene view and controls to the stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));
      
      
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
    
  }
  
  private void addAssociation() {
    try {
      // Check if the current viewpoint is outside of the max scale.
      if (mapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetScale() >= maxScale)
        return;
      
      // Check if the current viewpoint has an extent.
      Envelope extent = mapView.getCurrentViewpoint(Viewpoint.Type.BOUNDING_GEOMETRY).getTargetGeometry().getExtent();
      if (extent == null) {
        return;
      }
      
      // Get all of the associations in extent of the viewpoint
      ListenableFuture<List<UtilityAssociation>> associationsFutureResult = utilityNetwork.getAssociationsAsync(extent);
      
      associationsFutureResult.addDoneListener(() -> {
        
        try {
          List<UtilityAssociation> associations = associationsFutureResult.get();
          
          associations.forEach(association -> {
            // Check if the graphics overlay already contains the association.
            if (associationsOverlay.getGraphics().stream().noneMatch(g -> g.getAttributes().containsKey("GlobalId") && g.getAttributes().get("GlobalId") == association.getGlobalId())) {
              
              if (association.getGeometry() != null) {
                // Create and add a graphic for the association.
                Map<String, Object> attributes = Map.of("GlobalId", association.getGlobalId(), "AssociationType", association.getAssociationType().toString());
                Graphic newGraphic = new Graphic(association.getGeometry(), attributes);
                associationsOverlay.getGraphics().add(newGraphic);
              }
            }
          });
        } catch (InterruptedException | ExecutionException e) {
          new Alert(Alert.AlertType.ERROR, "Error " + e.getMessage()).show();
        }
        
      });
    }
    // This is thrown when there are too many associations in the extent.
    catch (Exception e) {
      new Alert(Alert.AlertType.ERROR, "too many associations" + e.getMessage()).show();
    }
  }
  
  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {
    
    if (mapView != null) {
      mapView.dispose();
    }
  }
  
}
