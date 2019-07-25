package com.esri.samples.download_preplanned_map;

import java.io.ByteArrayInputStream;
import java.util.concurrent.ExecutionException;

import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.tasks.offlinemap.PreplannedMapArea;

/**
 * Shows the available thumbnail of the PreplannedMapArea in the selection list view.
 */
class PreplannedMapAreaListCell extends ListCell<PreplannedMapArea> {

  @Override
  protected void updateItem(PreplannedMapArea preplannedMapArea, boolean empty) {
    super.updateItem(preplannedMapArea, empty);
    if (preplannedMapArea != null) {
      HBox hBox = new HBox();
      hBox.setMinHeight(65);

      // show the preplanned map area thumbnail image
      ImageView thumbnailImageView = new ImageView();
      setGraphic(thumbnailImageView);
      hBox.getChildren().add(thumbnailImageView);
      thumbnailImageView.setFitHeight(65);
      thumbnailImageView.setPreserveRatio(true);
      ListenableFuture<byte[]> thumbnailData = preplannedMapArea.getPortalItem().fetchThumbnailAsync();
      thumbnailData.addDoneListener(() -> {
        try {
          thumbnailImageView.setImage(new Image(new ByteArrayInputStream(thumbnailData.get())));
        } catch (InterruptedException | ExecutionException e) {
          new Alert(Alert.AlertType.ERROR, "Error getting preplanned map area thumbnails" + e.getMessage()).show();
        }
      });

      setGraphic(hBox);

      setText(preplannedMapArea.getPortalItem().getTitle());

    } else {
      setGraphic(null);
      setText(null);
    }
  }
}
