/*
 * Copyright 2018 Esri.
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

package com.esri.samples.displayinformation.sketch_on_map;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import javafx.fxml.FXML;

public class SketchOnMapController {

  @FXML private MapView mapView;
  // buttons go in here too?



  public void initialize(){
    ArcGISMap map = new ArcGISMap(Basemap.Type.IMAGERY, 64.3286, -15.5314, 13);
    mapView.setMap(map);



  }

  void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }

}
