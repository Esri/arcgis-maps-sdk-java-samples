/*
 * Copyright 2018 Esri.
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

package com.esri.samples.cut_geometry;

import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;

public class CutGeometrySample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Cut Geometry Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a map with the topographic basemap style
      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);

      // create a map view and set the map to it
      mapView = new MapView();
      mapView.setMap(map);

      // create a graphics overlay
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create a blue polygon graphic to cut
      Graphic polygonGraphic = new Graphic(createLakeSuperiorPolygon(), new SimpleFillSymbol(SimpleFillSymbol.Style
          .SOLID, Color.web("blue", 0.12), new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 2)));
      graphicsOverlay.getGraphics().add(polygonGraphic);

      // create a red polyline graphic to cut the polygon
      Graphic polylineGraphic = new Graphic(createBorder(), new SimpleLineSymbol(SimpleLineSymbol.Style.DOT,
          Color.RED, 3));
      graphicsOverlay.getGraphics().add(polylineGraphic);

      // zoom to show the polygon graphic
      mapView.setViewpointGeometryAsync(polygonGraphic.getGeometry());

      // create a graphics overlay to contain the cut areas
      GraphicsOverlay cutAreasOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(cutAreasOverlay);

      // create a button to perform the cut and reset operation
      var button = new Button("Cut");
      button.setOnAction(e -> {
        if (cutAreasOverlay.getGraphics().isEmpty()){
          // cut the polygon geometry with the polyline, expect two geometries
          List<Geometry> parts = GeometryEngine.cut(polygonGraphic.getGeometry(), (Polyline) polylineGraphic.getGeometry());

          // create graphics for the US and Canada sides
          Graphic canadaSide = new Graphic(parts.get(0), new SimpleFillSymbol(SimpleFillSymbol.Style.FORWARD_DIAGONAL,
            Color.AQUAMARINE, new SimpleLineSymbol(SimpleLineSymbol.Style.NULL, Color.WHITE, 0)));
          Graphic usSide = new Graphic(parts.get(1), new SimpleFillSymbol(SimpleFillSymbol.Style.FORWARD_DIAGONAL,
            Color.YELLOW, new SimpleLineSymbol(SimpleLineSymbol.Style.NULL, Color.WHITE, 0)));
          cutAreasOverlay.getGraphics().addAll(Arrays.asList(canadaSide, usSide));

          // update the button text
          button.setText("Reset");
        } else {
          // remove cut graphics
          cutAreasOverlay.getGraphics().clear();

          // update the button text
          button.setText("Cut");
        }
      });

      // add the map view to the stack pane
      stackPane.getChildren().addAll(mapView, button);
      StackPane.setAlignment(button, Pos.TOP_LEFT);
      StackPane.setMargin(button, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Creates a polyline along the US/Canada border over Lake Superior.
   *
   * @return polyline
   */
  private Polyline createBorder() {
    PointCollection points = new PointCollection(SpatialReferences.getWebMercator());
    points.add(new Point(-9981328.687124, 6111053.281447));
    points.add(new Point(-9946518.044066, 6102350.620682));
    points.add(new Point(-9872545.427566, 6152390.920079));
    points.add(new Point(-9838822.617103, 6157830.083057));
    points.add(new Point(-9446115.050097, 5927209.572793));
    points.add(new Point(-9430885.393759, 5876081.440801));
    points.add(new Point(-9415655.737420, 5860851.784463));
    return new Polyline(points);
  }

  /**
   * Creates a polygon of points around Lake Superior.
   *
   * @return polygon
   */
  private Polygon createLakeSuperiorPolygon() {
    PointCollection points = new PointCollection(SpatialReferences.getWebMercator());
    points.add(new Point(-10254374.668616, 5908345.076380));
    points.add(new Point(-10178382.525314, 5971402.386779));
    points.add(new Point(-10118558.923141, 6034459.697178));
    points.add(new Point(-9993252.729399, 6093474.872295));
    points.add(new Point(-9882498.222673, 6209888.368416));
    points.add(new Point(-9821057.766387, 6274562.532928));
    points.add(new Point(-9690092.583250, 6241417.023616));
    points.add(new Point(-9605207.742329, 6206654.660191));
    points.add(new Point(-9564786.389509, 6108834.986367));
    points.add(new Point(-9449989.747500, 6095091.726408));
    points.add(new Point(-9462116.153346, 6044160.821855));
    points.add(new Point(-9417652.665244, 5985145.646738));
    points.add(new Point(-9438671.768711, 5946341.148031));
    points.add(new Point(-9398250.415891, 5922088.336339));
    points.add(new Point(-9419269.519357, 5855797.317714));
    points.add(new Point(-9467775.142741, 5858222.598884));
    points.add(new Point(-9462924.580403, 5902686.086985));
    points.add(new Point(-9598740.325877, 5884092.264688));
    points.add(new Point(-9643203.813979, 5845287.765981));
    points.add(new Point(-9739406.633691, 5879241.702350));
    points.add(new Point(-9783061.694736, 5922896.763395));
    points.add(new Point(-9844502.151022, 5936640.023354));
    points.add(new Point(-9773360.570059, 6019099.583107));
    points.add(new Point(-9883306.649729, 5968977.105610));
    points.add(new Point(-9957681.938918, 5912387.211662));
    points.add(new Point(-10055501.612742, 5871965.858842));
    points.add(new Point(-10116942.069028, 5884092.264688));
    points.add(new Point(-10111283.079633, 5933406.315128));
    points.add(new Point(-10214761.742852, 5888134.399970));
    points.add(new Point(-10254374.668616, 5901877.659929));
    return new Polygon(points);
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
