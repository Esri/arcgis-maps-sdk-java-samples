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
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.symbology.MultilayerPointSymbol;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.symbology.SymbolStyle;
import com.esri.arcgisruntime.symbology.SymbolStyleSearchParameters;
import com.esri.arcgisruntime.symbology.SymbolStyleSearchResult;

public class ReadSymbolsFromMobileStyleFileController {

  @FXML
  private MapView mapView = new MapView();
  @FXML
  private ListView hatSelectionListView = new ListView<SymbolLayerInfo>();
  @FXML
  private ListView eyesSelectionListView = new ListView<SymbolLayerInfo>();
  @FXML
  private ListView mouthSelectionListView = new ListView<SymbolLayerInfo>();
  @FXML
  private ListView colorSelectionListView = new ListView<Integer>();
  @FXML
  private Slider sizeSlider = new Slider();
  @FXML
  private Label sizeLabel = new Label();
  @FXML
  private HBox symbolPreviewHBox = new HBox();

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

    // load the available symbols from the style file
    loadSymbolsFromStyleFile();

    // create a cell factory to show the available symbols in the respective list view
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

    // create a listener that builds the composite symbol when an item from the list view is selected
    ChangeListener<Object> changeListener = (ObservableValue<? extends Object> observable, Object oldValue, Object newValue) -> buildCompositeSymbol();

    // create an array of the ListView objects for choosing the symbols, and iterate over it
    ListView[] listViews = new ListView[]{hatSelectionListView, eyesSelectionListView, mouthSelectionListView};
    for (ListView listView : listViews) {
      // add the cell factory to show the symbol within the list view
      listView.setCellFactory(c -> new SymbolLayerInfoListCell());
      // add the change listener to rebuild the preview when a selection is made
      listView.getSelectionModel().selectedItemProperty().addListener(changeListener);
      // add an empty SymbolLayerInfo to allow selecting 'nothing'
      listView.getItems().add(new SymbolLayerInfo("", null));
    }

    // add colors to the color selection list view. We require the 0xAARRGGBB format to color the symbols
    colorSelectionListView.getItems().addAll(0xFFFFFF00, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF);
    // make the first item in the list view selected by default
    colorSelectionListView.getSelectionModel().select(0);
    // add the change listener to the color selection list view, making a selection update the preview image
    colorSelectionListView.getSelectionModel().selectedItemProperty().addListener(changeListener);
    // create a cell factory to show the colors in the list view
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
          // convert the 0xAARRGGBB format to a Color object and apply it to the rectangle fill
          rectangle.setFill(ColorUtil.argbToColor(item));
          setGraphic(rectangle);
        }
      }
    }
    // add the cell factory to the color selection list view
    colorSelectionListView.setCellFactory(c -> new ColorListCell());

    // initiate the size label to show the default value
    updateSizeLabel();
    // add a listener to the slider to update the preview when the size is changed
    sizeSlider.valueProperty().addListener(o -> {
      // update size label
      updateSizeLabel();
      // update the preview
      buildCompositeSymbol();
    });

    // listen to mouse clicks to add the desired multi layer symbol
    mapView.setOnMouseClicked(e -> {
      if (e.getButton() == MouseButton.PRIMARY && e.isStillSincePress()) {
        // convert clicked point to a map point
        Point mapPoint = mapView.screenToLocation(new Point2D(e.getX(), e.getY()));

        // create a new graphic with the point and symbol
        Graphic graphic = new Graphic(mapPoint, faceSymbol);
        graphicsOverlay.getGraphics().add(graphic);
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

              // create lists to contain the available symbol layers for each category of symbol and add an empty entry as default
              ArrayList<SymbolLayerInfo> hatSymbolInfos = new ArrayList<>(Collections.singletonList(new SymbolLayerInfo("", null)));
              ArrayList<SymbolLayerInfo> eyeSymbolInfos = new ArrayList<>(Collections.singletonList(new SymbolLayerInfo("", null)));
              ArrayList<SymbolLayerInfo> mouthSymbolInfos = new ArrayList<>(Collections.singletonList(new SymbolLayerInfo("", null)));

              // loop through the results and add symbols infos into the list according to category
              List<SymbolStyleSearchResult> symbolStyleSearchResults = symbolStyleSearchResultFuture.get();
              for (SymbolStyleSearchResult symbolStyleSearchResult : symbolStyleSearchResults) {

                // get the symbol for this result
                MultilayerPointSymbol multilayerPointSymbol = (MultilayerPointSymbol) symbolStyleSearchResult.getSymbol();

                // create a swatch image for the symbol (to be used for the preview)
                ListenableFuture<Image> imageListenableFuture = multilayerPointSymbol.createSwatchAsync(0x00000000, 1);
                imageListenableFuture.addDoneListener(() -> {
                  try {
                    // get the resulting image from the future and convert to an image view
                    Image image = imageListenableFuture.get();
                    ImageView imagePreview = new ImageView(image);

                    // create a symbol layer info object to represent the found symbol in the list
                    SymbolLayerInfo symbolLayerInfo = new SymbolLayerInfo(symbolStyleSearchResult.getKey(), imagePreview);

                    // add the symbol layer info object to the correct list for its category
                    switch (symbolStyleSearchResult.getCategory().toLowerCase()) {
                      case "hat":
                        hatSymbolInfos.add(symbolLayerInfo);
                        // add the preview of the symbol to the preview container
                        hatSelectionListView.getItems().add(symbolLayerInfo);
                        break;
                      case "eyes":
                        eyeSymbolInfos.add(symbolLayerInfo);
                        // add the preview of the symbol to the preview container
                        eyesSelectionListView.getItems().add(symbolLayerInfo);
                        break;
                      case "mouth":
                        mouthSymbolInfos.add(symbolLayerInfo);
                        // add the preview of the symbol to the preview container
                        mouthSelectionListView.getItems().add(symbolLayerInfo);
                        break;
                    }

                  } catch (InterruptedException | ExecutionException e) {
                    new Alert(Alert.AlertType.ERROR, "Error creating preview image for symbol in mobile style file" + e.getMessage()).show();
                  }
                });
              }

              // create the symbol to populate the preview
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

  /**
   * Gets the keys of the selected symbol components, and constructs a multilayer point symbol
   */
  @FXML
  private void buildCompositeSymbol() {

    // retrieve the requested key for the requested hat symbol
    SymbolLayerInfo requestedHat = (SymbolLayerInfo) hatSelectionListView.getSelectionModel().getSelectedItem();
    String hatKey = requestedHat != null ? requestedHat.getKey() : "";

    // retrieve the requested key for the requested eyes symbol
    SymbolLayerInfo requestedEyes = (SymbolLayerInfo) eyesSelectionListView.getSelectionModel().getSelectedItem();
    String eyesKey = requestedEyes != null ? requestedEyes.getKey() : "";

    // retrieve the requested key for the requested mouth symbol
    SymbolLayerInfo requestedMouth = (SymbolLayerInfo) mouthSelectionListView.getSelectionModel().getSelectedItem();
    String mouthKey = requestedMouth != null ? requestedMouth.getKey() : "";

    List<String> symbolKeys = Arrays.asList("Face1", eyesKey, mouthKey, hatKey);

    ListenableFuture<Symbol> symbolFuture = emojiStyle.getSymbolAsync(symbolKeys);
    symbolFuture.addDoneListener(() -> {
      try {
        faceSymbol = (MultilayerPointSymbol) symbolFuture.get();
        if (faceSymbol == null) {
          return;
        }

        // loop through all the symbol layers and lock the color
        faceSymbol.getSymbolLayers().forEach(symbolLayer -> symbolLayer.setColorLocked(true));

        // unlock the color of the base layer. Changing the symbol layer will now only change this layer's color
        faceSymbol.getSymbolLayers().get(0).setColorLocked(false);

        // set the color of the symbol
        faceSymbol.setColor((Integer) colorSelectionListView.getSelectionModel().getSelectedItem());

        // set the size of the symbol
        faceSymbol.setSize((float) sizeSlider.getValue());

        // update the symbol preview
        updateSymbolPreview(faceSymbol);

      } catch (ExecutionException | InterruptedException e) {
        new Alert(Alert.AlertType.ERROR, "Error creating symbol with the provided symbol keys" + e.getMessage()).show();
      }
    });
  }

  /**
   * Creates an ImageView object from a provided symbol and displays it in the preview area
   *
   * @param multilayerPointSymbol the symbol used to create the image view
   */
  private void updateSymbolPreview(MultilayerPointSymbol multilayerPointSymbol) {
    // remove the previously displayed image view
    symbolPreviewHBox.getChildren().clear();

    // create an image and image view from the symbol
    ListenableFuture<Image> symbolImageFuture = multilayerPointSymbol.createSwatchAsync(0x00000000, 1);
    try {
      Image symbolImage = symbolImageFuture.get();
      ImageView symbolImageView = new ImageView(symbolImage);
      // display the image view in the preview area
      symbolPreviewHBox.getChildren().add(symbolImageView);

    } catch (InterruptedException | ExecutionException e) {
      new Alert(Alert.AlertType.ERROR, "Error creating preview ImageView from provided MultilayerPointSymbol" + e.getMessage()).show();
    }
  }

  /**
   * Clears all the graphics from the graphics overlay.
   */
  @FXML
  private void resetView() {
    mapView.getGraphicsOverlays().get(0).getGraphics().clear();
  }

  /**
   * Gets the value of the size slider and updates the size label accordingly.
   */
  private void updateSizeLabel() {
    // get the slider value and convert to a string
    String sizeValue = Integer.toString((int) sizeSlider.getValue());
    // display the value in the label
    sizeLabel.setText(sizeValue + " px");
  }

  /**
   * a helper class used to store the information about a symbol layer
   */
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
