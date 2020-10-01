/*
 * Copyright 2017 Esri.
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

package com.esri.samples.display_map;

import java.nio.charset.StandardCharsets;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.location.NmeaLocationDataSource;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class DisplayMapSample extends Application {

  private MapView mapView;
  private String nmeaSentence;
  private SerialPort serialPort;
  private NmeaLocationDataSource nmeaLocationDataSource;
  private LocationDisplay locationDisplay;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Display Device Location Test Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();
      scene.getStylesheets().add(getClass().getResource("/display_device_location/style.css").toExternalForm());

      // create combo box
      ComboBox<String> comboBox= new ComboBox<>();
      comboBox.setMaxWidth(Double.MAX_VALUE);
      comboBox.setValue("Stop");

      // add the autopan modes to the combo box
      comboBox.getItems().addAll("Stop", "Navigation" ,"Compass Navigation", "Recenter");

      // add a label
      Label autopanModeLabel = new Label("Choose an autopan mode:");
      // add a checkbox
      CheckBox checkbox = new CheckBox("Show device location");

      // show a background behind the slider
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
        Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(180, 50);
      controlsVBox.getStyleClass().add("panel-region");
      controlsVBox.getChildren().addAll(checkbox, autopanModeLabel, comboBox);

      // create a map with the imagery basemap
      ArcGISMap map = new ArcGISMap(Basemap.createImagery());

      // create a map view and set its map
      mapView = new MapView();
      mapView.setMap(map);

      // make location data source and link to Location Display
      locationDisplay = mapView.getLocationDisplay();

      // make location data source and link to Location Display
      nmeaLocationDataSource = new NmeaLocationDataSource();

      checkbox.setOnAction(event -> {

        if (checkbox.isSelected()) {
          // start location data source and wait for it to be ready
          nmeaLocationDataSource.startAsync();
          nmeaLocationDataSource.addStartedListener(() -> {
            gpsStart();

          });
          // configure the map view's location display to follow the NMEA location data source
          locationDisplay.setLocationDataSource(nmeaLocationDataSource);

        }
        else{
          gpsStop();
          locationDisplay.stop();
        }
      });

      // set the autopan mode of the location display based on the mode chosen from the combo box
      comboBox.getSelectionModel().selectedItemProperty().addListener(e -> {

        if (nmeaLocationDataSource.isStarted()) {
          switch (comboBox.getSelectionModel().getSelectedItem()) {
            case "Navigation":
              locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.NAVIGATION);
              if (locationDisplay.isStarted()){
              locationDisplay.startAsync();
              }
              locationDisplay.setInitialZoomScale(7000);
              break;
            case "Compass Navigation":
              locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
              if (locationDisplay.isStarted()){
                locationDisplay.startAsync();
              }
              locationDisplay.setInitialZoomScale(7000);
              break;
            case "Recenter":
              locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
              if (locationDisplay.isStarted()){
                locationDisplay.startAsync();
              }
              locationDisplay.setInitialZoomScale(7000);
              break;
            case "Stop":
              locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.OFF);
              if (locationDisplay.isStarted()){
                locationDisplay.startAsync();
              }
              locationDisplay.setInitialZoomScale(7000);
              break;
          }
        }
        });

        // add the map view and control panel to the stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  private void gpsStart() {
    System.out.println("gps start");
    // crude way of hooking up to serial port.  Might fail of you have >1!
    serialPort = SerialPort.getCommPorts()[0];
    System.out.println("port name " + serialPort.getSystemPortName() + " open " + serialPort.isOpen());
    //set up the serial port
    serialPort.setComPortParameters(9600,8,1,0);
    serialPort.openPort();
    // set up a listen for new data
    serialPort.addDataListener(new SerialPortDataListener() {
      @Override
      public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; }
      @Override
      public void serialEvent(SerialPortEvent event)
      {
        // is it new data?
        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
          return;
        // read what has come in
        byte[] newData = new byte[serialPort.bytesAvailable()];
        serialPort.readBytes(newData, newData.length);
        // convert byte array to string
        String s = new String(newData, StandardCharsets.UTF_8);
        //System.out.println("string s " + s);
        // as it comes in 1 byte at a time build up the sentence...
        nmeaSentence = nmeaSentence + s;
        // see if we have come up to the end of the sentence
        if (s.contains("\n")) {
          // send the sentence to the location data source for parsing.
          // this could be improved as we are reading in bytes, converting them to a string to build up the sentence, then
          // converting back to a byte array.
          System.out.println("sentence : " + nmeaSentence);
          nmeaLocationDataSource.pushData(nmeaSentence.getBytes());
          // clear the way for a new sentence
          nmeaSentence = "";
        }
      }
    });
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    if (mapView != null) {
      mapView.dispose();
    }

    gpsStop();
  }

  /**
   * Stops the NMEA messages and closes the serial port.
   */
  public void gpsStop(){
    if (serialPort != null) {
      serialPort.closePort();
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
