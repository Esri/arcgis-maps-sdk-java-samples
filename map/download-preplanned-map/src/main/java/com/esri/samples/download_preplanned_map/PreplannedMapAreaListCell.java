package com.esri.samples.download_preplanned_map;

import javafx.scene.control.ListCell;

import com.esri.arcgisruntime.tasks.offlinemap.PreplannedMapArea;

/**
 * Shows the title of the PreplannedMapArea in the selection list view.
 */
class PreplannedMapAreaListCell extends ListCell<PreplannedMapArea> {

  @Override
  protected void updateItem(PreplannedMapArea preplannedMapArea, boolean empty) {
    super.updateItem(preplannedMapArea, empty);
    if (preplannedMapArea != null) {

      setText(preplannedMapArea.getPortalItem().getTitle());

    } else {
      setGraphic(null);
      setText(null);
    }
  }
}
