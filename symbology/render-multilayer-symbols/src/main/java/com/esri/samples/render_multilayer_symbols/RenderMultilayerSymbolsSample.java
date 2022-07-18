/*
 * Copyright 2022 Esri.
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

package com.esri.samples.render_multilayer_symbols;

import java.util.List;
import java.util.Objects;

import com.esri.arcgisruntime.symbology.MultilayerSymbol;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PolygonBuilder;
import com.esri.arcgisruntime.geometry.PolylineBuilder;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.symbology.DashGeometricEffect;
import com.esri.arcgisruntime.symbology.HatchFillSymbolLayer;
import com.esri.arcgisruntime.symbology.MultilayerPointSymbol;
import com.esri.arcgisruntime.symbology.MultilayerPolygonSymbol;
import com.esri.arcgisruntime.symbology.MultilayerPolylineSymbol;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbolLayer;
import com.esri.arcgisruntime.symbology.SolidFillSymbolLayer;
import com.esri.arcgisruntime.symbology.SolidStrokeSymbolLayer;
import com.esri.arcgisruntime.symbology.StrokeSymbolLayer;
import com.esri.arcgisruntime.symbology.SymbolAnchor;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.esri.arcgisruntime.symbology.VectorMarkerSymbolElement;
import com.esri.arcgisruntime.symbology.VectorMarkerSymbolLayer;

public class RenderMultilayerSymbolsSample extends Application {

  private GraphicsOverlay graphicsOverlay; // TODO: I've made this a member variable for readability
  private MapView mapView;
  private final double offset = 20; //used to keep a consistent distance between symbols in the same column

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      var stackPane = new StackPane();
      var scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Render Multilayer Symbols Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a map with the light gray basemap style
      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_LIGHT_GRAY);

      // create a map view and set the map to it
      mapView = new MapView();
      mapView.setMap(map);

      // create a graphics overlay and add it to the map view
      graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);
      
      // create picture marker symbols, from URI or embedded resources
      PictureMarkerSymbolLayer pictureMarkerFromUri = new PictureMarkerSymbolLayer((
        "https://sampleserver6.arcgisonline.com/arcgis/rest/services/Recreation/FeatureServer/0/images" +
          "/e82f744ebb069bb35b234b3fea46deae"));
      addGraphicFromPictureMarkerSymbolLayer(pictureMarkerFromUri, 0);
      
      PictureMarkerSymbolLayer pictureMarkerFromImage = new PictureMarkerSymbolLayer(
        new Image(Objects.requireNonNull(getClass().getResourceAsStream("/blue_pin.png")), 
          0, 50, true, true));
      addGraphicFromPictureMarkerSymbolLayer(pictureMarkerFromImage, offset);

      // add graphics with simple vector marker symbol elements (MultilayerPoint Simple Markers on app UI)
      var solidFillSymbolLayer = new SolidFillSymbolLayer(ColorUtil.colorToArgb(Color.RED));
      var multilayerPolygonSymbol = new MultilayerPolygonSymbol(List.of(solidFillSymbolLayer));
      var solidStrokeSymbolLayer = new SolidStrokeSymbolLayer(1, ColorUtil.colorToArgb(Color.RED),
        List.of(new DashGeometricEffect()));
      var multilayerPolylineSymbol = new MultilayerPolylineSymbol(List.of(solidStrokeSymbolLayer));
      
      // define vector element for a diamond, triangle and cross
      var diamondGeometry = Geometry.fromJson("{\"rings\":[[[0.0,2.5],[2.5,0.0],[0.0,-2.5],[-2.5,0.0],[0.0,2.5]]]}");
      var triangleGeometry = Geometry.fromJson("{\"rings\":[[[0.0,5.0],[5,-5.0],[-5,-5.0],[0.0,5.0]]]}");
      var crossGeometry = Geometry.fromJson("{\"paths\":[[[-1,1],[0,0],[1,-1]],[[1,1],[0,0],[-1,-1]]]}");
      
      addGraphicsWithVectorMarkerSymbolElements(multilayerPolygonSymbol, diamondGeometry, 0);
      addGraphicsWithVectorMarkerSymbolElements(multilayerPolygonSymbol, triangleGeometry, 20);
      addGraphicsWithVectorMarkerSymbolElements(multilayerPolylineSymbol, crossGeometry, (int)(2 * offset));
      
      // TODO: see if it's possible to cut down the lines of code as I've demonstrated above for the remaining methods!
      
      // create labels to go above each category of graphic
      addTextGraphics(graphicsOverlay);

      // create line marker symbols
      addLineGraphicsWithMarkerSymbols(graphicsOverlay);

      // create polygon marker symbols
      addPolygonGraphicsWithMarkerSymbols(graphicsOverlay);

      // create the more complex multilayer graphics: a point, polygon, and polyline
      addComplexPoint(graphicsOverlay);
      addComplexPolygon(graphicsOverlay);
      addComplexPolyline(graphicsOverlay);

      // add the map view to the stack pane
      stackPane.getChildren().addAll(mapView);
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Loads a picture marker symbol layer and after it has loaded, creates a new multilayer point symbol from it.
   * A graphic is created from the multilayer point symbol and added to the graphics overlay.
   * 
   * @param pictureMarkerSymbolLayer the picture marker symbol layer to be loaded
   * @param offset the value used to keep a consistent distance between symbols in the same column
   * 
   */
  private void addGraphicFromPictureMarkerSymbolLayer(PictureMarkerSymbolLayer pictureMarkerSymbolLayer, double offset) {
    // wait for the picture marker symbol layer to load and check it has loaded
    pictureMarkerSymbolLayer.addDoneLoadingListener(() -> {
      if (pictureMarkerSymbolLayer.getLoadStatus() == LoadStatus.LOADED) {
        // set the size of the layer and create a new multilayer point symbol from it
        pictureMarkerSymbolLayer.setSize(40);
        var multilayerPointSymbol = new MultilayerPointSymbol(List.of(pictureMarkerSymbolLayer));

        // create location for the symbol
        Point point = new Point(-80, 20 - offset, SpatialReferences.getWgs84());

        // create graphic with the location and symbol and add it to the graphics overlay
        var graphic = new Graphic(point, multilayerPointSymbol);
        graphicsOverlay.getGraphics().add(graphic);
      } else if (pictureMarkerSymbolLayer.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
        new Alert(Alert.AlertType.ERROR, "Picture marker symbol layer failed to load").show();
      }
    });
    // load the picture marker symbol layer
    pictureMarkerSymbolLayer.loadAsync();

  }

  /**
   * Adds a new graphic constructed from a multilayer point symbol.
   * 
   * @param multilayerSymbol the multilayer symbol to construct the vector marker symbol element with
   * @param geometry the input geometry for the vector marker symbol element
   * @param offset the value used to keep a consistent distance between symbols in the same column
   */
  private void addGraphicsWithVectorMarkerSymbolElements(MultilayerSymbol multilayerSymbol, Geometry geometry, int offset) {
    
    // define a vector element and create a new multilayer point symbol from it
    var vectorMarkerSymbolElement = new VectorMarkerSymbolElement(geometry, multilayerSymbol);
    var vectorMarkerSymbolLayer = new VectorMarkerSymbolLayer(List.of(vectorMarkerSymbolElement));
    var multilayerPointSymbol = new MultilayerPointSymbol(List.of(vectorMarkerSymbolLayer));
    
    // create point graphic using the symbol and add it to the graphics overlay
    var graphic = new Graphic(new Point(-150, 20 - offset, SpatialReferences.getWgs84()), multilayerPointSymbol);
    graphicsOverlay.getGraphics().add(graphic);
    
  }

  private void addLineGraphicsWithMarkerSymbols(GraphicsOverlay graphicsOverlay) {
    Graphic lineGraphic;

    // multilayer polyline symbol for different line styles
    MultilayerPolylineSymbol lineSymbol;

    // create a dash effect similar to SimpleLineSymbolStyle.SHORTDASHDOTDOT
    var dashGeometricEffect = new DashGeometricEffect(List.of(4.0, 6.0, 0.5, 6.0, 0.5, 6.0));

    // stroke used by line symbols
    SolidStrokeSymbolLayer strokeLayer = new SolidStrokeSymbolLayer(3.0, ColorUtil.colorToArgb(Color.RED),
      List.of(dashGeometricEffect));
    strokeLayer.setCapStyle(StrokeSymbolLayer.CapStyle.ROUND);

    // create a polyline for the multilayer polyline symbol
    var polylineBuilder = new PolylineBuilder(SpatialReferences.getWgs84());
    polylineBuilder.addPoint(new Point(-30, 20));
    polylineBuilder.addPoint(new Point(30, 20));

    lineSymbol = new MultilayerPolylineSymbol(List.of(strokeLayer));

    // create a polyline graphic with geometry using the symbol created above
    lineGraphic = new Graphic(polylineBuilder.toGeometry(), lineSymbol);
    graphicsOverlay.getGraphics().add(lineGraphic);


    polylineBuilder = new PolylineBuilder(SpatialReferences.getWgs84());
    polylineBuilder.addPoint(new Point(-30, 20 - offset));
    polylineBuilder.addPoint(new Point(30, 20 - offset));

    // create a dash effect similar to SimpleLineSymbolStyle.SHORTDASH
    dashGeometricEffect = new DashGeometricEffect(List.of(4.0, 6.0));
    strokeLayer = new SolidStrokeSymbolLayer(3.0, ColorUtil.colorToArgb(Color.RED), List.of(dashGeometricEffect));

    lineSymbol = new MultilayerPolylineSymbol(List.of(strokeLayer));

    // create a polyline graphic with geometry using the symbol created above
    lineGraphic = new Graphic(polylineBuilder.toGeometry(), lineSymbol);
    graphicsOverlay.getGraphics().add(lineGraphic);


    polylineBuilder = new PolylineBuilder(SpatialReferences.getWgs84());
    polylineBuilder.addPoint(new Point(-30, 20 - 2 * offset));
    polylineBuilder.addPoint(new Point(30, 20 - 2 * offset));

    // create a dash effect similar to SimpleLineSymbolStyle.DASHDOTDOT
    dashGeometricEffect = new DashGeometricEffect(List.of(7.0, 9.0, 0.5, 9.0));
    strokeLayer = new SolidStrokeSymbolLayer(3.0, ColorUtil.colorToArgb(Color.RED), List.of(dashGeometricEffect));
    strokeLayer.getGeometricEffects().add(dashGeometricEffect);

    lineSymbol = new MultilayerPolylineSymbol(List.of(strokeLayer));

    // create a polyline graphic with geometry using the symbol created above
    lineGraphic = new Graphic(polylineBuilder.toGeometry(), lineSymbol);
    graphicsOverlay.getGraphics().add(lineGraphic);
  }

  private void addPolygonGraphicsWithMarkerSymbols(GraphicsOverlay graphicsOverlay) {
    var polygonBuilder = new PolygonBuilder(SpatialReferences.getWgs84());
    polygonBuilder.addPoint(new Point(60, 25));
    polygonBuilder.addPoint(new Point(70, 25));
    polygonBuilder.addPoint(new Point(70, 20));
    polygonBuilder.addPoint(new Point(60, 20));

    // create a stroke symbol layer to be used by hatch patterns
    SolidStrokeSymbolLayer strokeForHatches = new SolidStrokeSymbolLayer(2, ColorUtil.colorToArgb(Color.RED),
      List.of(new DashGeometricEffect()));

    // create a stroke symbol layer to be used as an outline for aforementioned hatch patterns
    SolidStrokeSymbolLayer strokeForOutline = new SolidStrokeSymbolLayer(1, ColorUtil.colorToArgb(Color.BLACK),
      List.of(new DashGeometricEffect()));

    // create a diagonal cross pattern hatch symbol layers for diagonal cross fill style
    HatchFillSymbolLayer diagonalStroke1 =
      new HatchFillSymbolLayer(new MultilayerPolylineSymbol(List.of(strokeForHatches)), 45);
    HatchFillSymbolLayer diagonalStroke2 =
      new HatchFillSymbolLayer(new MultilayerPolylineSymbol(List.of(strokeForHatches)), -45);

    // define separation distance for lines in a hatch pattern
    diagonalStroke1.setSeparation(9);
    diagonalStroke2.setSeparation(9);

    // create a multilayer polygon symbol with symbol layers
    MultilayerPolygonSymbol diagonalCrossPolygonSymbol = new MultilayerPolygonSymbol(
      List.of(diagonalStroke1, diagonalStroke2, strokeForOutline)
    );

    // create a polygon graphic with geometry using the symbol created above, and add it to the graphics overlay
    Graphic diagonalCrossGraphic = new Graphic(polygonBuilder.toGeometry(), diagonalCrossPolygonSymbol);
    graphicsOverlay.getGraphics().add(diagonalCrossGraphic);


    polygonBuilder = new PolygonBuilder(SpatialReferences.getWgs84());
    polygonBuilder.addPoint(new Point(60, 25 - offset));
    polygonBuilder.addPoint(new Point(70, 25 - offset));
    polygonBuilder.addPoint(new Point(70, 20 - offset));
    polygonBuilder.addPoint(new Point(60, 20 - offset));

    // create a forward diagonal pattern hatch symbol layer for forward diagonal fill style
    HatchFillSymbolLayer forwardDiagonal =
      new HatchFillSymbolLayer(new MultilayerPolylineSymbol(List.of(strokeForHatches)), -45);

    // define separation distance for lines in a hatch pattern
    forwardDiagonal.setSeparation(9);

    // create a multilayer polygon symbol with symbol layers
    MultilayerPointSymbol forwardDiagonalPolySymbol = new MultilayerPointSymbol(
      List.of(forwardDiagonal, strokeForOutline)
    );

    // create a polygon graphic with geometry using the symbol created above
    Graphic forwardDiagonalGraphic = new Graphic(polygonBuilder.toGeometry(), forwardDiagonalPolySymbol);
    graphicsOverlay.getGraphics().add(forwardDiagonalGraphic);

    polygonBuilder = new PolygonBuilder(SpatialReferences.getWgs84());
    polygonBuilder.addPoint(new Point(60, 25 - 2 * offset));
    polygonBuilder.addPoint(new Point(70, 25 - 2 * offset));
    polygonBuilder.addPoint(new Point(70, 20 - 2 * offset));
    polygonBuilder.addPoint(new Point(60, 20 - 2 * offset));

    // create a vertical pattern hatch symbol layer for vertical fill style
    HatchFillSymbolLayer vertical = new HatchFillSymbolLayer(new MultilayerPolylineSymbol(List.of(strokeForHatches)),
      90);

    // define separation distance for lines in a hatch pattern
    vertical.setSeparation(9);

    // create a multilayer polygon symbol with symbol layers
    MultilayerPolygonSymbol verticalPolygonSymbol = new MultilayerPolygonSymbol(List.of(vertical, strokeForOutline));

    // create a polygon graphic with geometry using the symbol created above
    Graphic verticalPolygonGraphic = new Graphic(polygonBuilder.toGeometry(), verticalPolygonSymbol);
    graphicsOverlay.getGraphics().add(verticalPolygonGraphic);
  }

  private void addComplexPoint(GraphicsOverlay graphicsOverlay) {

    // create an orange envelope with a blue outline
    SolidFillSymbolLayer orangeFillLayer = new SolidFillSymbolLayer(ColorUtil.colorToArgb(Color.ORANGE));
    SolidStrokeSymbolLayer blueOutline = new SolidStrokeSymbolLayer(2, ColorUtil.colorToArgb(Color.BLUE),
      List.of(new DashGeometricEffect()));
    Envelope orangeSquareGeometry = new Envelope(
      new Point(-0.5, -0.5, SpatialReferences.getWgs84()),
      new Point(0.5, 0.5, SpatialReferences.getWgs84())
    );
    VectorMarkerSymbolElement orangeSquareVectorElement = new VectorMarkerSymbolElement(orangeSquareGeometry,
      new MultilayerPolygonSymbol(List.of(orangeFillLayer, blueOutline)));
    VectorMarkerSymbolLayer orangeSquareVectorMarkerLayer =
      new VectorMarkerSymbolLayer(List.of(orangeSquareVectorElement));
    orangeSquareVectorMarkerLayer.setSize(11);
    orangeSquareVectorMarkerLayer.setAnchor(new SymbolAnchor(-4, -6, SymbolAnchor.PlacementMode.ABSOLUTE));

    // create a black envelope with an orange-red outline
    SolidFillSymbolLayer blackFillLayer = new SolidFillSymbolLayer(ColorUtil.colorToArgb(Color.BLACK));
    SolidStrokeSymbolLayer orangeOutline = new SolidStrokeSymbolLayer(2, ColorUtil.colorToArgb(Color.ORANGERED),
      List.of(new DashGeometricEffect()));
    Envelope blackSquareGeometry = new Envelope(
      new Point(-0.5, -0.5, SpatialReferences.getWgs84()),
      new Point(0.5, 0.5, SpatialReferences.getWgs84())
    );
    VectorMarkerSymbolElement blackSquareVectorElement = new VectorMarkerSymbolElement(blackSquareGeometry,
      new MultilayerPolygonSymbol(List.of(blackFillLayer, orangeOutline)));
    VectorMarkerSymbolLayer blackSquareVectorMarkerLayer =
      new VectorMarkerSymbolLayer(List.of(blackSquareVectorElement));
    blackSquareVectorMarkerLayer.setSize(6);
    blackSquareVectorMarkerLayer.setAnchor(new SymbolAnchor(2, 1, SymbolAnchor.PlacementMode.ABSOLUTE));

    // create a transparent envelope with a purple outline
    SolidFillSymbolLayer transparentFillLayer = new SolidFillSymbolLayer(ColorUtil.colorToArgb(Color.TRANSPARENT));
    SolidStrokeSymbolLayer purpleOutline = new SolidStrokeSymbolLayer(2, ColorUtil.colorToArgb(Color.PURPLE),
      List.of(new DashGeometricEffect()));
    Envelope purpleSquareGeometry = new Envelope(
      new Point(-0.5, -0.5, SpatialReferences.getWgs84()),
      new Point(0.5, 0.5, SpatialReferences.getWgs84())
    );
    VectorMarkerSymbolElement purpleSquareVectorElement = new VectorMarkerSymbolElement(purpleSquareGeometry,
      new MultilayerPolygonSymbol(List.of(transparentFillLayer, purpleOutline)));
    VectorMarkerSymbolLayer purpleSquareVectorMarkerLayer =
      new VectorMarkerSymbolLayer(List.of(purpleSquareVectorElement));
    purpleSquareVectorMarkerLayer.setSize(14);
    purpleSquareVectorMarkerLayer.setAnchor(new SymbolAnchor(4, 2, SymbolAnchor.PlacementMode.ABSOLUTE));

    // create a yellow hexagon with a black outline
    Geometry hexagonElementGeometry = Geometry.fromJson(
      "{\"rings\":[[[-2.89,5.0],[2.89,5.0],[5.77,0.0],[2.89,-5.0]," + "[-2.89,-5.0],[-5.77,0.0],[-2.89,5.0]]]}");
    SolidFillSymbolLayer yellowFillLayer = new SolidFillSymbolLayer(ColorUtil.colorToArgb(Color.YELLOW));
    SolidStrokeSymbolLayer blackOutline = new SolidStrokeSymbolLayer(2, ColorUtil.colorToArgb(Color.BLACK),
      List.of(new DashGeometricEffect()));
    VectorMarkerSymbolElement hexagonVectorElement = new VectorMarkerSymbolElement(hexagonElementGeometry,
      new MultilayerPolylineSymbol(List.of(yellowFillLayer, blackOutline)));
    VectorMarkerSymbolLayer hexagonVectorMarkerLayer = new VectorMarkerSymbolLayer(List.of(hexagonVectorElement));
    hexagonVectorMarkerLayer.setSize(35);

    // create the multilayer point symbol
    var multilayerPointSymbol = new MultilayerPointSymbol(
      List.of(hexagonVectorMarkerLayer,
        orangeSquareVectorMarkerLayer,
        blackSquareVectorMarkerLayer,
        purpleSquareVectorMarkerLayer));

    // create the multilayer point graphic using the symbols created above
    Graphic complexPointGraphic = new Graphic(new Point(130, 20, SpatialReferences.getWgs84()), multilayerPointSymbol);
    graphicsOverlay.getGraphics().add(complexPointGraphic);
  }

  private void addComplexPolyline(GraphicsOverlay graphicsOverlay) {

    // symbol layers for multilayer polyline
    SolidStrokeSymbolLayer blackDashes = new SolidStrokeSymbolLayer(1, ColorUtil.colorToArgb(Color.BLACK),
      List.of(new DashGeometricEffect(List.of(5.0, 3.0))));
    blackDashes.setCapStyle(StrokeSymbolLayer.CapStyle.ROUND);

    // create the yellow stroke inside
    SolidStrokeSymbolLayer yellowStroke = new SolidStrokeSymbolLayer(5, ColorUtil.colorToArgb(Color.YELLOW),
      List.of(new DashGeometricEffect()));
    yellowStroke.setCapStyle(StrokeSymbolLayer.CapStyle.ROUND);

    // create the black outline
    SolidStrokeSymbolLayer blackOutline = new SolidStrokeSymbolLayer(7, ColorUtil.colorToArgb(Color.BLACK),
      List.of(new DashGeometricEffect()));
    blackOutline.setCapStyle(StrokeSymbolLayer.CapStyle.ROUND);

    // create the multilayer polyline symbol
    var multilayerPolylineSymbol = new MultilayerPolylineSymbol(List.of(blackOutline, yellowStroke, blackDashes));
    PolylineBuilder polylineBuilder = new PolylineBuilder(SpatialReferences.getWgs84());
    polylineBuilder.addPoint(new Point(120, -25));
    polylineBuilder.addPoint(new Point(140, -25));

    // create the multilayer polyline graphic with geometry using the symbols created above
    Graphic complexLineGraphic = new Graphic(polylineBuilder.toGeometry(), multilayerPolylineSymbol);
    graphicsOverlay.getGraphics().add(complexLineGraphic);
  }

  private void addComplexPolygon(GraphicsOverlay graphicsOverlay) {

    // create the black outline
    SolidStrokeSymbolLayer blackOutline = new SolidStrokeSymbolLayer(7, ColorUtil.colorToArgb(Color.BLACK),
      List.of(new DashGeometricEffect()));
    blackOutline.setCapStyle(StrokeSymbolLayer.CapStyle.ROUND);

    // create the yellow stroke inside
    SolidStrokeSymbolLayer yellowStroke = new SolidStrokeSymbolLayer(5, ColorUtil.colorToArgb(Color.YELLOW),
      List.of(new DashGeometricEffect()));
    yellowStroke.setCapStyle(StrokeSymbolLayer.CapStyle.ROUND);

    // symbol layers for multilayer polyline
    SolidStrokeSymbolLayer blackDashes = new SolidStrokeSymbolLayer(1, ColorUtil.colorToArgb(Color.BLACK),
      List.of(new DashGeometricEffect(List.of(5.0, 3.0))));
    blackDashes.setCapStyle(StrokeSymbolLayer.CapStyle.SQUARE);

    // create a red filling for the polygon
    SolidFillSymbolLayer redFillLayer = new SolidFillSymbolLayer(ColorUtil.colorToArgb(Color.RED));

    // create the multilayer polygon symbol
    var multilayerPolygonSymbol = new MultilayerPolygonSymbol(List.of(redFillLayer, blackOutline, yellowStroke,
      blackDashes));

    // create the polygon
    var polygonBuilder = new PolygonBuilder(SpatialReferences.getWgs84());
    polygonBuilder.addPoint(new Point(120, 0));
    polygonBuilder.addPoint(new Point(140, 0));
    polygonBuilder.addPoint(new Point(140, -10));
    polygonBuilder.addPoint(new Point(120, -10));

    // create a multilayer polygon graphic with geometry using the symbols created above and add it to the graphics 
    // overlay
    var complexPolygonGraphic = new Graphic(polygonBuilder.toGeometry(), multilayerPolygonSymbol);
    graphicsOverlay.getGraphics().add(complexPolygonGraphic);

  }

  private void addTextGraphics(GraphicsOverlay overlay) {

    // create labels for each category of marker/symbol
    Graphic textGraphicForMarkers = new Graphic(new Point(-150, 50, SpatialReferences.getWgs84()),
      getTextSymbol("MultilayerPoint\nSimple Markers"));

    Graphic textGraphicForPictureMarkers = new Graphic(new Point(-80, 50, SpatialReferences.getWgs84()),
      getTextSymbol("MultilayerPoint\nPicture Markers"));

    Graphic textGraphicForLineSymbols = new Graphic(new Point(0, 50, SpatialReferences.getWgs84()),
      getTextSymbol("Multilayer\nPolyline"));

    Graphic textGraphicForFillSymbols = new Graphic(new Point(65, 50, SpatialReferences.getWgs84()),
      getTextSymbol("Multilayer\nPolygon"));

    Graphic textGraphicForComplexSymbols = new Graphic(new Point(130, 50, SpatialReferences.getWgs84()),
      getTextSymbol("Multilayer\nPolygon"));

    // add all graphics to the graphics overlay
    overlay.getGraphics().addAll(
      List.of(
        textGraphicForMarkers,
        textGraphicForPictureMarkers,
        textGraphicForLineSymbols,
        textGraphicForFillSymbols,
        textGraphicForComplexSymbols));
  }

  private TextSymbol getTextSymbol(String text) {
    // create a text symbol with the provided text and consistent style
    var textSymbol = new TextSymbol(
      20,
      text,
      ColorUtil.colorToArgb(Color.BLACK),
      TextSymbol.HorizontalAlignment.CENTER,
      TextSymbol.VerticalAlignment.MIDDLE
    );

    // give the text symbol a white background
    textSymbol.setBackgroundColor(ColorUtil.colorToArgb(Color.WHITE));
    return textSymbol;
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
