/*
 * Copyright 2018 Esri.
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

package com.esri.samples.change_sublayer_renderer;

import java.util.Arrays;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISMapImageSublayer;
import com.esri.arcgisruntime.layers.SublayerList;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.ClassBreaksRenderer;
import com.esri.arcgisruntime.symbology.ClassBreaksRenderer.ClassBreak;
import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;

public class ChangeSublayerRendererSample extends Application {

  private ArcGISMapImageLayer imageLayer; // keep loadable in scope to avoid garbage collection
  private ArcGISMapImageSublayer countiesSublayer;
  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create a border pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/change_sublayer_renderer/style.css").toExternalForm());

      // size the stage and add a title
      stage.setTitle("Change Sublayer Renderer Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a map with the streets basemap style
      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_STREETS);

      // create a map view and set the map to it
      mapView = new MapView();
      mapView.setMap(map);

      // set a viewpoint on the map view
      mapView.setViewpoint(new Viewpoint(48.354406, -99.998267, 147914382));

      // create a button to apply the render (set up later)
      Button rendererButton = new Button("Change sublayer renderer");
      // disable until the sublayer is loaded
      rendererButton.setDisable(true);

      // create a map image layer from a service URL
      imageLayer = new ArcGISMapImageLayer("https://sampleserver6.arcgisonline.com/arcgis/rest/services/Census/MapServer");

      // load the layer and find one of its sublayers
      imageLayer.addDoneLoadingListener(() -> {
        if (imageLayer.getLoadStatus() == LoadStatus.LOADED) {
          // zoom to the image layers extent
          mapView.setViewpointGeometryAsync(imageLayer.getFullExtent());
          // get the sublayers from the map image layer
          SublayerList sublayers = imageLayer.getSublayers();
          countiesSublayer = (ArcGISMapImageSublayer) sublayers.get(2);
          // enable the change renderer button
          rendererButton.setDisable(false);
        } else {
          new Alert(Alert.AlertType.ERROR, imageLayer.getLoadError().getMessage());
        }
      });

      // add the map image layer to the map's operational layers
      map.getOperationalLayers().add(imageLayer);

      // create a class breaks renderer to switch to
      ClassBreaksRenderer classBreaksRenderer = createPopulationClassBreaksRenderer();

      // set the renderer on the counties sublayer when the button is pressed
      rendererButton.setOnAction(e -> {
        countiesSublayer.setRenderer(classBreaksRenderer);
        // disable the button
        rendererButton.setDisable(true);
      });

      // add the map view and button to the stack pane
      stackPane.getChildren().addAll(mapView, rendererButton);
      StackPane.setAlignment(rendererButton, Pos.TOP_LEFT);
      StackPane.setMargin(rendererButton, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Creates a class breaks renderer for 5 ranges of populations.
   *
   * @return class breaks renderer
   */
  private ClassBreaksRenderer createPopulationClassBreaksRenderer() {

    // create colors
    final int gray = ColorUtil.colorToArgb(Color.color(0.6, 0.6, 0.6, 1.0));
    final int blue1 = ColorUtil.colorToArgb(Color.color(0.89, 0.92, 0.81, 1.0));
    final int blue2 = ColorUtil.colorToArgb(Color.color(0.59, 0.76, 0.75, 1.0));
    final int blue3 = ColorUtil.colorToArgb(Color.color(0.38, 0.65, 0.71, 1.0));
    final int blue4 = ColorUtil.colorToArgb(Color.color(0.27, 0.49, 0.59, 1.0));
    final int blue5 = ColorUtil.colorToArgb(Color.color(0.16, 0.33, 0.47, 1.0));

    // create 5 fill symbols with different shades of blue and a gray outline
    SimpleLineSymbol outline = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, gray, 1);
    SimpleFillSymbol classSymbol1 = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, blue1, outline);
    SimpleFillSymbol classSymbol2 = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, blue2, outline);
    SimpleFillSymbol classSymbol3 = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, blue3, outline);
    SimpleFillSymbol classSymbol4 = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, blue4, outline);
    SimpleFillSymbol classSymbol5 = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, blue5, outline);

    // create 5 classes for different population ranges
    ClassBreak classBreak1 = new ClassBreak( "-99 to 8560", "-99 to 8560", -99, 8560, classSymbol1);
    ClassBreak classBreak2 = new ClassBreak("> 8,560 to 18,109", "> 8,560 to 18,109", 8560, 18109, classSymbol2);
    ClassBreak classBreak3 = new ClassBreak("> 18,109 to 35,501", "> 18,109 to 35,501", 18109, 35501,
        classSymbol3);
    ClassBreak classBreak4 = new ClassBreak( "> 35,501 to 86,100", "> 35,501 to 86,100", 35501, 86100,
        classSymbol4);
    ClassBreak classBreak5 = new ClassBreak( "> 86,100 to 10,110,975", "> 86,100 to 10,110,975",  86100, 10110975,
        classSymbol5);

    // create the renderer for the POP2007 field
    return new ClassBreaksRenderer("POP2007", Arrays.asList(classBreak1, classBreak2, classBreak3, classBreak4,
        classBreak5));
  }

  @Override
  public void stop() {

    // releases resources when the application closes
    if (mapView != null) {
      mapView.dispose();
    }
  }

  /**
   * Starting point of this application.
   *
   * @param args arguments to this application.
   */
  public static void main(String[] args) {

    Application.launch(args);
  }
}
