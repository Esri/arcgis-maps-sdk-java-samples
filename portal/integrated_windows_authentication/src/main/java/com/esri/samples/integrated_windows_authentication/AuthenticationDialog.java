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

package com.esri.samples.integrated_windows_authentication;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import com.esri.arcgisruntime.security.UserCredential;

class AuthenticationDialog extends Dialog {

  @FXML private TextField userdomain;
  @FXML private PasswordField password;
  @FXML private ButtonType cancelButton;
  @FXML private ButtonType continueButton;

  AuthenticationDialog() {

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/iwa_auth_dialog.fxml"));
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
        if (!userdomain.getText().equals("") && !password.getText().equals("")) {
          try {
            return new UserCredential(userdomain.getText(), password.getText());
          } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
          }
        } else {
          new Alert(Alert.AlertType.ERROR, "Missing credentials").show();
        }
      }
      return null;
    });
  }

}
