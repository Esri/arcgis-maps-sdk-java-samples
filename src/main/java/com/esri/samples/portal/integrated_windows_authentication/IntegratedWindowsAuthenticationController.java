package com.esri.samples.portal.integrated_windows_authentication;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.portal.PortalQueryParameters;
import com.esri.arcgisruntime.portal.PortalQueryResultSet;
import com.esri.arcgisruntime.security.AuthenticationManager;

public class IntegratedWindowsAuthenticationController {

  @FXML
  private MapView mapView;
  @FXML
  private ListView<PortalItem> resultsListView;
  @FXML
  private TextField portalUrlTextField;
  @FXML
  private Label loadStateTextView;

  public void initialize()  {
    try {

      // create a streets base map
      ArcGISMap map = new ArcGISMap(Basemap.createStreets());

      // set the map to be displayed in the map view
      mapView.setMap(map);

      // set authentication challenge handler
      AuthenticationManager.setAuthenticationChallengeHandler(new IWAChallengeHandler());

      // add a listener to the map results list view that loads the map on selection
      resultsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> displayMap());

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  private void searchPortal(Portal portal){

    // check if the portal is null
    if (portal == null) {
      new Alert(Alert.AlertType.ERROR, "No portal provided").show();
      return;
    }

    // clear any existing items in the list view
    resultsListView.getItems().clear();

    // load the portal items
    portal.loadAsync();
    portal.addDoneLoadingListener(()->{
      if (portal.getLoadStatus() == LoadStatus.LOADED) {
        // update load state in UI with the portal URI

        // report the user name used for this connection
        if (portal.getUser() != null){
          System.out.println(portal.getUser().getUsername());
          loadStateTextView.setText("Connected as: " + portal.getUser().getUsername());
        }

        // search the portal for web maps
        ListenableFuture<PortalQueryResultSet<PortalItem>> portalItemResultFuture = portal.findItemsAsync(new PortalQueryParameters("type:(\"web map\" NOT \"web mapping application\")"));
        portalItemResultFuture.addDoneListener(()->{
          try {
            // get the result
            PortalQueryResultSet<PortalItem> portalItemSet = portalItemResultFuture.get();
            List<PortalItem> portalItems = portalItemSet.getResults();
            // add the items to the list view
            portalItems.forEach(portalItem -> resultsListView.getItems().add(portalItem));

          } catch (ExecutionException | InterruptedException e) {
            new Alert(Alert.AlertType.ERROR,  "Error getting portal item set from portal: " + e.getMessage()).show();
          }
        });

      } else {
        // report error
        new Alert(Alert.AlertType.ERROR, "Portal sign in failed: " + portal.getLoadError().getCause().getMessage()).show();
      }
    });
  }

  private void displayMap(){
    PortalItem selectedItem = resultsListView.getSelectionModel().getSelectedItem();
    System.out.println(selectedItem);
    ArcGISMap webMap = new ArcGISMap(selectedItem);
    mapView.setMap(webMap);
  }

  @FXML
  private void handleSearchPublicPress(){
    searchPortal(new Portal("http://www.arcgis.com"));
  }

  @FXML
  private void handleSearchSecurePress(){

    if (!portalUrlTextField.getText().isEmpty()) {
      // search an instance of the IWA-secured portal, the user may be challenged for access
      searchPortal(new Portal(portalUrlTextField.getText(), true));
    } else {
      new Alert(Alert.AlertType.ERROR, "Portal URL is empty. Please enter a portal URL.").show();
    }
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
