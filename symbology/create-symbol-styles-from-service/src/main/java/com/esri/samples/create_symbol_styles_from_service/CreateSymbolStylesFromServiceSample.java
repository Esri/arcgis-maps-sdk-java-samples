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
import java.util.Collections;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
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
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.symbology.SymbolStyle;
import com.esri.arcgisruntime.symbology.SymbolStyleSearchResult;
import com.esri.arcgisruntime.symbology.UniqueValueRenderer;

public class CreateSymbolStylesFromServiceSample extends Application {

  private MapView mapView;
  private UniqueValueRenderer.UniqueValue school;
  private Symbol schoolSymbol;
  private Symbol parkSymbol;
  private Symbol cityHallSymbol;
  private Symbol beachSymbol;
  Symbol airportSymbol;
  Symbol airport;
  Symbol po;


  List<SymbolStyleSearchResult> symbolSearchResults;

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

      // create a map view and set its map
      mapView = new MapView();
      mapView.setMap(map);

      // create a feature layer from a service
      FeatureLayer featureLayer = new FeatureLayer(new ServiceFeatureTable(
        "http://services.arcgis.com/V6ZHFr6zdgNZuVG0/arcgis/rest/services/LA_County_Points_of_Interest/FeatureServer/0"));

      // create a unique value renderer and add the relevant field from the feature layer to match symbols with
      UniqueValueRenderer uniqueValueRenderer = new UniqueValueRenderer();
//      uniqueValueRenderer.setDefaultSymbol(new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, 0xFFFF0000, 10));
//      ArrayList<String> fields = new ArrayList<>();
//      fields.add("cat1");
//      fields.add("cat2");
//      fields.add("cat2");
//      fields.add("cat2");
//      uniqueValueRenderer.getFieldNames().addAll(fields);
      uniqueValueRenderer.getFieldNames().add("cat2");
//      uniqueValueRenderer.getFieldNames().add("cat1");

      // create a simple renderer
      SimpleRenderer simpleRenderer = new SimpleRenderer();

      // set the renderer on the feature layer
      featureLayer.setRenderer(uniqueValueRenderer);
//      featureLayer.setRenderer(simpleRenderer);

      // create a symbol style from a portal
      SymbolStyle symbolStyle = new SymbolStyle("Esri2DPointSymbolsStyle", null);

//      // display an error if the symbol style does not load
//      symbolStyle.loadAsync();
//      symbolStyle.addDoneLoadingListener(() -> {
//          if (symbolStyle.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
//            new Alert(Alert.AlertType.ERROR, "Error: could not load symbol style. Details: \n"
//              + symbolStyle.getLoadError().getMessage()).show();
//          }
//        });

      ///////
      // below works for manually setting up each symbol, adding multiple categories
      ///////

      // create the symbols

//      List<String> schoolSearch = new ArrayList<>();
//      schoolSearch.add("school");
//      ListenableFuture<Symbol> schoolSearchResult = symbolStyle.getSymbolAsync(schoolSearch);
//      schoolSearchResult.addDoneListener(() -> {
//        try {
//          schoolSymbol = schoolSearchResult.get();
//          List<String> schoolValue = new ArrayList<>();
//          schoolValue.add("Colleges and Universities");
//          UniqueValueRenderer.UniqueValue educationOverall = new UniqueValueRenderer.UniqueValue("", "Colleges and Universities", schoolSymbol, null);
//          educationOverall.getValues().addAll(schoolValue);
////          educationOverall.getValues().add("Government");
//          uniqueValueRenderer.getUniqueValues().add(educationOverall);
//          System.out.println(educationOverall.getValues().size());
//
//        } catch (Exception e) {
//          // on any error, display the stack trace
//          e.printStackTrace();
//        }
//      });

      ArrayList<String> listOfKeys = new ArrayList<>();
//      listOfKeys.add("post-office");
      listOfKeys.add("airport");
      listOfKeys.add("beach");
      listOfKeys.add("school");

//      List<String> poSearch = new ArrayList<>();
//      poSearch.add("post-office");
//      ListenableFuture<Symbol> poSearchResult = symbolStyle.getSymbolAsync(poSearch);
//      poSearchResult.addDoneListener(() -> {
//        try {
//          po = poSearchResult.get();
//          List<Object> poCategoryValue = new ArrayList<>();
//          poCategoryValue.add("Postal");
//          UniqueValueRenderer.UniqueValue uniqueValue = new UniqueValueRenderer.UniqueValue("", "postal", po, poCategoryValue);
//          uniqueValueRenderer.getUniqueValues().add(uniqueValue);
////          System.out.println("index 0 unique values: " + uniqueValueRenderer.getUniqueValues().get(0).getValues().get(0));
////          System.out.println("renderer field 0: " + uniqueValueRenderer.getFieldNames().get(0));
////          System.out.println("renderer field 1: " + uniqueValueRenderer.getFieldNames().get(1));
//        } catch (Exception e) {
//          // on any error, display the stack trace
//          e.printStackTrace();
//        }
//      });
//
//      List<String> airportSearch = new ArrayList<>();
//      poSearch.add("airport");
//      ListenableFuture<Symbol> airportSearchResult = symbolStyle.getSymbolAsync(airportSearch);
//      airportSearchResult.addDoneListener(() -> {
//        try {
//          airport = airportSearchResult.get();
//          List<Object> airportCategoryValue = new ArrayList<>();
//          airportCategoryValue.add("Airports");
//          UniqueValueRenderer.UniqueValue airportUniqueVal = new UniqueValueRenderer.UniqueValue("", "airports", airport, airportCategoryValue);
//          uniqueValueRenderer.getUniqueValues().add(airportUniqueVal);
////          System.out.println("index 1 unique values: " + uniqueValueRenderer.getUniqueValues().get(0).getValues().get(1));
//        } catch (Exception e) {
//          // on any error, display the stack trace
//          e.printStackTrace();
//        }
//      });

      for (String key : listOfKeys) {
        List<String> keySearch = new ArrayList<>();
        keySearch.add(key);
        ListenableFuture<Symbol> searchResult = symbolStyle.getSymbolAsync(keySearch);
        searchResult.addDoneListener(() -> {
          try {
            Symbol symbol = searchResult.get();
            List<String> categories = figureOutCategories(key);
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
          // set viewpoint to the location of feature layer's features
//          mapView.setViewpointCenterAsync(featureLayer.getFullExtent().getCenter(), 10000000);
          // set the map views's viewpoint centered on Los Angeles, California and scaled
          mapView.setViewpoint(new Viewpoint(new Point(-13185535.98, 4037766.28,
            SpatialReferences.getWebMercator()), 7000));
//          featureLayer.setMinScale(400000);
        } else new Alert(Alert.AlertType.ERROR, "Feature Layer Failed to Load!").show();
      });

      // add the map view to stack pane
      stackPane.getChildren().addAll(mapView);
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  private List<String> figureOutCategories(String key) {

    List<String> categories = new ArrayList<>();

    switch (key) {
      case "beach":
        categories.add("Beaches and Marinas");
        break;
      case "school":
        categories.add("Colleges and Universities");
        categories.add("Public High Schools");
      case "airport":
        categories.add("Airports");
        break;
      case "post-office":
        categories.add("Postal");
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