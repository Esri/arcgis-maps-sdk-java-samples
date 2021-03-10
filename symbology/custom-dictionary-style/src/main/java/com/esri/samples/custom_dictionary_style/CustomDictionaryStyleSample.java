/*
 * Copyright 2019 Esri
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

package com.esri.samples.custom_dictionary_style;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.symbology.DictionaryRenderer;
import com.esri.arcgisruntime.symbology.DictionarySymbolStyle;
import com.esri.arcgisruntime.symbology.Renderer;

import java.io.File;
import java.util.HashMap;

public class CustomDictionaryStyleSample extends Application {

  private MapView mapView;
  private DictionarySymbolStyle dictSymbStyleFromPortal; // keep loadables in scope to avoid garbage collection
  private DictionaryRenderer webStyleDictionaryRenderer;

  private VBox controlsVBox;
  private ToggleGroup toggleGroup;
  private RadioButton webStyleButton;
  private RadioButton fileStyleButton;
  private ProgressIndicator progressIndicator;

  @Override
  public void start(Stage stage) {
    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/custom_dictionary_style/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Custom Dictionary Style Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a map with the topographic basemap style
      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);

      // create a map view and set the map to it
      mapView = new MapView();
      mapView.setMap(map);

      // set the initial viewpoint to the Esri Redlands campus
      map.setInitialViewpoint(
        new Viewpoint(new Point(-1.304630524635E7, 4036698.1412000023, map.getSpatialReference()), 5000));

      // set up the UI
      setupUI();

      // enable UI interactions once the map has loaded
      map.addDoneLoadingListener(() -> {
        if (map.getLoadStatus() == LoadStatus.LOADED) {
          controlsVBox.setDisable(false);
        } else {
          new Alert(Alert.AlertType.ERROR, "Map failed to load").show();
        }
      });

      // create a stylex file from a local location
      File stylxFile = new File(System.getProperty("data.dir"), "./samples-data/stylx/Restaurant.stylx");
      // create a dictionary symbol style from the stylx file, and create a new dictionary renderer from it
      DictionarySymbolStyle dictSymbStyleFromFile = DictionarySymbolStyle.createFromFile(stylxFile.getAbsolutePath());
      DictionaryRenderer dictRendFromFile = new DictionaryRenderer(dictSymbStyleFromFile);
      // set the renderer from the style file to the UI
      fileStyleButton.setUserData(dictRendFromFile);

      // create a portal item using the portal and the item id of the dictionary web style
      Portal portal = new Portal("https://arcgisruntime.maps.arcgis.com");
      PortalItem portalItem = new PortalItem(portal, "adee951477014ec68d7cf0ea0579c800");

      // map the input fields in the feature layer to the dictionary symbol style's expected fields for symbols and text
      HashMap<String, String> fieldMap = new HashMap<>();
      fieldMap.put("healthgrade", "Inspection");

      // create a new dictionary symbol style from the web style in the portal item
      dictSymbStyleFromPortal = new DictionarySymbolStyle(portalItem);
      // load the symbol dictionary
      dictSymbStyleFromPortal.loadAsync();

      dictSymbStyleFromPortal.addDoneLoadingListener(() -> {
        if (dictSymbStyleFromPortal.getLoadStatus() == LoadStatus.LOADED) {
          // enable the UI
          controlsVBox.setDisable(false);
          // create a dictionary renderer with the dictionary symbol style,
          // and manually map the feature layer's attribute name to those expected by the dictionary symbol style
          webStyleDictionaryRenderer = new DictionaryRenderer(dictSymbStyleFromPortal, fieldMap, fieldMap);
          // set the renderer from web style to the UI
          webStyleButton.setUserData(webStyleDictionaryRenderer);
          progressIndicator.setVisible(false);
        } else {
          new Alert(Alert.AlertType.ERROR, "Dictionary symbol style failed to load!").show();
        }
      });

      // create a service feature table and create a feature layer from it (restaurant data)
      var serviceFeatureTable =
        new ServiceFeatureTable("https://services2.arcgis.com/ZQgQTuoyBrtmoGdP/ArcGIS/rest/services/Redlands_Restaurants/FeatureServer/0");
      FeatureLayer restaurantLayer = new FeatureLayer(serviceFeatureTable);
      map.getOperationalLayers().add(restaurantLayer);

      // add a listener to the radio button toggle group to change the dictionary renderer
      toggleGroup.selectedToggleProperty().addListener((observable -> {
          if (toggleGroup.getSelectedToggle() != null) {
            // set the chosen dictionary renderer to the feature layer
            restaurantLayer.setRenderer((Renderer) toggleGroup.getSelectedToggle().getUserData());
          }
        })
      );

      // show the style file symbols by default
      toggleGroup.selectToggle(fileStyleButton);

      // add the control panel, map view, and progress indicator to the stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox, progressIndicator);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setAlignment(progressIndicator, Pos.CENTER);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Sets up control panel with radio buttons to toggle between the dictionary symbol styles.
   */
  public void setupUI() {
    // create a label
    Label symbolStyleLabel = new Label("Choose a Dictionary Symbol Style:");

    // create radio buttons for toggling between the web and file styles
    webStyleButton = new RadioButton("Web style");
    fileStyleButton = new RadioButton("Style file ");

    // set the radio buttons to a toggle group
    toggleGroup = new ToggleGroup();
    toggleGroup.getToggles().addAll(webStyleButton, fileStyleButton);

    // show progress indicator when the dictionary symbol styles are loading
    progressIndicator = new ProgressIndicator();
    progressIndicator.setMaxWidth(30);
    progressIndicator.setVisible(false);

    // create a control panel
    controlsVBox = new VBox(6);
    controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
      Insets.EMPTY)));
    controlsVBox.setPadding(new Insets(10.0));
    controlsVBox.setMaxSize(210, 80);
    controlsVBox.getStyleClass().add("panel-region");
    controlsVBox.setDisable(true);
    // add the label and radio buttons to the control panel
    controlsVBox.getChildren().addAll(symbolStyleLabel, webStyleButton, fileStyleButton);
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

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
