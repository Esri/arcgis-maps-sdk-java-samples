/*
 * Copyright 2020 Esri.
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
package com.esri.samples.feature_collection_layer_from_portal;

import com.esri.arcgisruntime.data.FeatureCollection;
import com.esri.arcgisruntime.layers.FeatureCollectionLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class FeatureCollectionLayerFromPortalController {


    @FXML
    private MapView mapView;
    @FXML
    private TextField FeatureCollectionItemIdTextField;

   private String FCItemId = "32798dfad17942858d5eef82ee802f0b";

    public void initialize()
    {
        try {

            // create amp and set it to be displayed in this view
            ArcGISMap map = new ArcGISMap(Basemap.createOceans());
            mapView = new MapView();
            mapView.setMap(map);

            // load the portal and add the portal item
            Portal portal = new Portal("https://www.arcgis.com/");
            PortalItem portalItem = new PortalItem(portal,FCItemId);

            // create feature collection and add to the map as a layer
            FeatureCollection featureCollection = new FeatureCollection(portalItem);
            FeatureCollectionLayer featureCollectionLayer = new FeatureCollectionLayer(featureCollection);
            map.getOperationalLayers().add(featureCollectionLayer);

        } catch (Exception e) {
            // on any error, display the stack trace.
            e.printStackTrace();
        }
    }



    /**
     * Stops and releases all resources used in the application.
     */
    void terminate() {

        if (mapView != null) {
            mapView.dispose();
        }
    }


}