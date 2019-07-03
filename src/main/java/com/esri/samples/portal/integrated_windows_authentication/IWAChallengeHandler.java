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

package com.esri.samples.portal.integrated_windows_authentication;

import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.security.AuthenticationChallenge;
import com.esri.arcgisruntime.security.AuthenticationChallengeHandler;
import com.esri.arcgisruntime.security.AuthenticationChallengeResponse;
import com.esri.arcgisruntime.security.UserCredential;

/**
 * Handler to be used when accessing a secured resource.
 */
final class IWAChallengeHandler implements AuthenticationChallengeHandler {

  private UserCredential userCredential;

  /**
   * Handles challenge before accessing a secured resource.
   *
   * @param authenticationChallenge the authentication challenge to handle
   * @return the AuthenticationChallengeResponse indicating which action to take
   */
  @Override
  public AuthenticationChallengeResponse handleChallenge(AuthenticationChallenge authenticationChallenge) {

    if (authenticationChallenge.getType() == AuthenticationChallenge.Type.USER_CREDENTIAL_CHALLENGE && authenticationChallenge.getRemoteResource() instanceof Portal) {

      // If challenge has been requested by a Portal and the Portal has been loaded, cancel the challenge
      // This is required as some layers have private portal items associated with them and we don't
      // want to auth against them
      if (((Portal) authenticationChallenge.getRemoteResource()).getLoadStatus() == LoadStatus.LOADED) {
        return new AuthenticationChallengeResponse(AuthenticationChallengeResponse.Action.CANCEL, authenticationChallenge);
      }

      int maxAttempts = 5;
      if (authenticationChallenge.getFailureCount() > maxAttempts) {
        // exceeded maximum amount of attempts. Act like it was a cancel
        new Alert(Alert.AlertType.ERROR, "Exceeded maximum amount of attempts. Please try again!").show();
        return new AuthenticationChallengeResponse(AuthenticationChallengeResponse.Action.CANCEL, authenticationChallenge);
      }

      // create a countdown latch with a count of one to synchronize the authentication dialog
      CountDownLatch authenticationCountDownLatch = new CountDownLatch(1);
      // show the authentication dialog and capture the user credentials
      Platform.runLater(() -> {
        AuthenticationDialog authenticationDialog = new AuthenticationDialog();
        authenticationDialog.show();
        authenticationDialog.setOnCloseRequest(r -> {
          userCredential = (UserCredential) authenticationDialog.getResult();
          authenticationCountDownLatch.countDown();
        });
      });

      try {
        authenticationCountDownLatch.await();
      } catch (InterruptedException e) {
        new Alert(Alert.AlertType.ERROR, "Interruption handling AuthenticationChallengeResponse: " + e.getMessage()).show();
      }

      // if credentials were set, return a new auth challenge response with them. otherwise, act like it was a cancel action
      if (userCredential != null) {
        return new AuthenticationChallengeResponse(AuthenticationChallengeResponse.Action.CONTINUE_WITH_CREDENTIAL, userCredential);
      }
    }

    // no credentials were set, return a new auth challenge response with with a cancel action
    return new AuthenticationChallengeResponse(AuthenticationChallengeResponse.Action.CANCEL, authenticationChallenge);
  }
}
