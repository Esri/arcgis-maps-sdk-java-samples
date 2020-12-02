/*
 * Copyright 2020 Esri.
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

package com.esri.samples.create_symbol_styles_from_service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.symbology.SymbolStyle;
import com.esri.arcgisruntime.symbology.UniqueValueRenderer;

public class CreateSymbolStylesFromServiceSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Create Symbol Styles From Service Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map with the imagery basemap
      ArcGISMap map = new ArcGISMap(Basemap.createLightGrayCanvasVector());
      map.setReferenceScale(100000);

      // create a map view and set its map
      mapView = new MapView();
      mapView.setMap(map);

      // create a feature layer from a service
      FeatureLayer featureLayer = new FeatureLayer(new ServiceFeatureTable(
        "http://services.arcgis.com/V6ZHFr6zdgNZuVG0/arcgis/rest/services/LA_County_Points_of_Interest/FeatureServer/0"));

      // create a unique value renderer and add the relevant field from the feature layer to match symbols with
      UniqueValueRenderer uniqueValueRenderer = new UniqueValueRenderer();
      uniqueValueRenderer.getFieldNames().add("cat2");

      // set the renderer on the feature layer
      featureLayer.setRenderer(uniqueValueRenderer);

      // create a symbol style from a portal
      SymbolStyle symbolStyle = new SymbolStyle("Esri2DPointSymbolsStyle", null);

      // display an error if the symbol style does not load
      symbolStyle.loadAsync();
      symbolStyle.addDoneLoadingListener(() -> {
        if (symbolStyle.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
          new Alert(Alert.AlertType.ERROR, "Error: could not load symbol style. Details: \n"
            + symbolStyle.getLoadError().getMessage()).show();
        }
      });

      // create a legend
      GridPane gridPane = new GridPane();
      gridPane.getColumnConstraints().addAll(Arrays.asList(new ColumnConstraints(70), new ColumnConstraints(120)));
      Label legendTitle = new Label("Style: " + symbolStyle.getStyleName());
      legendTitle.setStyle("-fx-font-weight: bold");
      gridPane.add(legendTitle, 0, 0,2,1);
      Label symbolTitle = new Label("Symbol");
      symbolTitle.setStyle("-fx-font-weight: bold");
      Label nameTitle = new Label("Name");
      nameTitle.setStyle("-fx-font-weight: bold");
      gridPane.add(symbolTitle, 0, 1);
      gridPane.add(nameTitle, 1, 1);
      gridPane.setMaxWidth(175);
      gridPane.setMaxHeight(570);
      gridPane.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(255,255,255, 0.9)"), CornerRadii.EMPTY,
        Insets.EMPTY)));
      gridPane.setPadding(new Insets(10));
      gridPane.setVgap(12);

      ArrayList<String> listOfKeys = new ArrayList<>();
      listOfKeys.addAll(Arrays.asList("atm", "beach", "campground", "city-hall", "hospital", "library", "park",
        "place-of-worship", "police-station", "post-office", "school", "trail"));

      for (String key : listOfKeys) {
        List<String> keySearch = new ArrayList<>();
        keySearch.add(key);
        ListenableFuture<Symbol> searchResult = symbolStyle.getSymbolAsync(keySearch);
        searchResult.addDoneListener(() -> {
          try {
            Symbol symbol = searchResult.get();
            ImageView imageView = addSymbolToImageView(symbol);
            Label gridPaneLabel = new Label(key);
            gridPane.add(imageView, 0, listOfKeys.indexOf(key)+2);
            gridPane.add(gridPaneLabel, 1, listOfKeys.indexOf(key)+2);
            List<String> categories = mapSymbolKeyToCategories(key);
            for (String category : categories) {
              List<Object> categoryValue = new ArrayList<>();
              categoryValue.add(category);
              UniqueValueRenderer.UniqueValue uniqueValue = new UniqueValueRenderer.UniqueValue("", key, symbol, categoryValue);
              uniqueValueRenderer.getUniqueValues().add(uniqueValue);
            }
          } catch (Exception e) {
            // on any error, display the stack trace
            e.printStackTrace();
          }
        });
      }

      // add the feature layer to the map
      map.getOperationalLayers().add(featureLayer);

      featureLayer.addDoneLoadingListener(() -> {
        if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
          // set the map views's viewpoint centered on Los Angeles, California and scaled
          mapView.setViewpoint(new Viewpoint(new Point(-13184975, 4066890,
            SpatialReferences.getWebMercator()), 7000));
        } else new Alert(Alert.AlertType.ERROR, "Feature Layer Failed to Load!\n" +
          featureLayer.getLoadError().getCause().getMessage()).show();
      });

      mapView.addMapScaleChangedListener(mapScaleChangedEvent -> {
        featureLayer.setScaleSymbols(!(mapView.getMapScale() < 80000));
      });

      // add the map view to stack pane
      stackPane.getChildren().addAll(mapView, gridPane);
      StackPane.setAlignment(gridPane, Pos.TOP_LEFT);
      StackPane.setMargin(gridPane, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Returns a list of categories to be matched to a symbol key.
   *
   * @param key the name of a symbol
   * @return categories a list of categories matched to the provided symbol key
   */
  private List<String> mapSymbolKeyToCategories(String key) {

    List<String> categories = new ArrayList<>();

    switch (key) {
      case "atm":
        categories.add("Banking and Finance");
        break;
      case "beach":
        categories.add("Beaches and Marinas");
        break;
      case "campground":
        categories.add("Campgrounds");
        break;
      case "city-hall":
        categories.addAll(Arrays.asList("City Halls", "Government Offices"));
        break;
      case "hospital":
        categories.addAll(Arrays.asList("Hospitals and Medical Centers", "Health Screening and Testing", "Health Centers",
          "Mental Health Centers"));
        break;
      case "library":
        categories.add("Libraries");
        break;
      case "park":
        categories.add("Parks and Gardens");
        break;
      case "place-of-worship":
        categories.add("Churches");
        break;
      case "police-station":
        categories.add("Sheriff and Police Stations");
        break;
      case "post-office":
        categories.addAll(Arrays.asList("DHL Locations", "Federal Express Locations"));
        break;
      case "school":
        categories.addAll(Arrays.asList("Public High Schools", "Public Elementary Schools", "Private and Charter Schools"));
        break;
      case "trail":
        categories.add("Trails");
        break;
    }
    return categories;
  }

  /**
   * Returns an imageView populated with a symbol.
   *
   * @param symbol the symbol to display in the imageView
   * @return imageView the imageView populated with the symbol
   */
  private ImageView addSymbolToImageView(Symbol symbol) {

    // add the symbol to the image view to display it in the legend
    ImageView imageView = new ImageView();
    ListenableFuture<Image> image = symbol.createSwatchAsync(0x00000000);
    image.addDoneListener(() -> {
      try {
        // display the image in the image view
        imageView.setImage(image.get());
      } catch (InterruptedException | ExecutionException e) {
        new Alert(Alert.AlertType.ERROR, "Error creating preview ImageView from provided symbol").show();
      }
    });
    return imageView;
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    if (mapView != null) {
      mapView.dispose();
    }
  }

  /**
   * Opens and runs application.
   *
   * @param args arguments passed to this application
   */
  public static void main(String[] args) {

    Application.launch(args);
  }

}
