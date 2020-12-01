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
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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

      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.6)"), CornerRadii.EMPTY,
        Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(250, 80);
      Label heading = new Label();
      heading.setText("Symbol Style: " + symbolStyle.getStyleName());
      heading.setTextFill(Color.WHITE);
      controlsVBox.getChildren().add(heading);

      ArrayList<String> listOfKeys = new ArrayList<>();
      listOfKeys.add("post-office");
      listOfKeys.add("atm");
      listOfKeys.add("place-of-worship");
      listOfKeys.add("police-station");
      listOfKeys.add("school");
      listOfKeys.add("hospital");
      listOfKeys.add("beach");
      listOfKeys.add("trail");
      listOfKeys.add("city-hall");
      listOfKeys.add("park");
      listOfKeys.add("library");
      listOfKeys.add("campground");

      for (String key : listOfKeys) {
        List<String> keySearch = new ArrayList<>();
        keySearch.add(key);
        ListenableFuture<Symbol> searchResult = symbolStyle.getSymbolAsync(keySearch);
        searchResult.addDoneListener(() -> {
          try {
            Symbol symbol = searchResult.get();
            List<String> categories = mapSymbolKeyToCategories(key);
            Label label = new Label();
            label.setText(key);
            label.setTextFill(Color.WHITE);
            controlsVBox.getChildren().add(label);
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
          mapView.setViewpoint(new Viewpoint(new Point(-13185668.186639601, 4066176.418652561,
            SpatialReferences.getWebMercator()), 7000));
        } else new Alert(Alert.AlertType.ERROR, "Feature Layer Failed to Load!\n" +
          featureLayer.getLoadError().getCause().getMessage()).show();
      });

      mapView.addMapScaleChangedListener(mapScaleChangedEvent -> {
        featureLayer.setScaleSymbols(!(mapView.getMapScale() < 80000));
      });

      // add the map view to stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  private List<String> mapSymbolKeyToCategories(String key) {

    List<String> categories = new ArrayList<>();

    switch (key) {
      case "beach":
        categories.add("Beaches and Marinas");
        break;
      case "trail":
        categories.add("Trails");
        break;
      case "park":
        categories.add("Parks and Gardens");
        break;
      case "post-office":
        categories.add("DHL Locations");
        categories.add("Federal Express Locations");
        break;
      case "school":
        categories.add("Public High Schools");
        categories.add("Public Elementary Schools");
        categories.add("Private and Charter Schools");
        break;
      case "city-hall":
        categories.add("City Halls");
        categories.add("Government Offices");
        break;
      case "atm":
        categories.add("Banking and Finance");
        break;
      case "place-of-worship":
        categories.add("Churches");
        break;
      case "police-station":
        categories.add("Sheriff and Police Stations");
        break;
      case "hospital":
        categories.add("Hospitals and Medical Centers");
        categories.add("Health Screening and Testing");
        categories.add("Health Centers");
        categories.add("Mental Health Centers");
        break;
      case "library":
        categories.add("Libraries");
        break;
      case "campground":
        categories.add("Campgrounds");
        break;
    }
    return categories;
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

//      //////////
//      // simple renderer working for one specific key
//      ///////////
//
//      //
//      List<String> searchKey = new ArrayList<>();
//      searchKey.add("push-pin-1");
//
//      ListenableFuture<Symbol> searchResult = symbolStyle.getSymbolAsync(searchKey);
//      searchResult.addDoneListener(() -> {
//        try {
//          simpleRenderer.setSymbol(searchResult.get());
//        } catch (Exception e) {
//          // on any error, display the stack trace
//          e.printStackTrace();
//        }
//      });

// System.out.println(symbolStyle.getStyleName());
// this prints out Esri2DPointSymbolsStyle

// below prints out what information you can get back after creating the style
//      ListenableFuture<SymbolStyleSearchParameters> symbolStyleSearchParametersListenableFuture = symbolStyle.getDefaultSearchParametersAsync();
//      symbolStyleSearchParametersListenableFuture.addDoneListener(() -> {
//        try{
//          List<String> keys = symbolStyleSearchParametersListenableFuture.get().getKeys();
//          List<String> names = symbolStyleSearchParametersListenableFuture.get().getNames();
//          List<String> categories = symbolStyleSearchParametersListenableFuture.get().getCategories();
//
//          for (String key : keys) {
//            System.out.println("key: " + key);
//          }
//
//          for (String name : names) {
//            System.out.println("name: " + name);
//          }
//
//          for (String category : categories) {
//            System.out.println("category: " + category);
//          }
//
//        } catch (Exception e) {
//          // on any error, display the stack trace
//          e.printStackTrace();
//        }
//      });