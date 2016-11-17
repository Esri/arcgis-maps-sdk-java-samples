/*
 * Copyright 2016 Esri.
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

import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.security.AuthenticationManager;
import com.esri.arcgisruntime.security.OAuthConfiguration;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Controller for the Sign-in view.
 */
public class OAuthSignInController {

  private OAuthConfiguration oAuthConfiguration;

  @FXML
  private TextField portalURL;

  @FXML
  private TextField clientId;

  @FXML
  private TextField redirectUri;

  @FXML
  private Button signIn;

  public void initialize() {
    Platform.runLater(() -> signIn.requestFocus());
  }
  
  @FXML
  private void handleSignIn() throws Exception {
    // setup the OAuthSample info such as clientId
    OAuthConfiguration oAuthConfiguration = new OAuthConfiguration(
      portalURL.getText(), clientId.getText(), redirectUri.getText());
    AuthenticationManager.addOAuthConfiguration(oAuthConfiguration);

    // setup the handler that will prompt an authentication challenge to the user
    AuthenticationManager.setAuthenticationChallengeHandler(new OAuthChallengeHandler());

    // loading the portal info of a secured resource will invoke the authentication challenge
    Portal portal = new Portal(portalURL.getText(), true);
    portal.addDoneLoadingListener(() -> {
      if (portal.getLoadStatus() == LoadStatus.LOADED) {
        Controller.show(portal.getUser());
      } else if (portal.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
        Controller.showError(portal.getLoadError().getCause().getMessage());
      }
    });
    portal.loadAsync();
  }
}
