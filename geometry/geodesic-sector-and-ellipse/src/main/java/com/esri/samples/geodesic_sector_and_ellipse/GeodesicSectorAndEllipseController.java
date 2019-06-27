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

package com.esri.samples.geometry.geodesic_sector_and_ellipse;

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
import com.esri.arcgisruntime.symbology.LineSymbol;
import com.esri.arcgisruntime.symbology.MarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

public class GeodesicSectorAndEllipseController {

  @FXML private MapView mapView;
  @FXML private Slider axisDirectionSlider;
  @FXML private Spinner<Integer> maxPointCountSpinner;
  @FXML private Slider maxSegmentLengthSlider;
  @FXML private ComboBox<GeometryType> geometryTypeComboBox;
  @FXML private Slider sectorAngleSlider;
  @FXML private Slider semiAxis1LengthSlider;
  @FXML private Slider semiAxis2LengthSlider;
  @FXML private Slider startDirectionSlider;

  private Point center;
  private Graphic sectorGraphic;
  private Graphic ellipseGraphic;
  private FillSymbol sectorFillSymbol;
  private LineSymbol sectorLineSymbol;
  private MarkerSymbol sectorMarkerSymbol;

  public void initialize() {
    // initialize a map to a viewpoint and set it to the map view
    ArcGISMap map = new ArcGISMap(Basemap.createImagery());
    center = new Point(-13574921.207495, 4378809.903179, SpatialReference.create
        (3857));
    map.setInitialViewpoint(new Viewpoint(center, 10000));
    mapView.setMap(map);

    // create a graphics overlay for showing the geometries as graphics
    GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
    mapView.getGraphicsOverlays().add(graphicsOverlay);

    // create a graphic to show the geodesic sector geometry
    sectorGraphic = new Graphic();
    graphicsOverlay.getGraphics().add(sectorGraphic);

    // create green symbols for each sector output geometry type
    sectorFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x8800FF00, null);
    sectorLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0x8800FF00, 3);
    sectorMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0x8800FF00, 3);

    // create a red dotted outline graph for showing the geodesic ellipse geometry
    SimpleLineSymbol ellipseLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DOT, 0xFFFF0000, 2);
    ellipseGraphic = new Graphic();
    ellipseGraphic.setSymbol(ellipseLineSymbol);
    graphicsOverlay.getGraphics().add(ellipseGraphic);

    // set the center of the sector and ellipse where the user clicks on the map
    mapView.setOnMouseClicked(e -> {
      if (e.isStillSincePress() && e.getButton() == MouseButton.PRIMARY) {
        Point2D point2D = new Point2D(e.getX(), e.getY());
        center = mapView.screenToLocation(point2D);
        updateSector();
      }
    });

    // set up the controls with some default parameters
    GeodesicSectorParameters defaultParameters = new GeodesicSectorParameters(center, 100.0, 100.0, 15.0, 0.0);
    axisDirectionSlider.setValue(defaultParameters.getAxisDirection());
    maxPointCountSpinner.getValueFactory().setValue(Long.valueOf(defaultParameters.getMaxPointCount()).intValue());
    maxSegmentLengthSlider.setValue(defaultParameters.getMaxSegmentLength());
    geometryTypeComboBox.getItems().addAll(GeometryType.POLYGON, GeometryType.POLYLINE, GeometryType.MULTIPOINT);
    geometryTypeComboBox.getSelectionModel().select(GeometryType.POLYGON);
    sectorAngleSlider.setValue(defaultParameters.getSectorAngle());
    semiAxis1LengthSlider.setValue(defaultParameters.getSemiAxis1Length());
    semiAxis2LengthSlider.setValue(defaultParameters.getSemiAxis2Length());
    startDirectionSlider.setValue(defaultParameters.getStartDirection());

    // call updateSector when any of the controls change their value
    axisDirectionSlider.valueProperty().addListener(e -> updateSector());
    maxPointCountSpinner.valueProperty().addListener(e -> updateSector());
    maxSegmentLengthSlider.valueProperty().addListener(e -> updateSector());
    geometryTypeComboBox.valueProperty().addListener(e -> updateSector());
    sectorAngleSlider.valueProperty().addListener(e -> updateSector());
    semiAxis1LengthSlider.valueProperty().addListener(e -> updateSector());
    semiAxis2LengthSlider.valueProperty().addListener(e -> updateSector());
    startDirectionSlider.valueProperty().addListener(e -> updateSector());

    // update the sector with the default parameters
    updateSector();
  }

  /**
   * Updates the sector and ellipse graphics using the controls' values.
   */
  private void updateSector() {

    // create geodesic sector parameters
    GeodesicSectorParameters geodesicSectorParameters = new GeodesicSectorParameters();
    geodesicSectorParameters.setCenter(center);
    geodesicSectorParameters.setAxisDirection(axisDirectionSlider.getValue());
    geodesicSectorParameters.setMaxPointCount(maxPointCountSpinner.getValue());
    geodesicSectorParameters.setMaxSegmentLength(maxSegmentLengthSlider.getValue());
    geodesicSectorParameters.setGeometryType(geometryTypeComboBox.getSelectionModel().getSelectedItem());
    geodesicSectorParameters.setSectorAngle(sectorAngleSlider.getValue());
    geodesicSectorParameters.setSemiAxis1Length(semiAxis1LengthSlider.getValue());
    geodesicSectorParameters.setSemiAxis2Length(semiAxis2LengthSlider.getValue());
    geodesicSectorParameters.setStartDirection(startDirectionSlider.getValue());

    // create the geodesic sector parameter
    Geometry sectorGeometry = GeometryEngine.sectorGeodesic(geodesicSectorParameters);
    // set the sector graphic's geometry to the sector
    sectorGraphic.setGeometry(sectorGeometry);
    // update the graphic's symbol depending on the chosen output geometry type
    switch (sectorGeometry.getGeometryType()) {
      case MULTIPOINT:
        sectorGraphic.setSymbol(sectorMarkerSymbol);
        break;
      case POLYGON:
        sectorGraphic.setSymbol(sectorFillSymbol);
        break;
      case POLYLINE:
        sectorGraphic.setSymbol(sectorLineSymbol);
        break;
    }

    // create geodesic ellipse parameters using the same values from the geodesic sector parameters
    // use one of the constructors that sets some defaults for you
    GeodesicEllipseParameters geodesicEllipseParameters = new GeodesicEllipseParameters(center, semiAxis1LengthSlider
        .getValue(), semiAxis2LengthSlider.getValue());
    geodesicEllipseParameters.setAxisDirection(axisDirectionSlider.getValue());
    // show the geodesic ellipse that the sector is in
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
