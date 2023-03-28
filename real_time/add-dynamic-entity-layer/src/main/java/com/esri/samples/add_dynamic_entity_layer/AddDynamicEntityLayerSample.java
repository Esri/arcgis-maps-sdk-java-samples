/*
 * Copyright 2023 Esri.
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

package com.esri.samples.add_dynamic_entity_layer;

import java.util.List;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.DynamicEntityLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.realtime.ArcGISStreamService;
import com.esri.arcgisruntime.realtime.ArcGISStreamServiceFilter;
import com.esri.arcgisruntime.realtime.ConnectionStatus;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.symbology.UniqueValueRenderer;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AddDynamicEntityLayerSample extends Application {

  private MapView mapView;
  private VBox controlsVBox;
  private Label connectionStatusLabel;
  private Label observationSliderLabel;
  private Button connectionButton;
  private Button purgeButton;
  private CheckBox trackLinesCheckBox;
  private CheckBox observationsCheckBox;
  private Slider observationsSlider;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Add dynamic entity layer sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a map with the dark gray basemap style
      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_DARK_GRAY_BASE);

      // create a map view and set the map to it
      mapView = new MapView();
      mapView.setMap(map);

      // create an envelope around Sandy, Utah to be the extent used by the dynamic entity filter and viewpoint
      var utahSandyEnvelope = new Envelope(new Point(-112.110052, 40.718083, SpatialReferences.getWgs84()),
        new Point(-111.814782, 40.535247, SpatialReferences.getWgs84()));

      // set the viewpoint to the map view
      mapView.setViewpoint(new Viewpoint(utahSandyEnvelope));

      // create a graphics overlay and add a graphic to show the envelope's extent using a simple line symbol
      var borderLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, Color.RED, 2);
      var graphicsOverlay = new GraphicsOverlay();
      var graphic = new Graphic(utahSandyEnvelope, borderLineSymbol);
      graphicsOverlay.getGraphics().add(graphic);

      // add the graphics overlay to the map view
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create a controls vbox containing the UI components
      setUpControlsVBox();

      // create an ArcGIS stream service using a stream service URL
      var streamServiceUrl = "https://realtimegis2016.esri.com:6443/arcgis/rest/services/SandyVehicles/StreamServer";
      var streamService = new ArcGISStreamService(streamServiceUrl);

      // add a filter for the data source to limit the amount of data received by the application
      var streamServiceFilter = new ArcGISStreamServiceFilter();
      streamServiceFilter.setGeometry(utahSandyEnvelope);
      streamServiceFilter.setWhereClause("speed > 0");
      streamService.setFilter(streamServiceFilter);

      // set a duration of five seconds for how long observation data is stored in the data source
      streamService.getPurgeOptions().setMaximumDuration(5);

      // create a dynamic entity layer to display the data from the stream service
      var dynamicEntityLayer = new DynamicEntityLayer(streamService);

      // create unique values for the different snow plow operating agencies represented in the data
      // create a blue symbol to use as the default
      var entityBlueSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.BLUE, 8);

      // create a unique value with a pink marker symbol for the agency represented by value "3"
      var entityPinkSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.PINK, 8);
      var entityPinkValue = new UniqueValueRenderer.UniqueValue(null, null, entityPinkSymbol,
        List.of(3), List.of(entityBlueSymbol));

      // create a unique value with a lime marker symbol for the agency represented by value "4"
      var entityLimeSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.LIME, 8);
      var entityLimeValue = new UniqueValueRenderer.UniqueValue(null, null, entityLimeSymbol,
        List.of(4), List.of(entityBlueSymbol));

      // create a unique value renderer using the configured unique values
      var entityRenderer = new UniqueValueRenderer(List.of("agency"),
        List.of(entityPinkValue, entityLimeValue), null, entityBlueSymbol);

      // set the renderer to the dynamic entity layer
      dynamicEntityLayer.setRenderer(entityRenderer);

      // create unique values to represent observation tracks for the different snow plow operating agencies represented
      // in the data. Create a blue symbol to use as the default
      var previousObservationBlueSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.BLUE, 3);

      // create a unique value with a pink marker symbol for the agency represented by value "3"
      var previousObservationPinkSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.PINK, 3);
      var previousObservationPinkValue = new UniqueValueRenderer.UniqueValue(null, null, previousObservationPinkSymbol,
        List.of(3), List.of(previousObservationBlueSymbol));

      // create a unique value with a lime marker symbol for the agency represented by value "4"
      var previousObservationLimeSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.LIME, 3);
      var previousObservationLimeValue = new UniqueValueRenderer.UniqueValue(null, null, previousObservationLimeSymbol,
        List.of(4), List.of(previousObservationBlueSymbol));

      // create a unique value renderer for the previous observations using the unique values for the different snow plow
      // operating agencies represented in the data
      var previousObservationsRenderer = new UniqueValueRenderer(List.of("agency"),
        List.of(previousObservationPinkValue, previousObservationLimeValue), null, previousObservationBlueSymbol);

      // set the renderer to the dynamic entity layer display properties
      dynamicEntityLayer.getTrackDisplayProperties().setPreviousObservationRenderer(previousObservationsRenderer);

      // create a simple renderer to represent track lines and set it as the track line renderer on the dynamic entity
      // layer display properties
      var trackLineRenderer = new SimpleRenderer(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.LIGHTGRAY, 2));
      dynamicEntityLayer.getTrackDisplayProperties().setTrackLineRenderer(trackLineRenderer);

      // bind the label text property to the connection status property value
      connectionStatusLabel.textProperty().bind(Bindings.createStringBinding(() ->
        "Status: " + streamService.connectionStatusProperty().getValue(), streamService.connectionStatusProperty()));

      // update the button text depending on the connection status value
      connectionButton.textProperty().bind(Bindings.createStringBinding(() -> {
        if (streamService.getConnectionStatus() == ConnectionStatus.CONNECTED) {
          return "Disconnect";
        } else {
          return "Connect";
        }
      }, streamService.connectionStatusProperty()));

      // toggle the service connection on button press
      connectionButton.setOnAction(a -> {
        if (streamService.getConnectionStatus() == ConnectionStatus.CONNECTED) {
          streamService.disconnectAsync();
        } else {
          streamService.connectAsync();
        }
      });

      // get the track display properties property value to control the display of previous observations and track lines
      var layerTrackDisplayProperties = dynamicEntityLayer.getTrackDisplayProperties();

      // configure checkboxes to control the visibility of track lines and previous observations
      trackLinesCheckBox.selectedProperty().bindBidirectional(layerTrackDisplayProperties.showTrackLineProperty());
      observationsCheckBox.selectedProperty().bindBidirectional(layerTrackDisplayProperties.showPreviousObservationsProperty());

      // configure the observations label and slider to update and control the maximum number of observations
      observationSliderLabel.textProperty().bind(Bindings.createStringBinding(() ->
        "Observations per track (" + layerTrackDisplayProperties.maximumObservationsProperty().getValue() + ")",
        layerTrackDisplayProperties.maximumObservationsProperty()));

      observationsSlider.valueProperty().bindBidirectional(layerTrackDisplayProperties.maximumObservationsProperty());

      // bind the slider disable property to the selected property of the observations checkbox
      // disable the slider if the observations checkbox is not selected
      observationsSlider.disableProperty().bind(observationsCheckBox.selectedProperty().not());

      // purge all observations on button press
      purgeButton.setOnAction(a -> streamService.purgeAllAsync());

      // add the dynamic entity layer to the map's operational layers
      map.getOperationalLayers().add(dynamicEntityLayer);

      // add the map view and controls vbox to the stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_RIGHT);
      StackPane.setMargin(controlsVBox, new Insets(10, 10, 0, 10));
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Configure a VBox for the UI, containing labels, buttons, checkbox, and slider.
   */
  private void setUpControlsVBox() {

    // create labels for the connection status and the observations per track slider
    connectionStatusLabel = new Label("Status: ");
    observationSliderLabel = new Label("Observations per track: ");

    // create buttons to toggle the service connection and purge observations
    connectionButton = new Button();
    connectionButton.setMaxWidth(Double.MAX_VALUE);
    purgeButton = new Button("Purge all observations");
    purgeButton.setMaxWidth(Double.MAX_VALUE);

    // create checkbox to toggle visibility of track lines and previous observations
    trackLinesCheckBox = new CheckBox("Track lines");
    observationsCheckBox = new CheckBox("Previous observations");

    // create a slider to update the number of previous observations
    observationsSlider = new Slider(1, 5, 5);

    // create hbox and add the observations label and slider
    var sliderHBox = new HBox();
    sliderHBox.setSpacing(3.0);
    sliderHBox.getChildren().addAll(observationSliderLabel, observationsSlider);

    // create a vbox and add the labels, checkbox, hbox, and buttons
    controlsVBox = new VBox();
    controlsVBox.setPadding(new Insets(15.0));
    controlsVBox.setSpacing(5.0);
    controlsVBox.setMaxSize(325, 120);
    controlsVBox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
    controlsVBox.getChildren().addAll(connectionStatusLabel, connectionButton, trackLinesCheckBox,
      observationsCheckBox, sliderHBox, purgeButton);
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
