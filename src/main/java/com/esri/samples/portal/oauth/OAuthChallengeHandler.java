/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esri.samples.portal.oauth;

import com.esri.arcgisruntime.security.*;
import com.esri.arcgisruntime.security.AuthenticationChallengeResponse.Action;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.concurrent.CountDownLatch;

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
        config.getPortalUrl(), config.getClientId(), config.getRedirectUri(), 0 /* expiration */);
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
  public static String getAuthorizationCode(String authorizationUrl) throws Exception {
    StringBuilder authorizationCode = new StringBuilder();
    CountDownLatch authorizationCodeLatch = new CountDownLatch(1);

    Platform.runLater(() -> {
      WebView browser = new WebView();
      Stage dialog = Controller.showDialog(browser, 450, 450);
      WebEngine webEngine = browser.getEngine();

      webEngine.load(authorizationUrl);
      webEngine.setOnStatusChanged(new EventHandler<WebEvent<String>>() {
        public void handle(WebEvent<String> event) {
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
        }
      });
    });

    // wait for authorization response
    authorizationCodeLatch.await();

    return authorizationCode.toString();
  }
}