package com.esri.samples.map.generate_offline_map_with_local_basemap;

import com.esri.arcgisruntime.concurrent.Job;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LayerViewStateChangedListener;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.security.AuthenticationManager;
import com.esri.arcgisruntime.security.DefaultAuthenticationChallengeHandler;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.tasks.offlinemap.GenerateOfflineMapJob;
import com.esri.arcgisruntime.tasks.offlinemap.GenerateOfflineMapParameters;
import com.esri.arcgisruntime.tasks.offlinemap.GenerateOfflineMapResult;
import com.esri.arcgisruntime.tasks.offlinemap.OfflineMapTask;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class GenerateOfflineMapWithLocalBasemap extends Application {

  private MapView mapView;
  private String downloadedBasemapSavedLocation;
  private String localBasemapDirectory;
  private File localBasemapFile;
  private GraphicsOverlay graphicsOverlay;
  private Graphic downloadArea;
  private ArcGISMap map;

  private ProgressBar progressBar;
  private Button offlineMapButton;

  private GenerateOfflineMapParameters generateOfflineMapParameters;
  private GenerateOfflineMapJob generateOfflineMapJob;
  private OfflineMapTask task;


  @Override
  public void start(Stage stage) throws Exception {
    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Generate Offline Map With Local Basemap Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map view
      mapView = new MapView();

      // create a button to take the map offline
      offlineMapButton = new Button("Take Map Offline");
      offlineMapButton.setDisable(true);

      // create a portal item with the itemId of the web map
      Portal portal = new Portal("https://www.arcgis.com");
      PortalItem portalItem = new PortalItem(portal, "acc027394bc84c2fb04d1ed317aac674");

      // create a map with the portal item
      map = new ArcGISMap(portalItem);
      map.addDoneLoadingListener(() -> {
        // enable the button when the map is loaded
        if (map.getLoadStatus() == LoadStatus.LOADED) {
          offlineMapButton.setDisable(false);

          // create a graphics overlay for the map view
          graphicsOverlay = new GraphicsOverlay();
          mapView.getGraphicsOverlays().add(graphicsOverlay);

          // create a graphic to show a box around the extent we want to download
          downloadArea = new Graphic();
          graphicsOverlay.getGraphics().add(downloadArea);
          SimpleLineSymbol simpleLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF0000, 2);
          downloadArea.setSymbol(simpleLineSymbol);

          updateDownloadArea();
        }
      });

      // set the map to the map view
      mapView.setMap(map);
      // update the download area box whenever the viewpoint changes
      mapView.addViewpointChangedListener(viewpointChangedEvent -> updateDownloadArea());


      // create a progress bar to show download progress
      progressBar = new ProgressBar();
      progressBar.setProgress(0.0);
      progressBar.setVisible(false);
      progressBar.setMaxSize(200, 200);

      // when the take map offline button is clicked, start the offline map task job
      offlineMapButton.setOnAction(e -> {
        try {
          // show the progress bar
          progressBar.setVisible(true);

          // specify the extent, min scale, and max scale as parameters
          double minScale = mapView.getMapScale();
          double maxScale = map.getMaxScale();
          // minScale must always be larger than maxScale
          if (minScale <= maxScale) {
            minScale = maxScale + 1;
          }

          // create an offline map task with the map
          task = new OfflineMapTask(map);

          // create default generate offline map parameters
          ListenableFuture<GenerateOfflineMapParameters> generateOfflineMapParametersListenableFuture = task.createDefaultGenerateOfflineMapParametersAsync(downloadArea.getGeometry(), minScale, maxScale);

          // get the offline map parameters from the offline map task
          generateOfflineMapParameters = generateOfflineMapParametersListenableFuture.get();
          generateOfflineMapParametersListenableFuture.addDoneListener(() -> {
            // define the directory in which the downloaded .tpk file relevant to this offline map sits
            downloadedBasemapSavedLocation = new File("./samples-data/naperville/").getAbsolutePath();

            // get the name of the basemap file from the offline map as supplied by the map's author (in this instance naperville_imagery.tpk)
            String referenceBasemapFileName = generateOfflineMapParameters.getReferenceBasemapFilename();

            // check if the offline map parameters include reference to a basemap file

            // if the basemap file name isn't empty, search for that file name within local computer
            if (!referenceBasemapFileName.isEmpty()) {

              // search for the given file name within the samples-data directory
              String localBasemapFileString = FilenameUtils.concat(downloadedBasemapSavedLocation, referenceBasemapFileName);
              localBasemapFile = new File(localBasemapFileString);

              if (localBasemapFile.exists()) {
                // get the file's directory
                localBasemapDirectory = localBasemapFile.getParent();

                try {
                  // open a new dialog box to alert user to the existence of the .tpk saved already on their machine
                  dialogPopup();

                } catch (IOException e1) {
                  e1.printStackTrace();
                }

              } else {
                String message = "Local basemap file " + referenceBasemapFileName + " not found!";
                Alert alert = new Alert(Alert.AlertType.ERROR, message);
                alert.show();
              }
            } else {
              Alert alert = new Alert(Alert.AlertType.ERROR, "The map's author has not specified a local basemap");
              alert.show();
            }
          });

        } catch (InterruptedException e1) {
          e1.printStackTrace();
        } catch (ExecutionException e1) {
          e1.printStackTrace();
        }
      });

      // add the map view, button, and progress bar to stack pane
      stackPane.getChildren().addAll(mapView, offlineMapButton, progressBar);
      StackPane.setAlignment(offlineMapButton, Pos.TOP_LEFT);
      StackPane.setAlignment(progressBar, Pos.CENTER);

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }


  /**
   * Launches a new alert for the user to choose which basemap to load on the machine
   */
  private void dialogPopup() throws IOException {

    // create a new alert
    Alert baseMapAlert = new Alert(Alert.AlertType.CONFIRMATION);
    baseMapAlert.setTitle("Basemap Options");
    baseMapAlert.setHeaderText("Local basemap found on this machine");
    baseMapAlert.setContentText("The local basemap file " + generateOfflineMapParameters.getReferenceBasemapFilename() + " was found on the machine. Would you like to use the local file instead of an online basemap?" );

    // add two buttons to the alert
    ButtonType buttonTypeYes = new ButtonType("Yes");
    ButtonType buttonTypeNo = new ButtonType("No");
    baseMapAlert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

    Optional<ButtonType> result = baseMapAlert.showAndWait();

    if (result.get() == buttonTypeYes){
      // load the locally saved basemap to the machine
      loadDownloadedOfflineMap();
    } else {
      // handle authentication with the portal to access the basemap
      AuthenticationManager.setAuthenticationChallengeHandler(new DefaultAuthenticationChallengeHandler());
      generateOfflineMap();
    }
  }

  /**
   * Set the reference basemap directory to the locally saved basemap on the machine, and then generate the offline map.
   */
  private void loadDownloadedOfflineMap() throws IOException {
    generateOfflineMapParameters.setReferenceBasemapDirectory(localBasemapDirectory);
    generateOfflineMap();
  }

  /**
   * Called when the Generate offline map button is clicked. Builds parameters for the offline map task from the UI
   * inputs and executes the task.
   */
  private void generateOfflineMap() throws IOException {

    // create an offline map job with the download directory path and parameters and start the job
    Path tempDirectory = Files.createTempDirectory("offline_map");
    generateOfflineMapJob = task.generateOfflineMap(generateOfflineMapParameters, tempDirectory.toAbsolutePath().toString());
    generateOfflineMapJob.start();

    // replace the current map with the result offline map when the job finishes
    generateOfflineMapJob.addJobDoneListener(() -> {
      if (generateOfflineMapJob.getStatus() == Job.Status.SUCCEEDED) {
        GenerateOfflineMapResult result = generateOfflineMapJob.getResult();
        mapView.setMap(result.getOfflineMap());
        graphicsOverlay.getGraphics().clear();
        offlineMapButton.setDisable(true);
      } else {
        new Alert(Alert.AlertType.ERROR, generateOfflineMapJob.getError().getAdditionalMessage()).show();
      }
      Platform.runLater(() -> progressBar.setVisible(false));
    });

    // show the job's progress with the progress bar
    generateOfflineMapJob.addProgressChangedListener(() -> progressBar.setProgress(generateOfflineMapJob.getProgress() / 100.0));
  }

  /**
   * Updates the download area graphic to show a red border around the current view extent that will be downloaded if
   * taken offline.
   */
  private void updateDownloadArea() {
    if (map.getLoadStatus() == LoadStatus.LOADED) {
      // upper left corner of the area to take offline
      Point2D minScreenPoint = new Point2D(50, 50);
      // lower right corner of the downloaded area
      Point2D maxScreenPoint = new Point2D(mapView.getWidth() - 50, mapView.getHeight() - 50);
      // convert screen points to map points
      Point minPoint = mapView.screenToLocation(minScreenPoint);
      Point maxPoint = mapView.screenToLocation(maxScreenPoint);
      // use the points to define and return an envelope
      if (minPoint != null && maxPoint != null) {
        Envelope envelope = new Envelope(minPoint, maxPoint);
        downloadArea.setGeometry(envelope);
      }
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
