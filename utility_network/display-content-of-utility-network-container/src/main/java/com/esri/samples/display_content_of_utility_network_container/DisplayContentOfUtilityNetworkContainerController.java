/*
 * Copyright 2021 Esri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.esri.samples.display_content_of_utility_network_container;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.view.MapView;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.security.UserCredential;
import com.esri.arcgisruntime.utilitynetworks.UtilityElement;
import com.esri.arcgisruntime.utilitynetworks.UtilityNetwork;
import com.esri.arcgisruntime.utilitynetworks.UtilityTerminal;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceConditionalExpression;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceConfiguration;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraversability;
public class DisplayContentOfUtilityNetworkContainerController {

  @FXML private MapView mapView;

  private UtilityNetwork utilityNetwork;


  @FXML
  public void initialize() {

    try {

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a map with the streets night basemap style and set it to the map view
      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_STREETS_NIGHT);
      mapView.setMap(map);

      // load the utility network
      utilityNetwork = new UtilityNetwork(
          "https://sampleserver7.arcgisonline.com/server/rest/services/UtilityNetwork/NapervilleElectric" +
              "/FeatureServer");

      // set user credentials to authenticate with the service
      // NOTE: a licensed user is required to perform utility network operations
      UserCredential userCredential = new UserCredential("viewer01", "I68VGU^nMurF");
      utilityNetwork.setCredential(userCredential);

      utilityNetwork.loadAsync();
      utilityNetwork.addDoneLoadingListener(() -> {
        if (utilityNetwork.getLoadStatus() == LoadStatus.LOADED) {

        }
      });
    } catch (Exception e) {
      new Alert(Alert.AlertType.ERROR, "Error loading Utility Network.").show();
    }
  }

}
