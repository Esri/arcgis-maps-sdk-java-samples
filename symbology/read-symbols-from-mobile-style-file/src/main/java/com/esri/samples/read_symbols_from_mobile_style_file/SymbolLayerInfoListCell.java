package com.esri.samples.read_symbols_from_mobile_style_file;

import java.util.concurrent.ExecutionException;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.symbology.SymbolStyleSearchResult;

/**
 * Shows the available symbol of the SymbolStyleSearchResult in the symbol selection list view.
 */
class SymbolLayerInfoListCell extends ListCell<SymbolStyleSearchResult> {

  SymbolLayerInfoListCell() {
    // set the cell to display only a graphic
    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    setAlignment(Pos.CENTER);
  }

  @Override
  protected void updateItem(SymbolStyleSearchResult item, boolean empty) {
    super.updateItem(item, empty);

    if (item == null || empty) {
      // if the item in the list view is an empty item, show nothing
      setGraphic(null);
    } else {
      ImageView symbolImageView = new ImageView();
      setGraphic(symbolImageView);
      // get the symbol from the list view entry, and create an image from it
      ListenableFuture<Symbol> symbolFuture = item.getSymbolAsync();
      symbolFuture.addDoneListener(() -> {
        try {
          // get the resulting symbol
          Symbol symbol = symbolFuture.get();
          // create a bitmap swatch from the symbol
          ListenableFuture<Image> imageListenableFuture = symbol.createSwatchAsync(Color.TRANSPARENT, 1);
          imageListenableFuture.addDoneListener(() -> {
            try {
              Image symbolImage = imageListenableFuture.get();
              // update the image view with the symbol swatch
              symbolImageView.setImage(symbolImage);
            } catch (InterruptedException | ExecutionException e) {
              new Alert(Alert.AlertType.ERROR, "Error creating preview image for symbol in mobile style file" + e.getMessage()).show();
            }
          });
        } catch (InterruptedException | ExecutionException e) {
          new Alert(Alert.AlertType.ERROR, "Error getting symbol" + e.getMessage()).show();
        }
      });
    }
  }
}
