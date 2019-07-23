package com.esri.samples.read_symbols_from_mobile_style_file;

import java.util.concurrent.ExecutionException;

import javafx.scene.control.Alert;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.symbology.SymbolStyleSearchResult;

/**
 * Shows the available symbol of the SymbolStyleSearchResult in the symbol selection list view.
 */
class SymbolLayerInfoListCell extends ListCell<SymbolStyleSearchResult> {

  SymbolLayerInfoListCell() {
    // set the cell to display only a graphic
    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
  }

  @Override
  protected void updateItem(SymbolStyleSearchResult item, boolean empty) {
    super.updateItem(item, empty);

    if (item == null || empty) {
      // if the item in the list view is an empty item, show nothing
      setGraphic(null);
    } else {
      // get the symbol from the list view entry, and create an image from it
      ListenableFuture<Image> symbolImageFuture = item.getSymbol().createSwatchAsync(0x00000000, 1);
      symbolImageFuture.addDoneListener(() -> {
        try {
          // get the resulting image
          Image symbolImage = symbolImageFuture.get();
          // create and image view and display it in the cell
          ImageView symbolImageView = new ImageView(symbolImage);
          setGraphic(symbolImageView);
        } catch (InterruptedException | ExecutionException e) {
          new Alert(Alert.AlertType.ERROR, "Error creating preview image for symbol in mobile style file" + e.getMessage()).show();
        }
      });
    }
  }
}
