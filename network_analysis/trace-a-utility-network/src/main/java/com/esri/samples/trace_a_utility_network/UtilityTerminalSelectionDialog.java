/*
 * Copyright 2019 Esri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.esri.samples.trace_a_utility_network;

import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;

import com.esri.arcgisruntime.utilitynetworks.UtilityTerminal;

class UtilityTerminalSelectionDialog extends ChoiceDialog<UtilityTerminal> {

  @FXML
  private ListView<UtilityTerminal> utilityTerminalListView;
  @FXML
  private ButtonType continueButton;

  UtilityTerminalSelectionDialog(List<UtilityTerminal> utilityTerminals) {

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/terminal_selection_dialog.fxml"));
    loader.setRoot(this);
    loader.setController(this);

    setTitle("Select Utility Terminal:");

    this.getDialogPane().setContent(utilityTerminalListView);

    try {
      loader.load();

      // use a string converter which shows the utility terminal's title
      utilityTerminalListView.setCellFactory(utilityTerminalLv -> {
        TextFieldListCell<UtilityTerminal> cell = new TextFieldListCell<>();
        cell.setConverter(new StringConverter<>() {
          @Override
          public String toString(UtilityTerminal utilityTerminal) {
            return utilityTerminal.getName();
          }

          @Override
          public UtilityTerminal fromString(String string) {
            return cell.getItem();
          }
        });

        return cell;
      });

      // add the list of terminals to the ListView
      utilityTerminalListView.getItems().addAll(utilityTerminals);

    } catch (Exception e) {
      e.printStackTrace();
    }

    // on 'continue', return the selected terminal
    setResultConverter(dialogButton -> {
      if (dialogButton == continueButton) {
        if (utilityTerminalListView.getSelectionModel().getSelectedItem() != null) {
          return utilityTerminalListView.getSelectionModel().getSelectedItem();
        } else {
          new Alert(Alert.AlertType.ERROR, "No terminal selected.").show();
        }
      }
      return null;
    });
  }
}
