package com.esri.samples.map.generate_offline_map_with_local_basemap;

import com.esri.arcgisruntime.concurrent.Job;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
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
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
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
  private String downloadedBasemapNaperville;
  private String localBasemapDirectory;
  private File localBasemapFile;

  private GenerateOfflineMapParameters generateOfflineMapParameters;
  private GenerateOfflineMapJob generateOfflineMapJob;
//  private OfflineMapTask task;


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

      // create a button to take the map offline
      Button offlineMapButton = new Button("Take Map Offline");
      offlineMapButton.setDisable(true);

      // handle authentication with the portal
      AuthenticationManager.setAuthenticationChallengeHandler(new DefaultAuthenticationChallengeHandler());

      // create a portal item with the itemId of the web map
      Portal portal = new Portal("https://www.arcgis.com", true);
      PortalItem portalItem = new PortalItem(portal, "acc027394bc84c2fb04d1ed317aac674");

      // create a map with the portal item
      ArcGISMap map = new ArcGISMap(portalItem);
      map.addDoneLoadingListener(() -> {
        // enable the button when the map is loaded
        if (map.getLoadStatus() == LoadStatus.LOADED) {
          offlineMapButton.setDisable(false);
        }
      });

      // set the map to the map view
      mapView = new MapView();
      mapView.setMap(map);

      // create a graphics overlay for the map view
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create a graphic to show a box around the extent we want to download
      Graphic downloadArea = new Graphic();
      graphicsOverlay.getGraphics().add(downloadArea);
      SimpleLineSymbol simpleLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF0000, 2);
      downloadArea.setSymbol(simpleLineSymbol);

      // update the download area box whenever the viewpoint changes
      mapView.addViewpointChangedListener(viewpointChangedEvent -> {
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
      });

      // create progress bar to show download progress
      ProgressBar progressBar = new ProgressBar();
      progressBar.setProgress(0.0);
      progressBar.setVisible(false);


      // when the button is clicked, start the offline map task job
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
          OfflineMapTask task = new OfflineMapTask(map);

          // ************************************************************************************************************************************************
          // create default generate offline map parameters
          ListenableFuture<GenerateOfflineMapParameters> generateOfflineMapParametersListenableFuture = task.createDefaultGenerateOfflineMapParametersAsync(downloadArea.getGeometry(), minScale, maxScale);

          // get the offline map parameters from the offline map task
          generateOfflineMapParameters = generateOfflineMapParametersListenableFuture.get();
          System.out.println("1. " + generateOfflineMapParameters);


          generateOfflineMapParametersListenableFuture.addDoneListener(() -> {




            // define the directory in which the downloaded .tpk file relevant to this offline map sits
            downloadedBasemapNaperville = new File("./samples-data/naperville/").getAbsolutePath();

            // get the name of the basemap file from the offline map as supplied by the map's author (in this instance naperville_imagery.tpk)
            String localBasemapFileName = generateOfflineMapParameters.getReferenceBasemapFilename();

            // check if the offline map parameters include reference to a basemap file

            if (!localBasemapFileName.isEmpty()) {
              // search for the given file name within the samples-data directory

              String localBasemapFileString = FilenameUtils.concat(downloadedBasemapNaperville, localBasemapFileName);
              localBasemapFile = new File(localBasemapFileString);

              if (localBasemapFile != null) {

                // get the file's directory
                localBasemapDirectory = localBasemapFile.getParent();

                dialogPopup();


              } else {
                String message = "Local basemap file " + localBasemapFileName + " not found!";
                Alert alert = new Alert(Alert.AlertType.ERROR, message);
                alert.show();
              }

            } else {
              Alert alert = new Alert(Alert.AlertType.ERROR, "The map's author has not specified a local basemap");
              alert.show();
            }

          });

          // ***********************************************************************************************************************************

          // for when the dialog box comes up and the option yes is selected for using the already downloaded basemap

          // create an offline map job with the download directory path and parameters and start the job

//          GenerateOfflineMapParameters params = new GenerateOfflineMapParameters(downloadArea.getGeometry(), minScale, maxScale);


          Path tempDirectory = Files.createTempDirectory("offline_map");
          System.out.println(generateOfflineMapParameters);
          generateOfflineMapJob = task.generateOfflineMap(generateOfflineMapParameters, tempDirectory.toAbsolutePath().toString());

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

          // start the job
          generateOfflineMapJob.start();

          // show the job's progress with the progress bar
//          generateOfflineMapJob.addProgressChangedListener(() -> progressBar.setProgress(generateOfflineMapJob.getProgress() / 100.0));

        } catch (IOException ex) {
          new Alert(Alert.AlertType.ERROR, "Failed to create temporary directory").show();
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        } catch (ExecutionException e1) {
          e1.printStackTrace();
        }
      });


          // ************************************************************************************************************************************








//          GenerateOfflineMapParameters params = new GenerateOfflineMapParameters(downloadArea.getGeometry(), minScale, maxScale);
//
//          // create an offline map job with the download directory path and parameters and start the job
//          Path tempDirectory = Files.createTempDirectory("offline_map");
//          GenerateOfflineMapJob job = task.generateOfflineMap(params, tempDirectory.toAbsolutePath().toString());
//          job.start();
//          job.addJobDoneListener(() -> {
//            if (job.getStatus() == Job.Status.SUCCEEDED) {
//              // replace the current map with the result offline map when the job finishes
//              GenerateOfflineMapResult result = job.getResult();
//              mapView.setMap(result.getOfflineMap());
//              graphicsOverlay.getGraphics().clear();
//              offlineMapButton.setDisable(true);
//            } else {
//              new Alert(Alert.AlertType.ERROR, job.getError().getAdditionalMessage()).show();
//            }
//            Platform.runLater(() -> progressBar.setVisible(false));
//          });
////           show the job's progress with the progress bar
//          job.addProgressChangedListener(() -> progressBar.setProgress(job.getProgress() / 100.0));


      // add the map view, button, and progress bar to stack pane
      stackPane.getChildren().addAll(mapView, offlineMapButton, progressBar);
      StackPane.setAlignment(offlineMapButton, Pos.TOP_LEFT);
      StackPane.setAlignment(progressBar, Pos.TOP_RIGHT);

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }

  }


  private void dialogPopup() {

    Alert baseMapAlert = new Alert(Alert.AlertType.CONFIRMATION);
    baseMapAlert.setTitle("Basemap Options");
    baseMapAlert.setHeaderText("Local basemap found on this device");
    baseMapAlert.setContentText("The local basemap file " + generateOfflineMapParameters.getReferenceBasemapFilename() + " was found on the device. Would you like to use the local file instead of an online basemap?" );

    ButtonType buttonTypeYes = new ButtonType("Yes");
    ButtonType buttonTypeNo = new ButtonType("No");

    baseMapAlert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

    Optional<ButtonType> result = baseMapAlert.showAndWait();
    if (result.get() == buttonTypeYes){onYesClick();}
    else {return;}

  }


  private void onYesClick(){

    generateOfflineMapParameters.setReferenceBasemapDirectory(localBasemapDirectory);

  }

  private void generateOfflineMap() throws IOException {


    Path tempDirectory = Files.createTempDirectory("offline_map");
    System.out.println(generateOfflineMapParameters);
    generateOfflineMapJob = task.generateOfflineMap(generateOfflineMapParameters, tempDirectory.toAbsolutePath().toString());

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

    // start the job
    generateOfflineMapJob.start();




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
