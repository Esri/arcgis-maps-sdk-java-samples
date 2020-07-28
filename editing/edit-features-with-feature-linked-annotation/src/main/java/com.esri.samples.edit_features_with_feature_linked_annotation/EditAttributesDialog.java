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

public class EditAttributesDialog extends Dialog<String>{

  @FXML private TextField addressTextField;
  @FXML private TextField streetNameTextField;
  @FXML private ButtonType continueButton;
  @FXML private ButtonType cancelButton;

  EditAttributesDialog(Feature selectedFeature){
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/edit_features_with_feature_linked_annotation/edit_features_dialog.fxml"));
    loader.setRoot(this);
    loader.setController(this);

    setTitle("Edit Feature Attributes");

    try {
      loader.load();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // populate edit texts with current attribute values
    addressTextField.setText(selectedFeature.getAttributes().get("AD_ADDRESS").toString());
    streetNameTextField.setText(selectedFeature.getAttributes().get("ST_STR_NAM").toString());

    // convert the result to an address and street name when the ok button is clicked.
    setResultConverter(dialogButton -> {
      if (dialogButton == continueButton) {
        try {
          // set AD_ADDRESS value to the int from the edit text
          selectedFeature.getAttributes().put("AD_ADDRESS", Integer.parseInt(addressTextField.getText()));

          // set ST_STR_NAM value to the string from edit text
          selectedFeature.getAttributes().put("ST_STR_NAM", streetNameTextField.getText());

          // update the selected feature's feature table
          selectedFeature.getFeatureTable().updateFeatureAsync(selectedFeature);

        } catch (Exception e) {
          new Alert(Alert.AlertType.ERROR, "Cannot update attributes " + e.getMessage()).show();
        }
      }
      return null;
    });
  }
}
