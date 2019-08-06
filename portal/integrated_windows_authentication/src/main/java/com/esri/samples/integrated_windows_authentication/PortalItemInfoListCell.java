package com.esri.samples.integrated_windows_authentication;

import javafx.scene.control.ListCell;
import javafx.util.Pair;

/**
 * Shows the title of the portal items in the selection list view.
 */
class PortalItemInfoListCell extends ListCell<Pair<String, String>> {

  @Override
  protected void updateItem(Pair<String, String> portalItemInfo, boolean empty) {
    super.updateItem(portalItemInfo, empty);
    if (portalItemInfo != null) {
      // set the list cell's text to the map's index
      setText(portalItemInfo.getValue());
    } else {
      setText(null);
    }
  }
}
