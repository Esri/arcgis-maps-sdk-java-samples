/*
 * Copyright 2021 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.esri.samples.set_up_location_driven_geotriggers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;

import com.esri.arcgisruntime.arcade.ArcadeExpression;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Attachment;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geotriggers.*;
import com.esri.arcgisruntime.location.*;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import org.apache.commons.io.IOUtils;

public class SetUpLocationDrivenGeotriggersController {

  @FXML
  private MapView mapView;
  @FXML
  private ImageView gardenSectionImageView;
  @FXML
  private Label currentGardenSectionTitle;
  @FXML
  private Label currentGardenSectionDescription;
  @FXML
  private Label pointsOfInterestDescription;
  @FXML
  private Label pointsOfInterestTitle;

  private LocationGeotriggerFeed locationGeotriggerFeed;
  private SimulatedLocationDataSource simulatedLocationDataSource;
  private GeotriggerMonitor gardenSectionGeotriggerMonitor;
  private GeotriggerMonitor gardenPOIGeotriggerMonitor;
  private String fenceFeatureName;
  //  private FenceGeotrigger gardenSectionFenceGeotrigger;
//  private FenceGeotrigger gardenPOIFenceGeotrigger;
  private HashSet<String> names;

  public void initialize() throws IOException {

    // create a map with a predefined tile basemap, feature styles and labels in the Santa Barbara Botanic Garden and set it to the map view
    Portal portal = new Portal("https://www.arcgis.com/");
    ArcGISMap map = new ArcGISMap(new PortalItem(portal, "6ab0e91dc39e478cae4f408e1a36a308"));
    mapView.setMap(map);

    // instantiate a new hashset to store unique names of nearby features visited
    names = new HashSet<>();

    // create service feature tables to add geotrigger monitors for later
    ServiceFeatureTable gardenSectionFeatureTable = new ServiceFeatureTable(new PortalItem(portal, "1ba816341ea04243832136379b8951d9"), 0);
    ServiceFeatureTable gardenPOIFeatureTable = new ServiceFeatureTable(new PortalItem(portal, "7c6280c290c34ae8aeb6b5c4ec841167"), 0);
    // initialize the simulated location display
    initializeSimulatedLocationDisplay();

    simulatedLocationDataSource.addStartedListener(() -> {
      if (simulatedLocationDataSource.isStarted()) {

        // create, configure and start a location display that follows the simulated location data source
        setUpAndStartLocationDisplay();

        // create fence parameters for the garden section
        FeatureFenceParameters featureFenceParametersGardenSection = new FeatureFenceParameters(gardenSectionFeatureTable);
        // create fence parameters for the garden points of interest section with a buffer distance of 10m
        FeatureFenceParameters featureFenceParametersPOI = new FeatureFenceParameters(gardenPOIFeatureTable, 10);

        // define an arcade expression that returns the value for the "name" field of the feaeture that triggered the monitor
        ArcadeExpression arcadeExpression = new ArcadeExpression("$fenceFeature.name");
        // create a location geotrigger feed from the simulated location data source
        LocationGeotriggerFeed locationGeotriggerFeed = new LocationGeotriggerFeed(simulatedLocationDataSource);

        // create fence geotriggers for each of the service feature tables
        FenceGeotrigger fenceGeotriggerGardenSection = new FenceGeotrigger(locationGeotriggerFeed, FenceRuleType.ENTER_OR_EXIT,
          featureFenceParametersGardenSection, arcadeExpression, "Section Geotrigger");
        FenceGeotrigger fenceGeotriggerPOI = new FenceGeotrigger(locationGeotriggerFeed, FenceRuleType.ENTER_OR_EXIT,
          featureFenceParametersPOI, arcadeExpression, "POI Geotrigger");

        // create geotrigger monitors from the fence geotriggers and start them
        gardenSectionGeotriggerMonitor = new GeotriggerMonitor(fenceGeotriggerGardenSection);
        gardenPOIGeotriggerMonitor = new GeotriggerMonitor(fenceGeotriggerPOI);

        List<GeotriggerMonitor> geotriggerMonitors = new ArrayList<>(Arrays.asList(
          gardenSectionGeotriggerMonitor, gardenPOIGeotriggerMonitor));
        // for each geotrigger monitor, start it and add a notification listener
        geotriggerMonitors.forEach(monitor -> {
          // start each geotrigger monitor and add a notification event listener
          monitor.startAsync();
//          monitor.addGeotriggerMonitorStatusChangedEventListener(statusChangedEvent -> {


//            if (statusChangedEvent.getStatus() == GeotriggerMonitorStatus.STARTED) {

          monitor.addGeotriggerMonitorNotificationEventListener(notificationEvent -> {
            // fence geotrigger notification info provides access to the feature that triggered the notification
            var fenceGeotriggerNotificationInfo = (FenceGeotriggerNotificationInfo) notificationEvent.getGeotriggerNotificationInfo();

            // name of the fence feature
            fenceFeatureName = fenceGeotriggerNotificationInfo.getMessage();
            FenceNotificationType fenceNotificationType = fenceGeotriggerNotificationInfo.getFenceNotificationType();

            // when entering a given geofence, add the feature's information to the UI and save the feature for querying
            if (fenceNotificationType == FenceNotificationType.ENTERED) {
              // get the fence Geoelement as an ArcGISFeature, and the description from the feature's attributes
              ArcGISFeature fenceFeature = (ArcGISFeature) fenceGeotriggerNotificationInfo.getFenceGeoElement();
              handleAddingFeatureInfoToUI(fenceFeature, fenceGeotriggerNotificationInfo);

              // when exiting a given geofence, remove its information from the UI
            } else if (fenceNotificationType == FenceNotificationType.EXITED) {

              handleRemovingFeatureInfoFromUI();
            }
          });
        });
      }
    });

  }

  /**
   * Creates and starts a simulated location data source based on a json file containing a set polyline data path.
   */
  private void initializeSimulatedLocationDisplay() throws IOException {

    // access the json of the walking route points
    String polylineData = IOUtils.toString(getClass().getResourceAsStream(
      "/set_up_location_driven_geotriggers/polyline_data.json"), StandardCharsets.UTF_8);
    // create a polyline from the location points
    Polyline locations = (Polyline) Geometry.fromJson(polylineData, SpatialReferences.getWgs84());

    // create a new simulated location data source to replicate the path walked around the garden
    simulatedLocationDataSource = new SimulatedLocationDataSource();
    // set the location of the simulated location data source with simulation parameters to set a consistent velocity
    simulatedLocationDataSource.setLocations(
      locations, new SimulationParameters(Calendar.getInstance(), 2.0, 0.0, 0.0));

    simulatedLocationDataSource.startAsync();

    // disable map view interaction, the location display will automatically center on the mock device location
    mapView.setEnableMousePan(false);
    mapView.setEnableKeyboardNavigation(false);

  }

  /**
   * Gets information from the fence geotrigger notification information and the fence feature itself and adds the information
   * to the UI either as a formatted string or as an image.
   *
   * @param fenceFeature                    the feature
   * @param fenceGeotriggerNotificationInfo the fence geotrigger notification info
   */
  private void handleAddingFeatureInfoToUI(ArcGISFeature fenceFeature, FenceGeotriggerNotificationInfo fenceGeotriggerNotificationInfo) {

    String geoTriggerName = fenceGeotriggerNotificationInfo.getGeotriggerMonitor().getGeotrigger().getName();

    // fetch the fence feature's attachments
    ListenableFuture<List<Attachment>> attachmentsFuture = fenceFeature.fetchAttachmentsAsync();
    // listen for fetch attachments to complete
    attachmentsFuture.addDoneListener(() -> {
      // get the feature attachments
      try {
        List<Attachment> attachments = attachmentsFuture.get();
        if (!attachments.isEmpty()) {
          // get the first (and only) attachment for the feature, which is an image
          Attachment imageAttachment = attachments.get(0);
          // fetch the attachment's data
          ListenableFuture<InputStream> attachmentDataFuture = imageAttachment.fetchDataAsync();
          // listen for fetch data to complete
          attachmentDataFuture.addDoneListener(() -> {
            // get the attachments data as an input stream
            try {
              InputStream attachmentInputStream = attachmentDataFuture.get();
              // save the input stream to a temporary directory and get a reference to its URI
              Image imageFromStream = new Image(attachmentInputStream);
              attachmentInputStream.close();

              // if the geotrigger notification belongs to the garden section, populate the garden section part of the UI
              if (geoTriggerName.equals(gardenSectionGeotriggerMonitor.getGeotrigger().getName())) {
                // add details to the UI
                currentGardenSectionTitle.setText("Current section: " + fenceFeatureName);
                // get the first sentence of the description to display in the UI
                String firstSentenceOfDescription = fenceFeature.getAttributes().get("description").toString().split("\\.")[0];
                currentGardenSectionDescription.setText(firstSentenceOfDescription + ".");
                gardenSectionImageView.setImage(imageFromStream);

              } else if (geoTriggerName.equals(gardenPOIGeotriggerMonitor.getGeotrigger().getName())) {
                // add details to the UI
                names.add(fenceFeatureName);
                String str = String.join(", ", names);
                pointsOfInterestTitle.setText(str);
              }

            } catch (Exception exception) {
              exception.printStackTrace();
            }
          });
        }
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    });

  }

  /**
   * Removes the name of the fence feature being exited from the collection of feature names, and updates the label on the UI.
   */
  private void handleRemovingFeatureInfoFromUI() {
    names.remove(fenceFeatureName);
    String str = String.join(", ", names);
    pointsOfInterestTitle.setText(str);
    if (names.isEmpty()) {
      pointsOfInterestTitle.setText("No features nearby");
    }
  }

  /**
   * Creates and sets up a location display from the simulated location data source, and starts it.
   */
  private void setUpAndStartLocationDisplay() {
    // configure the map view's location display to follow the simulated location data source
    LocationDisplay locationDisplay = mapView.getLocationDisplay();
    locationDisplay.setLocationDataSource(simulatedLocationDataSource);
    locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
    locationDisplay.setInitialZoomScale(1000);
    // start the location display
    locationDisplay.startAsync();
  }

  /**
   * Disposes application resources.
   */
  void terminate() {
    if (mapView != null) {
      simulatedLocationDataSource.stop();
      gardenPOIGeotriggerMonitor.stop();
      gardenSectionGeotriggerMonitor.stop();
      mapView.dispose();
    }
  }

}
