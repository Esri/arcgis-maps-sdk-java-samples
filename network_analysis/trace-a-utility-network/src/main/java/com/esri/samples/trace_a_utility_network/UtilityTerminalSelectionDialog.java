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
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;

import com.esri.arcgisruntime.utilitynetworks.UtilityTerminal;

class UtilityTerminalSelectionDialog extends Dialog<UtilityTerminal> {

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

      // use a cell factory which shows the utility terminal's title
      utilityTerminalListView.setCellFactory(c -> new UtilityTerminalListCell());

      // add the list of terminals to the ListView
      utilityTerminalListView.getItems().addAll(utilityTerminals);

    } catch (Exception e) {
      e.printStackTrace();
    }

    // on 'continue', return the selected terminal
    setResultConverter(dialogButton -> {
      if (dialogButton == continueButton) {
        if (utilityTerminalListView.getSelectionModel().getSelectedItem() != null) {
          try {
            return utilityTerminalListView.getSelectionModel().getSelectedItem();
          } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error selecting terminal.").show();
          }
        } else {
          new Alert(Alert.AlertType.ERROR, "No terminal selected.").show();
        }
      }
      return null;
    });
  }
}
