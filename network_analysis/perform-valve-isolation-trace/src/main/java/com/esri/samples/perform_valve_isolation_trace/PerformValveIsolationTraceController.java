/*
 * Copyright 2020 Esri.
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

package com.esri.samples.perform_valve_isolation_trace;

import com.esri.arcgisruntime.mapping.view.MapView;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class PerformValveIsolationTraceController {

  @FXML private MapView mapView;
  @FXML private ProgressIndicator progressIndicator;
  @FXML private ComboBox filterSelectionComboBox;
  @FXML private Button traceButton;
  @FXML private Label statusLabel;
  @FXML private CheckBox includeIsolatedFeaturesCheckbox;

  public void initialize() {
    try {

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void handleTraceClick(){
  }

  /**
   * Stops and releases all resources used in application.
   */
  public void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }
}
