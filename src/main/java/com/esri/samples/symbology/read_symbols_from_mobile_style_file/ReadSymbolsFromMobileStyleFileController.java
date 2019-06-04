/*
 * Copyright 2019 Esri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.esri.samples.symbology.read_symbols_from_mobile_style_file;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.MultilayerPointSymbol;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.symbology.SymbolStyle;
import com.esri.arcgisruntime.symbology.SymbolStyleSearchParameters;
import com.esri.arcgisruntime.symbology.SymbolStyleSearchResult;

public class ReadSymbolsFromMobileStyleFileController {

  @FXML
  private MapView mapView = new MapView();
  @FXML
  private HBox symbolPreview = new HBox();
  @FXML
  private ListView hatSelectionComboBox = new ListView<SymbolLayerInfo>();
  @FXML
  private ListView eyesSelectionComboBox = new ListView<SymbolLayerInfo>();
  @FXML
  private ListView mouthSelectionComboBox = new ListView<SymbolLayerInfo>();
  @FXML
  private ListView colorSelectionComboBox = new ListView<Rectangle>();
  @FXML
  private Slider sizeSlider = new Slider();

  private MultilayerPointSymbol faceSymbol;
  private SymbolStyle emojiStyle;

  @FXML
  public void initialize() {

    // create a map
    ArcGISMap map = new ArcGISMap(Basemap.createTopographic());
    // add the map to the map view
    mapView.setMap(map);

    // create a graphics overlay and add it to the map
    GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
    mapView.getGraphicsOverlays().add(graphicsOverlay);

    // add colors to the color selection combo box
    class ColorListCell extends ListCell<Integer> {
      private final Rectangle rectangle;

      {
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        rectangle = new Rectangle(10, 10);
      }

      protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
          setGraphic(null);
        } else {
          switch (item) {
            case (0xFFFFFF00):
              rectangle.setFill(Color.YELLOW);
              break;
            case (0xFF00FF00):
              rectangle.setFill(Color.GREEN);
              break;
            case (0xFF0000FF):
              rectangle.setFill(Color.BLUE);
              break;
          }
          setGraphic(rectangle);
        }
      }
    }

    colorSelectionComboBox.getItems().addAll(0xFFffff00, 0xFF00FF00, 0xFF0000FF);
    colorSelectionComboBox.setCellFactory(c -> new ColorListCell());
//    colorSelectionComboBox.setButtonCell(new ColorListCell());

    colorSelectionComboBox.getSelectionModel().select(0);

    // load the available symbols from the style file
    loadSymbolsFromStyleFile();

    // add symbols to the symbol selection combo box
    class SymbolLayerInfoListCell extends ListCell<SymbolLayerInfo> {
      private ImageView imageView;

      {
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        imageView = new ImageView();
      }

      protected void updateItem(SymbolLayerInfo item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
          setGraphic(null);
        } else {
          imageView = item.getImagePreview();
          setGraphic(imageView);
        }
      }
    }

    ChangeListener<Object> changeListener = (ObservableValue<? extends Object> observable, Object oldValue, Object newValue) -> buildCompositeSymbol();

    ListView[] listViews = new ListView[]{hatSelectionComboBox, eyesSelectionComboBox, mouthSelectionComboBox, colorSelectionComboBox};

    for (int i = 0; i < listViews.length; i++) {
      listViews[i].getSelectionModel().selectedItemProperty().addListener(changeListener);
    }

    hatSelectionComboBox.setCellFactory(c -> new SymbolLayerInfoListCell());
//    hatSelectionComboBox.setButtonCell(new SymbolLayerInfoListCell());
    eyesSelectionComboBox.setCellFactory(c -> new SymbolLayerInfoListCell());
//    eyesSelectionComboBox.setButtonCell(new SymbolLayerInfoListCell());
    mouthSelectionComboBox.setCellFactory(c -> new SymbolLayerInfoListCell());
//    mouthSelectionComboBox.setButtonCell(new SymbolLayerInfoListCell());

    // listen to mouse clicks to add the desired multi layer symbol
    mapView.setOnMouseClicked(e -> {
      // convert clicked point to a map point
      Point mapPoint = mapView.screenToLocation(new Point2D(e.getX(), e.getY()));

      // create a new graphic with the point and symbol
      Graphic graphic = new Graphic(mapPoint, faceSymbol);
      graphicsOverlay.getGraphics().add(graphic);

    });

    // add a listener to the slider to update the preview when the size is changed
    sizeSlider.valueProperty().addListener(o -> {
      if (!sizeSlider.isValueChanging()) {
        buildCompositeSymbol();
      }
    });

  }

  /**
   * Loads the stylx file and searches for all symbols contained within. Put the resulting symbols into the GUI
   * based on their category (eyes, mouth, hat, face).
   */
  private void loadSymbolsFromStyleFile() {
    // create a SymbolStyle by passing the location of the .stylx file in the constructor
    emojiStyle = new SymbolStyle("./samples-data/stylx/emoji-mobile.stylx");
    emojiStyle.loadAsync();

    // add a listener to run when the symbol style has loaded
    emojiStyle.addDoneLoadingListener(() -> {
      if (emojiStyle.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
        new Alert(Alert.AlertType.ERROR, "Error: could not load .stylx file. Details: " + emojiStyle.getLoadError().getMessage()).show();
        return;
      }

      // load the default search parameters
      ListenableFuture<SymbolStyleSearchParameters> defaultSearchParametersFuture = emojiStyle.getDefaultSearchParametersAsync();
      defaultSearchParametersFuture.addDoneListener(() -> {
        try {
          SymbolStyleSearchParameters defaultSearchParameters = defaultSearchParametersFuture.get();

          // use the default parameters to perform the search
          ListenableFuture<List<SymbolStyleSearchResult>> symbolStyleSearchResultFuture = emojiStyle.searchSymbolsAsync(defaultSearchParameters);
          symbolStyleSearchResultFuture.addDoneListener(() -> {
            try {

              // create an empty placeholder image to represent "no symbol" for each category
              ImageView emptyImage = null;

              // create lists to contain the available symbol layers for each category of symbol and add an empty entry as default
              ArrayList<SymbolLayerInfo> eyeSymbolInfos = new ArrayList<>(Collections.singletonList(new SymbolLayerInfo( "", emptyImage)));
              ArrayList<SymbolLayerInfo> mouthSymbolInfos = new ArrayList<>(Collections.singletonList(new SymbolLayerInfo( "", emptyImage)));
              ArrayList<SymbolLayerInfo> hatSymbolInfos = new ArrayList<>(Collections.singletonList(new SymbolLayerInfo("", emptyImage)));

              // loop through the results and add symbols infos into the list according to category
              List<SymbolStyleSearchResult> symbolStyleSearchResults = symbolStyleSearchResultFuture.get();
              for (SymbolStyleSearchResult symbolStyleSearchResult : symbolStyleSearchResults) {

                // get the symbol for this result
                MultilayerPointSymbol multilayerPointSymbol = (MultilayerPointSymbol) symbolStyleSearchResult.getSymbol();

                // create a swatch image for the symbol (to be used for the preview)
                ListenableFuture<Image> imageListenableFuture = multilayerPointSymbol.createSwatchAsync(0x00000000, 1);
                imageListenableFuture.addDoneListener(() -> {
                  try {
                    Image image = imageListenableFuture.get();
                    ImageView imagePreview = new ImageView(image);

                    // create a symbol layer info object to represent the found symbol in the list
                    SymbolLayerInfo symbolLayerInfo = new SymbolLayerInfo(symbolStyleSearchResult.getKey(), imagePreview);

                    // add the symbol layer info object to the correct list for its category
                    switch (symbolStyleSearchResult.getCategory().toLowerCase()) {
                      case "eyes":
                        eyeSymbolInfos.add(symbolLayerInfo);
                        // add the preview of the symbol to the preview container
                        eyesSelectionComboBox.getItems().add(symbolLayerInfo);
                        break;
                      case "mouth":
                        mouthSymbolInfos.add(symbolLayerInfo);
                        // add the preview of the symbol to the preview container
                        mouthSelectionComboBox.getItems().add(symbolLayerInfo);
                        break;
                      case "hat":
                        hatSymbolInfos.add(symbolLayerInfo);
                        // add the preview of the symbol to the preview container
                        hatSelectionComboBox.getItems().add(symbolLayerInfo);
                        break;
                    }

                  } catch (InterruptedException | ExecutionException e) {
                    new Alert(Alert.AlertType.ERROR, "Error creating preview image for symbol in mobile style file" + e.getMessage()).show();
                  }
                });
              }
              // create the symbol preview

              hatSelectionComboBox.getSelectionModel().select(1);
              mouthSelectionComboBox.getSelectionModel().select(1);
              eyesSelectionComboBox.getSelectionModel().select(1);
              buildCompositeSymbol();

            } catch (InterruptedException | ExecutionException e) {
              new Alert(Alert.AlertType.ERROR, "Error performing the symbol search" + e.getMessage()).show();
            }
          });
        } catch (InterruptedException | ExecutionException e) {
          new Alert(Alert.AlertType.ERROR, "Error retrieving default search parameters for symbol search" + e.getMessage()).show();
        }
      });
    });
  }

  @FXML
  private void buildCompositeSymbol() {

    // remove the previously displayed image view
    symbolPreview.getChildren().clear();

    // retrieve the requested key for the requested hat symbol
    SymbolLayerInfo requestedHat = (SymbolLayerInfo) hatSelectionComboBox.getSelectionModel().getSelectedItem();
    String hatKey = requestedHat != null ? requestedHat.getKey() : "";

    // retrieve the requested key for the requested eyes symbol
    SymbolLayerInfo requestedEyes = (SymbolLayerInfo) eyesSelectionComboBox.getSelectionModel().getSelectedItem();
    String eyesKey = requestedEyes != null ? requestedEyes.getKey() : "";

    // retrieve the requested key for the requested mouth symbol
    SymbolLayerInfo requestedMouth = (SymbolLayerInfo) mouthSelectionComboBox.getSelectionModel().getSelectedItem();
    String mouthKey = requestedMouth != null ? requestedMouth.getKey() : "";

    List<String> symbolKeys = Arrays.asList("Face1", eyesKey, mouthKey, hatKey);

    ListenableFuture<Symbol> symbolFuture = emojiStyle.getSymbolAsync(symbolKeys);
    symbolFuture.addDoneListener(() -> {
      try {
        faceSymbol = (MultilayerPointSymbol) symbolFuture.get();
        if (faceSymbol == null) {
          return;
        }

        // set the size of the symbol
        faceSymbol.setSize((float) sizeSlider.getValue() * 10);

        // loop through all the symbol layers and lock the color
        faceSymbol.getSymbolLayers().forEach(symbolLayer -> symbolLayer.setColorLocked(true));

        // unlock the color of the base layer. Changing the symbol layer will now only change this layer's color
        faceSymbol.getSymbolLayers().get(0).setColorLocked(false);

        // set the color of the symbol
        faceSymbol.setColor((Integer) colorSelectionComboBox.getSelectionModel().getSelectedItem());

        // create an image and image view from the symbol
        ListenableFuture<Image> symbolImageFuture = faceSymbol.createSwatchAsync(0x00000000, 1);
        Image symbolImage = symbolImageFuture.get();
        ImageView symbolImageView = new ImageView(symbolImage);
        // display the image view in the preview area
        symbolPreview.getChildren().add(symbolImageView);

      } catch (ExecutionException | InterruptedException e) {
        new Alert(Alert.AlertType.ERROR, "Error creating symbol with the provided symbol keys" + e.getMessage()).show();
      }
    });
  }

  // a class used to store the information about a symbol layer
  private class SymbolLayerInfo {
    // an image view used to preview the symbol
    private ImageView imagePreview;

    // a key that identifies the symbol within the style
    private String key;

    private SymbolLayerInfo(String key, ImageView imagePreview) {
      this.imagePreview = imagePreview;
      this.key = key;
    }

    private String getKey() {
      return key;
    }

    private ImageView getImagePreview() {
      return imagePreview;
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  public void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }
}
