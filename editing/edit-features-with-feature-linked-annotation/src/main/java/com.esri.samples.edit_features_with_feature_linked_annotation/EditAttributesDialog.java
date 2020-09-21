/*
 * Copyright 2020 Esri.
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

package com.esri.samples.edit_features_with_feature_linked_annotation;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;

import com.esri.arcgisruntime.data.Feature;

/**
 * Custom dialog for editing feature attributes.
 */
public class EditAttributesDialog extends Dialog<Boolean>{

  @FXML private TextField addressTextField;
  @FXML private TextField streetNameTextField;
  @FXML private ButtonType cancelButton;
  @FXML private ButtonType updateButton;

  EditAttributesDialog(Feature selectedFeature){
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/edit_features_with_feature_linked_annotation/edit_attributes_dialog.fxml"));
    loader.setRoot(this);
    loader.setController(this);

    setTitle("Edit Feature Attributes");

    try {
      loader.load();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // populate text fields with current attribute values
    addressTextField.setText(selectedFeature.getAttributes().get("AD_ADDRESS").toString());
    streetNameTextField.setText(selectedFeature.getAttributes().get("ST_STR_NAM").toString());

    // convert the result to an address and street name when the update button is clicked
    setResultConverter(dialogButton -> {
      if (dialogButton == updateButton) {
        try {
          // ensure input is equal to or less than 5 characters (max length for addresses in area)
          if (addressTextField.getLength() <= 5){
            // set AD_ADDRESS value to the int from the text field
            selectedFeature.getAttributes().put("AD_ADDRESS", Integer.parseInt(addressTextField.getText()));
          } else  {
            new Alert(Alert.AlertType.WARNING, "Field not updated. Integer must be less than 6 characters").showAndWait();
          }
          // set ST_STR_NAM value to the string from the text field
          selectedFeature.getAttributes().put("ST_STR_NAM", streetNameTextField.getText());
        } catch (Exception e) {
          e.printStackTrace();
        }
        return true;
      }
      return null;
    });
  }
}
