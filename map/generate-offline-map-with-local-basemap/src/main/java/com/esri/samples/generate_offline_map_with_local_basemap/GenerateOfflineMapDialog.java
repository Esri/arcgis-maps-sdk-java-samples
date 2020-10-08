/*
 * Copyright 2019 Esri.
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

package com.esri.samples.generate_offline_map_with_local_basemap;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;

/**
 * Custom dialog for selection of local basemap or online base map when generating an offline map.
 */
class GenerateOfflineMapDialog extends Dialog<Boolean> {

  @FXML
  private Label referencedBasemapFileNameLabel;
  @FXML
  private ButtonType localBasemapButton;
  @FXML
  private ButtonType downloadBasemapButton;

  private final StringProperty referencedBasemapFileName = new SimpleStringProperty();

  GenerateOfflineMapDialog() {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/generate_offline_map_with_local_basemap" +
            "/basemap_dialog.fxml"));
    loader.setRoot(this);
    loader.setController(this);

    setTitle("Generate offline map");

    try {
      loader.load();

      referencedBasemapFileNameLabel.textProperty().bind(referencedBasemapFileName);
    } catch (Exception e) {
      e.printStackTrace();
    }

    setResultConverter(dialogButton -> {
      if (dialogButton == localBasemapButton) {
        return true;
      } else if (dialogButton == downloadBasemapButton) {
        return false;
      }
      return null;
    });
  }

  public String getReferencedBasemapFileName() {
    return referencedBasemapFileName.get();
  }

  public StringProperty referencedBasemapFileNameProperty() {
    return referencedBasemapFileName;
  }

  public void setReferencedBasemapFileName(String referencedBasemapFileName) {
    this.referencedBasemapFileName.set(referencedBasemapFileName);
  }
}
