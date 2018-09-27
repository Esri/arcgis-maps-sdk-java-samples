package com.esri.samples.map.generate_offline_map_overrides;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.security.AuthenticationManager;
import com.esri.arcgisruntime.security.DefaultAuthenticationChallengeHandler;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;

public class GenerateOfflineMapOverridesController {

  @FXML private MapView mapView;
  @FXML private Spinner minScaleLevelSpinner;
  @FXML private Spinner maxScaleLevelSpinner;
  @FXML private Spinner extentBufferDistanceSpinner;
  @FXML private Spinner minHydrantFlowRateSpinner;
  @FXML private Button generateOfflineMapButton;

  @FXML
  private void initialize() {
    // handle authentication with the portal
    AuthenticationManager.setAuthenticationChallengeHandler(new DefaultAuthenticationChallengeHandler());

    // create a portal item with the itemId of the web map
    Portal portal = new Portal("https://www.arcgis.com", true);
    PortalItem portalItem = new PortalItem(portal, "acc027394bc84c2fb04d1ed317aac674");

    // create a map with the portal item
    ArcGISMap map = new ArcGISMap(portalItem);
    map.addDoneLoadingListener(() -> {
      // enable the generate offline map button when the map is loaded
      if (map.getLoadStatus() == LoadStatus.LOADED) {
        generateOfflineMapButton.setDisable(false);
      }
    });

    // set the map to the map view
    mapView.setMap(map);

    // create a graphics overlay for the map view
    GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
    mapView.getGraphicsOverlays().add(graphicsOverlay);

    // create a graphic to show a box around the extent we want to download
    Graphic downloadArea = new Graphic();
    graphicsOverlay.getGraphics().add(downloadArea);
    SimpleLineSymbol simpleLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF0000, 2);
    downloadArea.setSymbol(simpleLineSymbol);

    // update the box whenever the viewpoint changes
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
  }

  /**
   * Stops the animation and disposes of application resources.
   */
  void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }
}
