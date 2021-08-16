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

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import javafx.scene.layout.VBox;
import org.apache.commons.io.IOUtils;

import javax.swing.text.html.HTMLEditorKit;

public class SetUpLocationDrivenGeotriggersController {

  @FXML private MapView mapView;
  @FXML private ImageView gardenSectionImageView;
  @FXML private ImageView gardenPOIImageView;
  @FXML private Label currentGardenSectionTitle;
  @FXML private Label currentGardenSectionDescription;
  @FXML private Label pointsOfInterestDescription;
  @FXML private Label pointsOfInterestTitle;
  @FXML private Button startButton;
  @FXML private Button stopButton;
  @FXML private Label systemInfo;

  private LocationGeotriggerFeed locationGeotriggerFeed;
  private SimulatedLocationDataSource simulatedLocationDataSource;
  private List<String> currentSections;
  private GeotriggerMonitor gardenSectionGeotriggerMonitor;
  private GeotriggerMonitor gardenPOIGeotriggerMonitor;
  private FenceGeotrigger gardenSectionFenceGeotrigger;
  private FenceGeotrigger gardenPOIFenceGeotrigger;

  public void initialize() {

    try {

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a map with a predefined tile basemap, feature styles and labels in the Santa Barbara Botanic Garden and set it to the map view
      Portal portal = new Portal("https://www.arcgis.com/");
      ArcGISMap map = new ArcGISMap(new PortalItem(portal, "6ab0e91dc39e478cae4f408e1a36a308"));
      mapView.setMap(map);

      // create service feature tables to add geotrigger monitors for later
      ServiceFeatureTable gardenSectionFeatureTable = new ServiceFeatureTable(new PortalItem(portal,"1ba816341ea04243832136379b8951d9"), 0);
      ServiceFeatureTable gardenPOIFeatureTable = new ServiceFeatureTable(new PortalItem(portal,"7c6280c290c34ae8aeb6b5c4ec841167"), 0);

      initializeSimulatedLocationDisplay();

      simulatedLocationDataSource.addStartedListener(() -> {
        if (simulatedLocationDataSource.isStarted()) {
          locationGeotriggerFeed = new LocationGeotriggerFeed(simulatedLocationDataSource);
          // configure the map view's location display to follow the simulated location data source
          LocationDisplay locationDisplay = mapView.getLocationDisplay();
          locationDisplay.setLocationDataSource(simulatedLocationDataSource);
          locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
          locationDisplay.setInitialZoomScale(1000);
          // start the location display
          locationDisplay.startAsync();

          // create geotriggers for each of the service feature tables

          createFenceGeotrigger(gardenSectionFeatureTable, 0.0, "Section Geotrigger");
          createFenceGeotrigger(gardenPOIFeatureTable, 10.0, "POI Geotrigger");

          gardenSectionFenceGeotrigger = createFenceGeotrigger(gardenSectionFeatureTable, 0.0, "Section Geotrigger");
          gardenPOIFenceGeotrigger = createFenceGeotrigger(gardenPOIFeatureTable, 10.0, "POI Geotrigger");

          gardenSectionGeotriggerMonitor = new GeotriggerMonitor(gardenSectionFenceGeotrigger);
          gardenPOIGeotriggerMonitor = new GeotriggerMonitor(gardenPOIFenceGeotrigger);

          gardenPOIGeotriggerMonitor.startAsync();
          gardenSectionGeotriggerMonitor.startAsync();

          gardenSectionGeotriggerMonitor.addGeotriggerMonitorNotificationEventListener(e -> {
            handleGeotriggerNotification(e.getGeotriggerNotificationInfo());
          });

          gardenPOIGeotriggerMonitor.addGeotriggerMonitorNotificationEventListener(e -> {
            handleGeotriggerNotification(e.getGeotriggerNotificationInfo());
          });

        }
      });

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   *
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
      locations, new SimulationParameters(Calendar.getInstance(), 2.0, 0.0, 0.0 ));

    simulatedLocationDataSource.startAsync();

    // disable map view interaction, the location display will automatically center on the mock device location
    mapView.setEnableMousePan(false);
    mapView.setEnableKeyboardNavigation(false);

  }

  /**
   * Creates a geotrigger monitor for a given service feature table
   * @param serviceFeatureTable
   * @param bufferSize
   * @param geotriggerName
   */
  private FenceGeotrigger createFenceGeotrigger(ServiceFeatureTable serviceFeatureTable, double bufferSize, String geotriggerName) {

    FeatureFenceParameters featureFenceParameters = new FeatureFenceParameters(serviceFeatureTable, bufferSize);
    // define an arcade expression that returns the value for the "name" field of the feaeture that triggered the monitor
    ArcadeExpression arcadeExpression = new ArcadeExpression("$fenceFeature.name");
    FenceGeotrigger fenceGeotrigger = new FenceGeotrigger(
      locationGeotriggerFeed, FenceRuleType.ENTER_OR_EXIT, featureFenceParameters, arcadeExpression, geotriggerName);

    return fenceGeotrigger;
  }

  private void handleGeotriggerNotification(GeotriggerNotificationInfo geotriggerNotificationInfo) {

      // fence geotrigger notification info provides access to the feature that triggered the notification
      var fenceGeotriggerNotificationInfo = (FenceGeotriggerNotificationInfo) geotriggerNotificationInfo;

      // name of the fence feature
      String fenceFeatureName = fenceGeotriggerNotificationInfo.getMessage();
    System.out.println(fenceFeatureName);

      if (fenceGeotriggerNotificationInfo.getFenceNotificationType() == FenceNotificationType.ENTERED) {
        // if the user enters a given geofence, add the feature's information to the UI and save the feature for querying

        // get the fence Geoelement as an ArcGISFeature, and the description from the feature's attributes
        ArcGISFeature fenceFeature = (ArcGISFeature) fenceGeotriggerNotificationInfo.getFenceGeoElement();
        String description = fenceFeature.getAttributes().get("description").toString().replaceAll("<.*?>", " ");


        String geoTriggerName = geotriggerNotificationInfo.getGeotriggerMonitor().getGeotrigger().getName();

        // fetch the fence feature's attachments
        ListenableFuture<List<Attachment>> attachmentsFuture = fenceFeature.fetchAttachmentsAsync();
        // listen for fetch attachments to complete
        attachmentsFuture.addDoneListener(() -> {
          // get the feature attachments
          try {
            List<Attachment> attachments = attachmentsFuture.get();
            System.out.println("first try");
            if (!attachments.isEmpty()) {
              System.out.println("attachments aren't empty");
              // get the first (and only) attachment for the feature, which is an image
              Attachment imageAttachment = attachments.get(0);
              // fetch the attachment's data
              ListenableFuture<InputStream> attachmentDataFuture = imageAttachment.fetchDataAsync();
              // listen for fetch data to complete
              attachmentDataFuture.addDoneListener(() -> {
                // get the attachments data as an input stream
                System.out.println("attachment future done");
                try {
                  InputStream attachmentInputStream = attachmentDataFuture.get();
                  // save the input stream to a temporary directory and get a reference to its URI
                  Image imageFromStream = new Image(attachmentInputStream);

                  // if the geotrigger notification belongs to the garden section, populate the garden section part of the UI
                  if (geoTriggerName.equals(gardenSectionFenceGeotrigger.getName())) {
                    currentGardenSectionTitle.setText(fenceFeatureName);
                    currentGardenSectionDescription.setText(description);
                    gardenSectionImageView.setImage(imageFromStream);

                  } else {
                    pointsOfInterestTitle.setText(fenceFeatureName);
                    pointsOfInterestDescription.setText(description);
                    gardenPOIImageView.setImage(imageFromStream);
                  }
                  attachmentInputStream.close();


                } catch (Exception e) {
                  e.printStackTrace();
                }

              });
            }
          } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
          }
        });

      } else if (fenceGeotriggerNotificationInfo.getFenceNotificationType() == FenceNotificationType.EXITED) {
        // otherwise remove the feature's information
        pointsOfInterestDescription.setText("");
        pointsOfInterestTitle.setText("");
        gardenPOIImageView.setImage(null);
//        currentSections.remove(fenceFeatureName);
      }
  }

  /**
   * Disposes application resources.
   */
  void terminate() {
    if (mapView != null) {
      simulatedLocationDataSource.stop();
      mapView.dispose();
    }
  }

}
