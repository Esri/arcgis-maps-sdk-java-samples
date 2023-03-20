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

package com.esri.samples.dynamic_entity_layer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class DynamicEntityLayerSample extends Application {

  private MapView mapView;
  private DynamicEntityLayer dynamicEntityLayer;
  private VBox controlsVBox;
  private Label connectionStatusLabel;
  private Label observationSliderLabel;
  private Button connectionButton;
  private Button purgeButton;
  private CheckBox trackLinesChkBox;
  private CheckBox observationsChkBox;
  private Slider observationsSlider;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Dynamic entity layer sample");
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

      // create the stream service as a dynamic entity data source
      var streamServiceUrl = "https://realtimegis2016.esri.com:6443/arcgis/rest/services/SandyVehicles/StreamServer";
      var streamService = new ArcGISStreamService(streamServiceUrl);

      // add a filter for the data source to limit the amount of data received by the application
      var streamServiceFilter = new ArcGISStreamServiceFilter();
      streamServiceFilter.setGeometry(utahSandyEnvelope);
      streamServiceFilter.setWhereClause("speed > 0");
      streamService.setFilter(streamServiceFilter);

      // set a duration of five minutes for how long observation data is stored in the data source
      streamService.getPurgeOptions().setMaximumDuration(5);

      // create a layer to display the data from the stream service
      dynamicEntityLayer = new DynamicEntityLayer(streamService);

      // create unique value renderers for customizing the dynamic entity layer appearance
      var uniqueValueRenderers = createUniqueRenderers();

      // set the dynamic entity and previous observation renderers
      dynamicEntityLayer.setRenderer(uniqueValueRenderers.get(0));
      dynamicEntityLayer.getTrackDisplayProperties().setPreviousObservationRenderer(uniqueValueRenderers.get(1));

      // create a simple line renderer and set the track line renderer to it
      var trackLineRenderer = new SimpleRenderer(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.LIGHTGRAY, 2));
      dynamicEntityLayer.getTrackDisplayProperties().setTrackLineRenderer(trackLineRenderer);

      // update the label with the service connection status
      connectionStatusLabel.textProperty().bind(Bindings.createStringBinding(() ->
        "Status: " + streamService.connectionStatusProperty().asString().get(), streamService.connectionStatusProperty()));

      // update the button text with the service connection status
      connectionButton.textProperty().bind(Bindings.createStringBinding(() ->
        streamService.getConnectionStatus() == ConnectionStatus.CONNECTED ?
          "Disconnect" : "Connect", streamService.connectionStatusProperty()));

      // toggle the service connection on button press
      connectionButton.setOnAction(a -> {
        if (streamService.getConnectionStatus() == ConnectionStatus.CONNECTED) {
          streamService.disconnectAsync();
        } else {
          streamService.connectAsync();
        }
      });

      // enable or disable track line visibility with the checkbox
      trackLinesChkBox.selectedProperty().bindBidirectional(dynamicEntityLayer.trackDisplayPropertiesProperty().get().
        showTrackLineProperty());

      // enable or disable visibility of previous observation tracks with the checkbox
      observationsChkBox.selectedProperty().bindBidirectional(dynamicEntityLayer.trackDisplayPropertiesProperty().get().
        showPreviousObservationsProperty());

      // update label with the maximum number of observations set in the slider
      observationSliderLabel.textProperty().bind(Bindings.createStringBinding(() -> "Observations per track (" +
        dynamicEntityLayer.trackDisplayPropertiesProperty().getValue().maximumObservationsProperty().asString().get() + ")",
        dynamicEntityLayer.trackDisplayPropertiesProperty().getValue().maximumObservationsProperty()));

      // update the slider value and maximum previous observation tracks simultaneously
      observationsSlider.valueProperty().bindBidirectional(dynamicEntityLayer.trackDisplayPropertiesProperty().
        getValue().maximumObservationsProperty());

      // disable the slider when the observations checkbox is not selected
      observationsSlider.disableProperty().bind(observationsChkBox.selectedProperty().not());

      // purge all observations on button press
      purgeButton.setOnAction(a -> streamService.purgeAllAsync());

      // add the map dynamic entity layer to the map's operational layers
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
   * Creates and returns renderers for the entities and observations.
   */
  private ArrayList<UniqueValueRenderer> createUniqueRenderers() {

    // create lists to hold the unique values for the dynamic entities and previous observations and the renderers
    var entityValues = new ArrayList<UniqueValueRenderer.UniqueValue>();
    var observationValues = new ArrayList<UniqueValueRenderer.UniqueValue>();
    var uniqueValueRenderers = new ArrayList<UniqueValueRenderer>();

    // create simple marker symbols where the pink and lime symbols represent the agencies "3" and "4", respectively
    // create a blue simple marker symbol to be used as alternate and default symbol
    var biggerSimplePinkSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.PINK, 8);
    var biggerSimpleLimeSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.LIME, 8);
    var biggerSimpleBlueSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.BLUE, 8);

    // create simple marker symbols with a smaller size for the previous observation tracks
    var smallerSimplePinkSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.PINK, 3);
    var smallerSimpleLimeSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.LIME, 3);
    var smallerSimpleBlueSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.BLUE, 3);

    // create a pink unique value for the agency represented by value "3" and add it to the entity list
    entityValues.add(new UniqueValueRenderer.UniqueValue("", "",
      biggerSimplePinkSymbol, List.of(3), List.of(biggerSimpleBlueSymbol)));

    // create a lime unique value for the agency represented by value "4" and add it to the entity list
    entityValues.add(new UniqueValueRenderer.UniqueValue("", "",
      biggerSimpleLimeSymbol, List.of(4), List.of(biggerSimpleBlueSymbol)));

    // create a pink unique value for the agency represented by value "3" and add it to the observation list
    observationValues.add(new UniqueValueRenderer.UniqueValue("", "",
      smallerSimplePinkSymbol, List.of(3), List.of(smallerSimpleBlueSymbol)));

    // create a lime unique value for the agency represented by value "4" and add it to the observation list
    observationValues.add(new UniqueValueRenderer.UniqueValue("", "",
      smallerSimpleLimeSymbol, List.of(4), List.of(smallerSimpleBlueSymbol)));

    // create unique value renderers for the entities and tracks
    var dynamicEntityRenderer = new UniqueValueRenderer(List.of("agency"),
      entityValues, "", biggerSimpleBlueSymbol);
    var previousObservationRenderer = new UniqueValueRenderer(List.of("agency"),
      observationValues, "", smallerSimpleBlueSymbol);

    // add the unique value renderers to the list
    uniqueValueRenderers.add(dynamicEntityRenderer);
    uniqueValueRenderers.add(previousObservationRenderer);

    return uniqueValueRenderers;
  }

  /**
   * Creates user interface with labels, buttons, checkbox, and slider.
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
    trackLinesChkBox = new CheckBox("Track lines");
    observationsChkBox = new CheckBox("Previous observations");

    // create a slider to update the number of previous observations
    observationsSlider = new Slider(1, 5, 5);

    // create hbox and add the observations label and slider
    HBox sliderHBox = new HBox();
    sliderHBox.setSpacing(3.0);
    sliderHBox.getChildren().addAll(observationSliderLabel, observationsSlider);

    // create a vbox and add the labels, checkbox, hbox, and buttons
    controlsVBox = new VBox();
    controlsVBox.setPadding(new Insets(15.0));
    controlsVBox.setSpacing(5.0);
    controlsVBox.setMaxSize(325, 120);
    controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(255,255,255,1)"),
      CornerRadii.EMPTY, Insets.EMPTY)));
    controlsVBox.getChildren().addAll(connectionStatusLabel, connectionButton, trackLinesChkBox,
      observationsChkBox, sliderHBox, purgeButton);
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
