package com.esri.samples.portal.integrated_windows_authentication;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import com.esri.arcgisruntime.security.UserCredential;

class AuthenticationDialog extends Dialog {

  @FXML private TextField user_domain;
  @FXML private PasswordField password;
  @FXML private ButtonType cancelButton;
  @FXML private ButtonType continueButton;

  AuthenticationDialog() {

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/iwa_auth_dialog.fxml"));
    loader.setRoot(this);
    loader.setController(this);

    setTitle("Authenticate");

    try {
      loader.load();
    } catch (Exception e) {
      e.printStackTrace();
    }

    setResultConverter(dialogButton->{
      if (dialogButton == continueButton) {
        if (!user_domain.getText().equals("") && !password.getText().equals("")){
          try {
            return new UserCredential(user_domain.getText(), password.getText());
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
