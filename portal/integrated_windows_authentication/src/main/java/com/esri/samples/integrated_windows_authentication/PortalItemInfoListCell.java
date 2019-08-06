package com.esri.samples.integrated_windows_authentication;

import javafx.scene.control.ListCell;
import javafx.util.Pair;

import com.esri.arcgisruntime.portal.PortalItem;

/**
 * Shows the title of the portal items in the selection list view.
 */
class PortalItemInfoListCell extends ListCell<PortalItem> {

  @Override
  protected void updateItem(PortalItem portalItem, boolean empty) {
    super.updateItem(portalItem, empty);
    if (portalItem != null) {
      // set the list cell's text to the map's index
      setText(portalItem.getTitle());
    } else {
      setText(null);
    }
  }
}
