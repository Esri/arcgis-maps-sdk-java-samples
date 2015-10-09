/*
 * Copyright 2015 Esri.
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

package com.esri.sampleviewer.samples.featurelayers;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.datasource.Feature;
import com.esri.arcgisruntime.datasource.FeatureQueryResult;
import com.esri.arcgisruntime.datasource.QueryParameters;
import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.Viewpoint;
import com.esri.arcgisruntime.symbology.RgbColor;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This sample demonstrates how to select features in a feature layer. How it
 * works: the map view provides a way for add a listener for mouse events. A
 * clicked point is buffered into an envelope to select some features. A query
 * parameter object sets with the envelope is created. It then calls the select
 * features method which takes the query parameters object and selects the
 * features. It also returns the result from which you can inspect the selected
 * features.
 */
public class FeatureLayerSelection extends Application {

	private MapView mapView;

	private final String DAMAGE_ASSESSMENT_FEATURE_SERVICE = "http://sampleserver6.arcgisonline.com/arcgis/rest/services/DamageAssessment/FeatureServer/0";
	private static final String SAMPLES_THEME_PATH = "../resources/SamplesTheme.css";

	@Override
	public void start(Stage stage) throws Exception {
		// create stack pane and application scene
		StackPane stackPane = new StackPane();
		Scene scene = new Scene(stackPane);
		scene.getStylesheets().add(getClass().getResource(SAMPLES_THEME_PATH).toExternalForm());

		// size the stage, add a title, and set scene to stage
		stage.setTitle("Feature Layer Selection");
		stage.setHeight(700);
		stage.setWidth(800);
		stage.setScene(scene);
		stage.show();

		// create a control panel
		VBox vBoxControl = new VBox(6);
		vBoxControl.setMaxSize(240, 100);
		vBoxControl.getStyleClass().add("panel-region");

		// create sample label and description
		Label descriptionLabel = new Label("Sample Description");
		descriptionLabel.getStyleClass().add("panel-label");

		TextArea description = new TextArea("This sample demonstrates how to \n"
				+ "select features in a feature layer. Click\n" + "the features to select them.");
		description.setEditable(false);
		description.setMinSize(210, 60);

		// add sample label and description to the control panel
		vBoxControl.getChildren().addAll(descriptionLabel, description);

		try {
			// create a view for this map
			mapView = new MapView();

			// create a map with the streets basemap
			Map map = new Map(Basemap.createStreets());

			// set an initial viewpoint
			map.setInitialViewpoint(new Viewpoint(new Envelope(-1131596.019761, 3893114.069099, 3926705.982140,
					7977912.461790, 0, 0, 0, 0, SpatialReferences.getWebMercator())));

			// set the map to be displayed in the view
			mapView.setMap(map);

			// create feature layer with its service feature table
			// create the service feature table
			final ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(DAMAGE_ASSESSMENT_FEATURE_SERVICE);

			// create the feature layer using the service feature table
			final FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
			featureLayer.setSelectionColor(new RgbColor(0, 255, 255, 255)); // cyan,
			// fully
			// opaque
			featureLayer.setSelectionWidth(3);

			// add the layer to the map
			map.getOperationalLayers().add(featureLayer);

			mapView.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {

				// get the point that was clicked and convert it to a point in
				// map coordinates
				Point clickPoint = mapView.screenToLocation(new Point2D(e.getX(), e.getY()));
				int tolerance = 10;
				double mapTolerance = tolerance * mapView.getUnitsPerPixel();

				// create objects required to do a selection with a query
				Envelope envelope = new Envelope(clickPoint.getX() - mapTolerance, clickPoint.getY() - mapTolerance,
						clickPoint.getX() + mapTolerance, clickPoint.getY() + mapTolerance, 0, 0, 0, 0,
						map.getSpatialReference());
				QueryParameters query = new QueryParameters();
				query.setGeometry(envelope);
				query.getOutFields().add("*");

				// call select features
				final ListenableFuture<FeatureQueryResult> queryFeatures = featureLayer.selectFeatures(query,
						FeatureLayer.SelectionMode.NEW);
				// add done loading listener to fire when the selection returns
				queryFeatures.addDoneListener(new Runnable() {
					@Override
					public void run() {
						try {
							// call get on the future to get the result
							FeatureQueryResult result = queryFeatures.get();

							// find out how many items there are in the result
							int nFeatures = 0;
							for (Feature feature : result) {
								nFeatures++;
							}
							System.out.println("Features selected: " + nFeatures);

						} catch (Exception e) {
							// on any error, notify.
							e.printStackTrace();
						}
					}
				});
			});

			// add the map view and control box to stack pane
			stackPane.getChildren().addAll(mapView, vBoxControl);
			StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
			StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));

		} catch (Exception e) {
			// on any error, display exception
			e.printStackTrace();
		}
	}

	/**
	 * Stops and releases all resources used in application.
	 * 
	 * @throws Exception if security manager doesn't allow JVM to exit with
	 * current status
	 */
	@Override
	public void stop() throws Exception {
		// release resources when the application closes
		if (mapView != null) {
			mapView.dispose();
		}
		Platform.exit();
		System.exit(0);
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
