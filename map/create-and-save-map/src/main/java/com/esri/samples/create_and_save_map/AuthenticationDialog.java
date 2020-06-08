/*
 * Copyright 2017 Esri.
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

package com.esri.samples.create_and_save_map;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;

import com.esri.arcgisruntime.security.OAuthConfiguration;

/**
 * Custom dialog for getting an OAuthConfiguration.
 */
class AuthenticationDialog extends Dialog<OAuthConfiguration> {

  @FXML private TextField portalURL;
  @FXML private TextField clientID;
  @FXML private TextField redirectURI;
  @FXML private ButtonType cancelButton;
  @FXML private ButtonType continueButton;

  AuthenticationDialog() {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/create_and_save_map/auth_dialog.fxml"));
    loader.setRoot(this);
    loader.setController(this);

    setTitle("Authenticate");

    try {
      loader.load();
    } catch (Exception e) {
      e.printStackTrace();
    }

    setResultConverter(dialogButton -> {
      if (dialogButton == continueButton) {
        if (!portalURL.getText().equals("") && !clientID.getText().equals("") && !redirectURI.getText().equals("")) {
          try {
            return new OAuthConfiguration(portalURL.getText(), clientID.getText(), redirectURI.getText());
          } catch (Exception e) {
            showMessage(e.getMessage());
          }
        } else {
          showMessage("missing credentials");
        }
      }
      return null;
    });
  }

  private void showMessage(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.initOwner(this.getOwner());
    alert.setContentText(message);
    alert.show();
  }

}
