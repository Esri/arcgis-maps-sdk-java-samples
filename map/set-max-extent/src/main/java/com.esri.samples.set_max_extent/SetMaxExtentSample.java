/*
 * Copyright 2022 Esri.
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

package com.esri.samples.set_max_extent;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.symbology.*;
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.view.MapView;

public class SetMaxExtentSample extends Application {

    private MapView mapView;


    @Override
    public void start(Stage stage) {

        try {
            // create stack pane and application scene
            var stackPane = new StackPane();
            var scene = new Scene(stackPane);

            // set title, size, and add scene to stage
            stage.setTitle("Set Max Extent Sample");
            stage.setWidth(800);
            stage.setHeight(700);
            stage.setScene(scene);
            stage.show();

            // authentication with an API key or named user is required to access basemaps and other location services
            String yourAPIKey = System.getProperty("apiKey");
            ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

            // create a map with the streets basemap style
            var map = new ArcGISMap(BasemapStyle.ARCGIS_STREETS);

            // create a map view and set the map to it
            mapView = new MapView();
            mapView.setMap(map);

            // create a max extent envelope with points on Colorado's northwest and southeast corners
            var coloradoNW = new Point(-12139393.2109, 5012444.0468, SpatialReferences.getWebMercator());
            var coloradoSE = new Point(-11359277.5124, 4438148.7816, SpatialReferences.getWebMercator());
            var extentEnvelope = new Envelope(coloradoNW, coloradoSE);

            // set the map's max extent to the max extent envelope
            map.setMaxExtent(extentEnvelope);

            // create a graphics overlay of the map's max extent and add it to the map view
            var coloradoGraphicsOverlay = new GraphicsOverlay();
            coloradoGraphicsOverlay.getGraphics().add(new Graphic(map.getMaxExtent()));
            mapView.getGraphicsOverlays().add(coloradoGraphicsOverlay);

            // create a simple renderer with a red dashed line and set it to be the renderer for the graphics overlay
            var simpleRenderer = new SimpleRenderer(new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, ColorUtil.colorToArgb(Color.RED), 5));
            coloradoGraphicsOverlay.setRenderer(simpleRenderer);

            // create a control panel for the on/off toggles
            var controlsVBox = new VBox(6);
            controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.4)"), CornerRadii.EMPTY,
                    Insets.EMPTY)));
            controlsVBox.setPadding(new Insets(10.0));
            controlsVBox.setMaxSize(120, 75);

            // create a toggle group for the enabled/disable buttons
            var toggleGroup = new ToggleGroup();

            // create the enable/disable buttons and add them to the toggle group
            var toggleOn = new ToggleButton("Max Extent ON ");
            var toggleOff = new ToggleButton("Max Extent OFF");
            toggleOn.setOnMouseClicked(e -> map.setMaxExtent(extentEnvelope));
            toggleOff.setOnMouseClicked(e -> map.setMaxExtent(null));
            toggleOn.setToggleGroup(toggleGroup);
            toggleOff.setToggleGroup(toggleGroup);
            toggleOn.setSelected(true);

            // add the toggle buttons to the control panel
            controlsVBox.getChildren().addAll(toggleOn, toggleOff);

            // add the map view and the control panel to the stack pane
            stackPane.getChildren().addAll(mapView, controlsVBox);
            StackPane.setAlignment(controlsVBox, Pos.BOTTOM_LEFT);
            StackPane.setMargin(controlsVBox, new Insets(0, 0, 20, 10));
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
