package com.esri.samples.symbology;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.symbology.CimSymbol;
import com.esri.arcgisruntime.symbology.StyleSymbolSearchResult;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class SymbolView extends HBox implements Initializable {

  private @FXML ImageView imageView;
  private @FXML Label name;
  private @FXML Label tags;
  private @FXML Label symbolClass;
  private @FXML Label category;
  private @FXML Label key;
  
  StyleSymbolSearchResult styleSymbolSearchResult;

  /**
   * Creates a view of a symbol with a picture and description.
   *
   * @param symbolResult symbol result from a symbol dictionary search
   */
  public SymbolView(StyleSymbolSearchResult symbolResult) {
    styleSymbolSearchResult = symbolResult;

    // Set the view of this component to the fxml file
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/symbol_view.fxml"));
    loader.setRoot(this);
    loader.setController(this);

    try {
      loader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // initialize the component values
    name.setText(styleSymbolSearchResult.getName());
    tags.setText(styleSymbolSearchResult.getTags().toString());
    symbolClass.setText(styleSymbolSearchResult.getSymbolClass());
    category.setText(styleSymbolSearchResult.getCategory());
    key.setText(styleSymbolSearchResult.getKey());

    // set image for non-text symbols
    if (!category.getText().startsWith("Text")) {
      CimSymbol symbol = styleSymbolSearchResult.getSymbol();
      ListenableFuture<Image> imageResult = symbol.createSwatchAsync(40, 40, 100, 0x00FFFFFF, new Point(0, 0, 0));
      imageResult.addDoneListener(() -> {
        try {
          imageView.setImage(imageResult.get());
        } catch (ExecutionException | InterruptedException e) {
          e.printStackTrace();
        }
      });
    }
  }
}
