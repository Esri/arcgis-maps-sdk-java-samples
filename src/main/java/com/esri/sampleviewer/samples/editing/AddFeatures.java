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

package com.esri.sampleviewer.samples.editing;

import java.util.HashMap;
import java.util.List;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.datasource.Feature;
import com.esri.arcgisruntime.datasource.arcgis.FeatureEditResult;
import com.esri.arcgisruntime.datasource.arcgis.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.Viewpoint;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This sample demonstrates how to add a new <@Feature> to a
 * <@ServiceFeatureTable> by clicking on a <@MapView>. How it works: a
 * ServiceFeatureTable is created from a URL holding the <@FeatureLayer> where
 * it is then added to the <@Map> and displayed on a <@MapView>. Once the user
 * clicks the MapView, a new Feature will be added to the ServiceFeatureTable
 * and displayed on the MapView. Lastly, this new Feature will be saved to the
 * server and be able to persist beyond this session.
 */
public class AddFeatures extends Application {
	private MapView mapView;

	private static final String SERVICE_LAYER_URL = "http://sampleserver6.arcgisonline.com/arcgis/rest/services/DamageAssessment/FeatureServer/0";
	private static final String SAMPLES_THEME_PATH = "../resources/SamplesTheme.css";

	@Override
	public void start(Stage stage) throws Exception {
		// create stack pane and application scene
		StackPane stackPane = new StackPane();
		Scene scene = new Scene(stackPane);
		scene.getStylesheets().add(getClass().getResource(SAMPLES_THEME_PATH).toExternalForm());

		// set title, size, and add scene to stage
		stage.setTitle("Add Features Sample");
		stage.setWidth(800);
		stage.setHeight(700);
		stage.setScene(scene);
		stage.show();

		// create a control panel
		VBox vBoxControl = new VBox(6);
		vBoxControl.setMaxSize(240, 120);
		vBoxControl.getStyleClass().add("panel-region");

		// create sample label and description
		Label descriptionLabel = new Label("Sample Description");
		descriptionLabel.getStyleClass().add("panel-label");

		TextArea description = new TextArea("This sample shows how to add a new\n"
				+ "features to a ServiceFeatureTable.\n" + "Click on the map and a new Feature\n" + "will be added.");
		description.setEditable(false);
		description.setMinSize(210, 80);

		// add sample label and description to the control panel
		vBoxControl.getChildren().addAll(descriptionLabel, description);

		try {
			// create spatial reference for point
			SpatialReference spatialReference = SpatialReferences.getWebMercator();

			// create a initial viewpoint with a point and scale
			Point pointLondon = new Point(-16773, 6710477, spatialReference);
			Viewpoint viewpoint = new Viewpoint(pointLondon, 200000);

			// create a map with streets basemap
			Map map = new Map(Basemap.createStreets());

			// set viewpoint to the map
			map.setInitialViewpoint(viewpoint);

			// create a view for this map
			mapView = new MapView();

			// create service feature table from URL
			ServiceFeatureTable featureTable = new ServiceFeatureTable(SERVICE_LAYER_URL);
			featureTable.getOutFields().add("*"); // * gets all fields from the
													// table

			// create a feature layer from table
			FeatureLayer featureLayer = new FeatureLayer(featureTable);

			// add the layer to the map
			map.getOperationalLayers().add(featureLayer);

			mapView.setOnMouseClicked(e -> {
				// check that the primary mouse button was clicked
				if (e.getButton() == MouseButton.PRIMARY) {
					// create a point from where the user clicked
					Point2D point = new Point2D(e.getX(), e.getY());

					// create a map point from a point
					Point mapPoint = mapView.screenToLocation(point);

					// add a new feature to the service feature table
					addNewFeature(mapPoint, featureTable);
				}
			});

			// set map to be displayed in map view
			mapView.setMap(map);

			// add the map view and control box to stack pane
			stackPane.getChildren().addAll(mapView, vBoxControl);
			StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
			StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));

		} catch (Exception e) {
			// on any error, display the stack trace
			e.printStackTrace();
		}
	}

	/**
	 * Adds a new Feature to the ServiceFeatureTable and applies the changes to
	 * the server.
	 * 
	 * @param mapPoint x,y-coordinate pair
	 * @param damageTable holds all Feature data
	 */
	private void addNewFeature(Point mapPoint, ServiceFeatureTable damageTable) {
		// create default attributes for the feature
		java.util.Map<String, Object> attributes = new HashMap<>();
		attributes.put("typdamage", "Minor");
		attributes.put("primcause", "Earthquake");

		// creates a new feature using a default attributes and point
		Feature feature = damageTable.createFeature(attributes, mapPoint);

		// adds the new feature to the service
		final ListenableFuture<Boolean> result = damageTable.addFeatureAsync(feature);

		try {
			// apply the changes to the server if successful
			if (result.get().booleanValue()) {
				final ListenableFuture<List<FeatureEditResult>> serverResult = damageTable.applyEditsAsync();
				// check if the server result was successful
				if (!serverResult.get().get(0).hasCompletedWithErrors()) {
					System.out.println("Feature successfully added");
				} else {
					System.out.println("Server Error: Feature failed to be added to Server.");
				}
			} else {
				System.out.println("Local Error: Feature failed to be added to ServiceFeatureTable locally.");
			}

		} catch (Exception e) {
			// on any error, display the stack trace
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
