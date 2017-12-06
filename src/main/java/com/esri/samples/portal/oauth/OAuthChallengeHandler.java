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

import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.esri.arcgisruntime.security.AuthenticationChallenge;
import com.esri.arcgisruntime.security.AuthenticationChallengeHandler;
import com.esri.arcgisruntime.security.AuthenticationChallengeResponse;
import com.esri.arcgisruntime.security.AuthenticationChallengeResponse.Action;
import com.esri.arcgisruntime.security.AuthenticationManager;
import com.esri.arcgisruntime.security.OAuthConfiguration;
import com.esri.arcgisruntime.security.OAuthTokenCredential;
import com.esri.arcgisruntime.security.OAuthTokenCredentialRequest;

/**
 * Handler to be used when accessing a secured resource.
 */
final class OAuthChallengeHandler implements AuthenticationChallengeHandler {

  /**
   * Handles challenge before accessing a secured resource.
   *
   * @param challenge the authentication challenge to handle
   * @return the AuthenticationChallengeResponse indicating which action to take
   */
  @Override
  public AuthenticationChallengeResponse handleChallenge(AuthenticationChallenge challenge) {
    try {
      // get config such as clientId from the authentication manager
      OAuthConfiguration config = AuthenticationManager.getOAuthConfiguration(challenge.getRemoteResource().getUri());

      // get the authorization code by sending user to the authorization screen
      String authorizationUrl = OAuthTokenCredentialRequest.getAuthorizationUrl(
          config.getPortalUrl(), config.getClientId(), config.getRedirectUri(), 0);
      String authorizationCode = OAuthChallenge.getAuthorizationCode(authorizationUrl);

      // use the authorization code to get a token
      OAuthTokenCredentialRequest request = new OAuthTokenCredentialRequest(
          config.getPortalUrl(), null, config.getClientId(), null, authorizationCode);
      OAuthTokenCredential credential = request.executeAsync().get();
      return new AuthenticationChallengeResponse(Action.CONTINUE_WITH_CREDENTIAL, credential);
    } catch (Exception e) {
      return new AuthenticationChallengeResponse(Action.CANCEL, null);
    }
  }
}

/**
 * This utility class provides a method to display the Authorization Screen as part of the OAuthSample workflow.
 */
final class OAuthChallenge {

  /**
   * Displays the Authorization Screen prompt to enter user credentials.
   *
   * @param authorizationUrl URL of the Authorization Screen
   * @return authorization code
   * @throws Exception if something goes wrong during authorization
   */
  static String getAuthorizationCode(String authorizationUrl) throws Exception {
    StringBuilder authorizationCode = new StringBuilder();
    CountDownLatch authorizationCodeLatch = new CountDownLatch(1);

    Platform.runLater(() -> {
      // display the authorization screen as a web view
      WebView browser = new WebView();
      Stage dialog = new Stage();
      dialog.initModality(Modality.APPLICATION_MODAL);
      dialog.setScene(new Scene(browser, 450, 450));
      dialog.show();
      WebEngine webEngine = browser.getEngine();
      webEngine.load(authorizationUrl);

      // read the HTTP response to user action
      webEngine.setOnStatusChanged(event -> {
        // extract code or error from the location in HTTP response
        if (event.getSource() instanceof WebEngine) {
          String location = webEngine.getLocation();
          if (location.contains("code=")) {
            authorizationCode.append(location.split("code=")[1]);
            authorizationCodeLatch.countDown();
            dialog.close();
          } else if (location.contains("error=")) {
            authorizationCodeLatch.countDown();
            dialog.close();
          }
        }
      });
    });

    // wait for authorization response
    authorizationCodeLatch.await();

    return authorizationCode.toString();
  }
}
