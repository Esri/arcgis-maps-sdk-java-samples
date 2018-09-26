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

package com.esri.samples.portal.oauth;

import java.text.SimpleDateFormat;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.security.AuthenticationManager;
import com.esri.arcgisruntime.security.OAuthConfiguration;

public class OAuthController {

  @FXML private Label fullName;
  @FXML private Label username;
  @FXML private Label email;
  @FXML private Label memberSince;
  @FXML private Label role;

  private SimpleDateFormat formatter;

  @FXML
  private void initialize() {
    formatter = new SimpleDateFormat("MMM dd, yyyy");
  }

  void authenticate() {

    AuthenticationDialog authenticationDialog = new AuthenticationDialog();
    authenticationDialog.initOwner(fullName.getScene().getWindow());
    authenticationDialog.show();
    authenticationDialog.setOnCloseRequest(r -> {

      OAuthConfiguration configuration = authenticationDialog.getResult();
      // check that configuration was made
      if (configuration != null) {
        AuthenticationManager.addOAuthConfiguration(configuration);

        // setup the handler that will prompt an authentication challenge to the user
        AuthenticationManager.setAuthenticationChallengeHandler(new OAuthChallengeHandler());

        Portal portal = new Portal("http://" + configuration.getPortalUrl(), true);
        portal.addDoneLoadingListener(() -> {
          if (portal.getLoadStatus() == LoadStatus.LOADED) {

            // display portal user info
            fullName.setText(portal.getUser().getFullName());
            username.setText(portal.getUser().getUsername());
            email.setText(portal.getUser().getEmail());
            memberSince.setText(formatter.format(portal.getUser().getCreated().getTime()));
            role.setText(portal.getUser().getRole() != null ? portal.getUser().getRole().name() : "");

          } else if (portal.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {

            // show alert message on error
            new Alert(Alert.AlertType.ERROR, "Authentication failed: " + portal.getLoadError().getCause().getMessage()).show();
          }
        });

        // loading the portal info of a secured resource
        // this will invoke the authentication challenge
        portal.loadAsync();
      }
    });
  }

}
