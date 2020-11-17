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
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.internal.jni.CoreSymbolStyle;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.symbology.SymbolStyle;
import com.esri.arcgisruntime.symbology.SymbolStyleSearchParameters;
import com.esri.arcgisruntime.symbology.SymbolStyleSearchResult;
import com.esri.arcgisruntime.symbology.UniqueValueRenderer;

public class CreateSymbolStylesFromServiceSample extends Application {

  private MapView mapView;
//  UniqueValueRenderer.UniqueValue postal;
    UniqueValueRenderer.UniqueValue school;
  Symbol schoolSymbol;
//  Symbol defaultSymbol;

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

      // create a feature layer from a feature service
      FeatureLayer featureLayer = new FeatureLayer(new ServiceFeatureTable("http://services.arcgis.com/V6ZHFr6zdgNZuVG0/arcgis/rest/services/LA_County_Points_of_Interest/FeatureServer/0"));

//      UniqueValueRenderer uniqueRenderer = new UniqueValueRenderer();

      SymbolStyle symbolStyle = new SymbolStyle("Esri2DPointSymbolsStyle", null);

      SymbolStyleSearchParameters searchParams = new SymbolStyleSearchParameters();
      searchParams.getKeys().add("school");
//      searchParams.getKeys().add("post-office");
//      searchParams.getKeys().add("hexagon-3");

//      List<String> keys = new ArrayList<>();
//      keys.add("hexagon-3");

      UniqueValueRenderer renderer = new UniqueValueRenderer();
            Symbol dftSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFFFF0000, 12);


      ListenableFuture<List<SymbolStyleSearchResult>> searchSymbolResult = symbolStyle.searchSymbolsAsync(searchParams);

      searchSymbolResult.addDoneListener(() -> {
        try {
          ListenableFuture<Symbol> symbolResultOne = searchSymbolResult.get().get(0).getSymbolAsync();
          symbolResultOne.addDoneListener(() -> {
            try{
              System.out.println("inside the done listener");
              schoolSymbol = symbolResultOne.get();
              school = new UniqueValueRenderer.UniqueValue("", "Education", schoolSymbol, Collections.singletonList("Education"));
              featureLayer.setRenderer(new UniqueValueRenderer(Collections.singletonList("cat1"), Arrays.asList(school), "", dftSymbol));
//              featureLayer.setRenderer(renderer);
            } catch (Exception e) {
              // on any error, display the stack trace
              e.printStackTrace();
            }
          });
//          ListenableFuture<Symbol> symbolResultTwo = searchSymbolResult.get().get(1).getSymbolAsync();
//          symbolResultOne.addDoneListener(() -> {
//            try{
//              postal = new UniqueValueRenderer.UniqueValue("N/A", "Postal", symbolResultTwo.get(), Collections.singletonList(50));
//            } catch (Exception e) {
//              // on any error, display the stack trace
//              e.printStackTrace();
//            }
//          });
//          ListenableFuture<Symbol> symbolResultThree = searchSymbolResult.get().get(1).getSymbolAsync();
//          symbolResultOne.addDoneListener(() -> {
//            try{
//              defaultSymbol = symbolResultThree.get();
//            } catch (Exception e) {
//              // on any error, display the stack trace
//              e.printStackTrace();
//            }
//          });
        } catch (Exception e) {
          // on any error, display the stack trace
          e.printStackTrace();
        }
      });

//      ArrayList<String> fieldNames = new ArrayList<>();
//      fieldNames.add("category");
//
//      ArrayList<UniqueValueRenderer.UniqueValue> uniqueValues = new ArrayList<>();
//      uniqueValues.add(school);
//
//      Symbol dftSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFFFF0000, 12);
//
//      SimpleMarkerSymbol defaultSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFFFF0000, 12);
//
//      UniqueValueRenderer renderer = new UniqueValueRenderer(fieldNames, uniqueValues, "other", dftSymbol);


      //      // create and apply a renderer for the electric distribution lines feature layer
//      UniqueValueRenderer.UniqueValue mediumVoltageValue = new UniqueValueRenderer.UniqueValue("N/A", "Medium Voltage",
//        new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, ColorUtil.colorToArgb(Color.DARKCYAN), 3),
//        Collections.singletonList(5));
//      UniqueValueRenderer.UniqueValue lowVoltageValue = new UniqueValueRenderer.UniqueValue("N/A", "Low Voltage",
//        new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, ColorUtil.colorToArgb(Color.DARKCYAN), 3),
//        Collections.singletonList(3));
//      distributionLineLayer.setRenderer(new UniqueValueRenderer(Collections.singletonList("ASSETGROUP"),
//        Arrays.asList(mediumVoltageValue, lowVoltageValue), "", new SimpleLineSymbol()));

      // working with simple renderer:

//      SimpleRenderer renderer = new SimpleRenderer();
//
//      SymbolStyle symbolStyle = new SymbolStyle("Esri2DPointSymbolsStyle", null);
//
//      SymbolStyleSearchParameters searchParams = new SymbolStyleSearchParameters();
//      searchParams.getKeys().add("hexagon-3");
//
////      List<String> keys = new ArrayList<>();
////      keys.add("hexagon-3");
//
//      ListenableFuture<List<SymbolStyleSearchResult>> searchSymbolResult = symbolStyle.searchSymbolsAsync(searchParams);
//
//      searchSymbolResult.addDoneListener(() -> {
//        try {
//          ListenableFuture<Symbol> symbolResult = searchSymbolResult.get().get(0).getSymbolAsync();
//          symbolResult.addDoneListener(() -> {
//            try{
//              renderer.setSymbol(symbolResult.get());
//              featureLayer.setRenderer(renderer);
//            } catch (Exception e) {
//              // on any error, display the stack trace
//              e.printStackTrace();
//            }
//          });
//        } catch (Exception e) {
//          // on any error, display the stack trace
//          e.printStackTrace();
//        }
//      });
//
////      ListenableFuture<Symbol> symbolResult = symbolStyle.getSymbolAsync(keys);
////
////      symbolResult.addDoneListener(() -> {
////        try {
////          renderer.setSymbol(symbolResult.get());
////          featureLayer.setRenderer(renderer);
////        } catch (Exception e) {
////          // on any error, display the stack trace
////          e.printStackTrace();
////        }
////      });

//      UniqueValueRenderer uniqueValueRenderer = new UniqueValueRenderer();
//
//      SymbolStyle symbolStyle = new SymbolStyle("hexagon-3", null);
//      SimpleFillSymbol defaultFillSymbol = new SimpleFillSymbol();
//      defaultFillSymbol.setStyle(symbolStyle);
//
//      uniqueValueRenderer.setDefaultSymbol(defaultFillSymbol);
//      uniqueValueRenderer.setDefaultLabel("Other");
//
//      featureLayer.setRenderer(uniqueValueRenderer);

      // unique value renderer sample stuff:

//      // override the feature layer renderer with a new unique value renderer
//      UniqueValueRenderer uniqueValueRenderer = new UniqueValueRenderer();
//      // field name is a key, in a key/value pair, of a feature's attributes
//      // can be a list but only looking for one in this case
//      uniqueValueRenderer.getFieldNames().add("STATE_ABBR");
//
//      // create the symbols to be used in the renderer
//      SimpleFillSymbol defaultFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.NULL, 0x00000000,
//        new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, GRAY, 2));
//      SimpleFillSymbol californiaFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, RED,
//        new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, RED, 2));
//      SimpleFillSymbol arizonaFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, GREEN,
//        new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, GREEN, 2));
//      SimpleFillSymbol nevadaFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, BLUE, new SimpleLineSymbol(
//        SimpleLineSymbol.Style.SOLID, BLUE, 2));
//
//      // set the default symbol
//      uniqueValueRenderer.setDefaultSymbol(defaultFillSymbol);
//      uniqueValueRenderer.setDefaultLabel("Other");
//
//      // set value for California, Arizona, and Nevada
//      List<Object> californiaValue = new ArrayList<>();
//      californiaValue.add("CA");
//      uniqueValueRenderer.getUniqueValues().add(new UniqueValue("State of California", "California",
//        californiaFillSymbol, californiaValue));
//
//      List<Object> arizonaValue = new ArrayList<>();
//      arizonaValue.add("AZ");
//      uniqueValueRenderer.getUniqueValues().add(new UniqueValue("State of Arizona", "Arizona", arizonaFillSymbol,
//        arizonaValue));
//
//      List<Object> nevadaValue = new ArrayList<>();
//      nevadaValue.add("NV");
//      uniqueValueRenderer.getUniqueValues().add(new UniqueValue("State of Nevada", "Nevada", nevadaFillSymbol,
//        nevadaValue));
//
//      // set the renderer on the feature layer
//      featureLayer.setRenderer(uniqueValueRenderer);







      // add the feature layer to the map
      map.getOperationalLayers().add(featureLayer);

      featureLayer.addDoneLoadingListener(() -> {
        if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
          // set viewpoint to the location of feature layer's features
//          mapView.setViewpointCenterAsync(featureLayer.getFullExtent().getCenter(), 10000000);
          // set the map views's viewpoint centered on Los Angeles, California and scaled
          mapView.setViewpoint(new Viewpoint(new Point(-13185535.98, 4037766.28,
            SpatialReferences.getWebMercator()), 7000));
        } else new Alert(Alert.AlertType.ERROR, "Feature Layer Failed to Load!").show();
      });

      // adding a simple point with a simple marker symbol to the map:

//      // create new graphics overlay and add it to the map view
//      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
//      mapView.getGraphicsOverlays().add(graphicsOverlay);
//
//      // create a red (0xFFFF0000) simple marker symbol
//      SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFFFF0000, 12);
//      Point point = new Point(-13185535.98, 4037766.28, SpatialReferences.getWebMercator());
//
//      // create a new graphic with a our point and symbol
//      Graphic graphic = new Graphic(point, symbol);
//      graphicsOverlay.getGraphics().add(graphic);

      // add the map view to stack pane
      stackPane.getChildren().addAll(mapView);
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
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
