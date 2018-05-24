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

package com.esri.samples.geometry.geodesic_sectors;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.input.MouseButton;

import com.esri.arcgisruntime.geometry.GeodesicEllipseParameters;
import com.esri.arcgisruntime.geometry.GeodesicSectorParameters;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.FillSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;

public class GeodesicSectorsController {

  @FXML private MapView mapView;
  @FXML private Slider axisDirectionSlider;
  @FXML private Spinner<Integer> maxPointCountSpinner;
  @FXML private Slider maxSegmentLengthSlider;
  @FXML private ComboBox<GeometryType> outputGeometryTypeComboBox;
  @FXML private Slider sectorAngleSlider;
  @FXML private Slider semiAxis1LengthSlider;
  @FXML private Slider semiAxis2LengthSlider;
  @FXML private Slider startDirectionSlider;

  private Point center;
  private Graphic sectorsGraphic;
  private Graphic ellipseGraphic;

  public void initialize() {
    ArcGISMap map = new ArcGISMap(Basemap.createImagery());
    center = new Point(-13574921.207495, 4378809.903179, SpatialReference.create
        (3857));
    map.setInitialViewpoint(new Viewpoint(center, 10000));
    mapView.setMap(map);

    GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
    mapView.getGraphicsOverlays().add(graphicsOverlay);

    FillSymbol sectorFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x8800FF00, null);
    sectorsGraphic = new Graphic();
    sectorsGraphic.setSymbol(sectorFillSymbol);
    graphicsOverlay.getGraphics().add(sectorsGraphic);

    SimpleLineSymbol ellipseLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DOT, 0xFFFF0000, 2);
    ellipseGraphic = new Graphic();
    ellipseGraphic.setSymbol(ellipseLineSymbol);
    graphicsOverlay.getGraphics().add(ellipseGraphic);

    mapView.setOnMouseClicked(e -> {
      if (e.isStillSincePress() && e.getButton() == MouseButton.PRIMARY) {
        Point2D point2D = new Point2D(e.getX(), e.getY());
        center = mapView.screenToLocation(point2D);
        updateSectors();
      }
    });

    GeodesicSectorParameters defaultParameters = new GeodesicSectorParameters(center, 100.0, 100.0, 15.0, 0.0);
    axisDirectionSlider.setValue(defaultParameters.getAxisDirection());
    maxPointCountSpinner.getValueFactory().setValue(Long.valueOf(defaultParameters.getMaxPointCount()).intValue());
    maxSegmentLengthSlider.setValue(defaultParameters.getMaxSegmentLength());
    outputGeometryTypeComboBox.getItems().addAll(GeometryType.POLYGON, GeometryType.POLYLINE, GeometryType.MULTIPOINT);
    outputGeometryTypeComboBox.getSelectionModel().select(GeometryType.POLYGON);
    sectorAngleSlider.setValue(defaultParameters.getSectorAngle());
    semiAxis1LengthSlider.setValue(defaultParameters.getSemiAxis1Length());
    semiAxis2LengthSlider.setValue(defaultParameters.getSemiAxis2Length());
    startDirectionSlider.setValue(defaultParameters.getStartDirection());

    updateSectors();
  }

  /**
   * Updates the map view's grid when the "Update" button is clicked.
   */
  @FXML
  private void updateSectors() {

    GeodesicSectorParameters geodesicSectorParameters = new GeodesicSectorParameters();
    geodesicSectorParameters.setCenter(center);
    geodesicSectorParameters.setAxisDirection(axisDirectionSlider.getValue());
    geodesicSectorParameters.setMaxPointCount(maxPointCountSpinner.getValue());
    geodesicSectorParameters.setMaxSegmentLength(maxSegmentLengthSlider.getValue());
    geodesicSectorParameters.setGeometryType(outputGeometryTypeComboBox.getSelectionModel().getSelectedItem());
    geodesicSectorParameters.setSectorAngle(sectorAngleSlider.getValue());
    geodesicSectorParameters.setSemiAxis1Length(semiAxis1LengthSlider.getValue());
    geodesicSectorParameters.setSemiAxis2Length(semiAxis2LengthSlider.getValue());
    geodesicSectorParameters.setStartDirection(startDirectionSlider.getValue());

    GeodesicEllipseParameters geodesicEllipseParameters = new GeodesicEllipseParameters(center, semiAxis1LengthSlider
        .getValue(), semiAxis2LengthSlider.getValue());
    geodesicEllipseParameters.setAxisDirection(axisDirectionSlider.getValue());


    Geometry sectorGeometry = GeometryEngine.sectorGeodesic(geodesicSectorParameters);
    sectorsGraphic.setGeometry(sectorGeometry);

    Geometry ellipseGeometry = GeometryEngine.ellipseGeodesic(geodesicEllipseParameters);
    ellipseGraphic.setGeometry(ellipseGeometry);
  }

  /**
   * Disposes of application resources.
   */
  void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }

}
