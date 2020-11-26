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

package com.esri.samples.display_grid;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.paint.Color;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Grid;
import com.esri.arcgisruntime.mapping.view.LatitudeLongitudeGrid;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.MgrsGrid;
import com.esri.arcgisruntime.mapping.view.UsngGrid;
import com.esri.arcgisruntime.mapping.view.UtmGrid;
import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.symbology.LineSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;

public class DisplayGridController {

  @FXML private MapView mapView;
  @FXML private ComboBox<GridType> gridTypeComboBox;
  @FXML private CheckBox labelsVisibleCheckBox;
  @FXML private CheckBox gridVisibleCheckBox;
  @FXML private ColorPicker gridColorPicker;
  @FXML private ColorPicker labelColorPicker;
  @FXML private ComboBox<Grid.LabelPosition> labelPositionComboBox;
  @FXML private ComboBox<LatitudeLongitudeGrid.LabelFormat> labelFormatComboBox;

  /**
   * Used for combo box.
   */
  private enum GridType {
    LAT_LON, UTM, USNG, MGRS
  }

  public void initialize() {

    // authentication with an API key or named user is required to access basemaps and other location services
    String yourAPIKey = System.getProperty("apiKey");
    ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

    // create a map and set its initial viewpoint
    ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_IMAGERY_STANDARD);
    map.setInitialViewpoint(new Viewpoint(new Point(-10336141.70018318, 5418213.05332071, SpatialReference.create
        (3857)), 6450785));

    // set the map to the map view
    mapView.setMap(map);

    // set initial values for options
    gridTypeComboBox.getItems().addAll(GridType.values());
    gridTypeComboBox.setValue(GridType.LAT_LON);
    gridColorPicker.setValue(Color.WHITE);
    labelColorPicker.setValue(Color.RED);
    labelPositionComboBox.getItems().addAll(Grid.LabelPosition.values());
    labelPositionComboBox.setValue(Grid.LabelPosition.TOP_LEFT);
    labelFormatComboBox.getItems().addAll(LatitudeLongitudeGrid.LabelFormat.values());
    labelFormatComboBox.setValue(LatitudeLongitudeGrid.LabelFormat.DECIMAL_DEGREES);

    // label position and format only apply to Lat Lon grid type
    labelPositionComboBox.disableProperty().bind(Bindings.createBooleanBinding(() ->
            gridTypeComboBox.getSelectionModel().getSelectedItem() != GridType.LAT_LON,
        gridTypeComboBox.getSelectionModel().selectedItemProperty())
    );
    labelFormatComboBox.disableProperty().bind(Bindings.createBooleanBinding(() ->
        gridTypeComboBox.getSelectionModel().getSelectedItem() != GridType.LAT_LON,
        gridTypeComboBox.getSelectionModel().selectedItemProperty())
    );

    // update the grid with the default values on start
    updateGrid();
  }

  /**
   * Updates the map view's grid when the "Update" button is clicked.
   */
  @FXML
  private void updateGrid() {
    // grid type
    Grid grid = null;
    switch (gridTypeComboBox.getSelectionModel().getSelectedItem()) {
      case LAT_LON:
        grid = new LatitudeLongitudeGrid();
        break;
      case UTM:
        grid = new UtmGrid();
        break;
      case USNG:
        grid = new UsngGrid();
        break;
      case MGRS:
        grid = new MgrsGrid();
        break;
    }

    // color the grid lines and labels for each grid level
    for (int i = 0; i < grid.getLevelCount(); i++) {
      // grid lines
      LineSymbol gridLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, ColorUtil.colorToArgb(gridColorPicker
          .getValue()), 1 + i);
      grid.setLineSymbol(i, gridLineSymbol);

      // labels
      TextSymbol labelTextSymbol = new TextSymbol(14, "text", ColorUtil.colorToArgb(labelColorPicker.getValue()),
          TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.BOTTOM);
      labelTextSymbol.setHaloColor(0xFFFFFFFF); //white
      labelTextSymbol.setHaloWidth(2 + i);
      grid.setTextSymbol(i, labelTextSymbol);
    }

    // grid visibility
    grid.setVisible(gridVisibleCheckBox.isSelected());

    // label visibility
    grid.setLabelVisible(labelsVisibleCheckBox.isSelected());

    // label position and format
    if (grid instanceof LatitudeLongitudeGrid) {
      grid.setLabelPosition(labelPositionComboBox.getSelectionModel().getSelectedItem());
      ((LatitudeLongitudeGrid) grid).setLabelFormat(labelFormatComboBox.getSelectionModel().getSelectedItem());
    }

    // set the grid
    mapView.setGrid(grid);
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
