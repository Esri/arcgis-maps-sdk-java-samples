package com.esri.samples.trace_a_utility_network;

import javafx.scene.control.ListCell;

import com.esri.arcgisruntime.utilitynetworks.UtilityTerminal;

public class UtilityTerminalListCell extends ListCell<UtilityTerminal> {
  @Override
  protected void updateItem(UtilityTerminal item, boolean empty) {
    super.updateItem(item, empty);
    if (empty) {
      setText(null);
    } else {
      setText(item.getName());
    }
  }
}
