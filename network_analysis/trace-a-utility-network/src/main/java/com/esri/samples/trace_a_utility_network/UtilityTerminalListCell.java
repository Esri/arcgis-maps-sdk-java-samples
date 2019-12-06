/* Copyright 2019 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.esri.samples.trace_a_utility_network;

import javafx.scene.control.ListCell;

import com.esri.arcgisruntime.utilitynetworks.UtilityTerminal;

/**
 * Shows the title of the UtilityTerminal in the selection list view.
 */
class UtilityTerminalListCell extends ListCell<UtilityTerminal> {

  @Override
  protected void updateItem(UtilityTerminal utilityTerminal, boolean empty) {
    super.updateItem(utilityTerminal, empty);
    if (utilityTerminal != null) {

      setText(utilityTerminal.getName());

    } else {
      setGraphic(null);
      setText(null);
    }
  }
}