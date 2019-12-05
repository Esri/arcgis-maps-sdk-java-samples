/*
 * Copyright 2019 Esri.
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

package com.esri.samples.feature_layers;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.ArcGISRuntimeException;
import com.esri.arcgisruntime.arcgisservices.ArcGISMapServiceSublayerInfo;
import com.esri.arcgisruntime.arcgisservices.LabelDefinition;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.SubtypeFeatureLayer;
import com.esri.arcgisruntime.layers.SubtypeSublayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.Renderer;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.symbology.Symbol;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class DisplaySubtypeFeatureLayerSample extends Application {

  private MapView mapView;
  private SubtypeSublayer sublayer;

  @Override
  public void start(Stage stage) {

    try {

      // set the title and size of the stage and show it
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);
      fxScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

      stage.setTitle("Display subtype feature layer");
      stage.setWidth(800);
      stage.setHeight(700);

      // create a JavaFX scene with a stackpane and set it to the stage
      stage.setScene(fxScene);
      stage.show();

      // create a map view and add it to the stack pane
      mapView = new MapView();
      // create a map with streets night vector basemap and add it to the map view
      ArcGISMap map = new ArcGISMap();
      map.setBasemap(Basemap.createStreetsNightVector());
      mapView.setMap(map);


      // show current map scale
      Label currentMapScaleLabel = new Label();
//      currentMapScaleLabel.setTextFill(Color.WHITE);
//      currentMapScaleLabel.setAlignment(Pos.CENTER);
      mapView.addMapScaleChangedListener(mapScaleChangedEvent -> currentMapScaleLabel.setText("Current Map Scale: 1:" + Math.round(mapView.getMapScale())));

      // create a control panel
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0, 0, 0, 0.3)"), CornerRadii.EMPTY, Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(260, 150);
      controlsVBox.setAlignment(Pos.TOP_CENTER);
      controlsVBox.getStyleClass().add("panel-region");

      HBox firstHBox = new HBox(15);
      firstHBox.setAlignment(Pos.CENTER);
      // create checkbox for toggling sublayer visibility
      CheckBox checkBox = new CheckBox("Show sublayer");
      checkBox.setSelected(true);
      
      ToggleButton changeRendererButton = new ToggleButton("Toggle Renderer");
      firstHBox.getChildren().addAll(checkBox, changeRendererButton);

      HBox secondHBox = new HBox(15);
      secondHBox.setAlignment(Pos.CENTER);
      // create label for showing min scale
      Label minScaleLabel = new Label("Sublayer labelling min scale: not set");
      minScaleLabel.setMaxWidth(100);
      minScaleLabel.setWrapText(true);
      Button setMinScaleButton = new Button("Set Minimum Scale");

      secondHBox.getChildren().addAll(minScaleLabel, setMinScaleButton);
      controlsVBox.getChildren().addAll(currentMapScaleLabel, firstHBox, secondHBox);

      Viewpoint initialViewpoint = new Viewpoint(new Envelope(-9812691.11079696, 5128687.20710657, 
        -9812377.9447607, 5128865.36767282, SpatialReferences.getWebMercator()));
      map.setInitialViewpoint(initialViewpoint);
      
      String labelJson = "{ " +
        "\"labelExpression\":\"[nominalvoltage]\",\"labelPlacement\":\"esriServerPointLabelPlacement" +
        "AboveRight\",\"useCodedValues\":true,\"symbol\":{\"angle\":0,\"backgroundColor\":[0,0,0,0],\"borderLineColor\":" +
        "[0,0,0,0],\"borderLineSize\":0,\"color\":[0,0,255,255],\"font\":{\"decoration\":\"none\",\"size\":10.5,\"style\"" +
        ":\"normal\",\"weight\":\"normal\"},\"haloColor\":[255,255,255,255],\"haloSize\":2,\"horizontalAlignment\":\"" +
        "center\",\"kerning\":false,\"type\":\"esriTS\",\"verticalAlignment\":\"middle\",\"xoffset\":0,\"yoffset\":0}}";

      final String serviceFeatureTableUrl = "https://sampleserver7.arcgisonline.com/arcgis/rest/services/UtilityNetwork/NapervilleElectric/FeatureServer/100";
      ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(serviceFeatureTableUrl);
      SubtypeFeatureLayer subtypeFeatureLayer = new SubtypeFeatureLayer(serviceFeatureTable);
      map.getOperationalLayers().add(subtypeFeatureLayer);
      
      subtypeFeatureLayer.loadAsync();
      subtypeFeatureLayer.addDoneLoadingListener(() -> {

        // get the Street Light sublayer and set it up 
        sublayer = subtypeFeatureLayer.getSublayerWithSubtypeName("Street Light");
        sublayer.setLabelsEnabled(true);
        sublayer.getLabelDefinitions().add(LabelDefinition.fromJson(labelJson));
        
        // set visibility of the sublayer
        checkBox.setOnAction(event -> {
          sublayer.setVisible(checkBox.isSelected());
        });

        // change the renderer of the sublayer
        Renderer originalRenderer = sublayer.getRenderer();
        changeRendererButton.setOnAction(event -> {
          
          if (changeRendererButton.isSelected()) { 
          Symbol symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, 0xfff58f84, 20);
          Renderer sublayerRenderer = new SimpleRenderer(symbol);
          sublayer.setRenderer(sublayerRenderer);
          System.out.println("button clicked");
          } else {
            sublayer.setRenderer(originalRenderer);
          }
        });
        
        // set the minimum scale of the labels for the sub layer
        setMinScaleButton.setOnAction(event -> {
          sublayer.setMinScale(mapView.getMapScale());
          minScaleLabel.setText("Sublayer labelling min scale: 1:" + Math.round(sublayer.getMinScale()));
        });
        
        
        
      });

      stackPane.getChildren().addAll(mapView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_RIGHT);
      StackPane.setMargin(controlsVBox, new Insets(50, 10, 0, 0));



    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
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

  /**
   * Opens and runs application.
   *
   * @param args arguments passed to this application
   */
  public static void main(String[] args) {

    Application.launch(args);
  }

}
