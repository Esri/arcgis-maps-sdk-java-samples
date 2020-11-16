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

package com.esri.samples.group_layers;

import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TreeCell;

import com.esri.arcgisruntime.layers.Layer;

/**
 * A custom tree cell for displaying a layer in a tree view. Includes a check box or radio button
 * for toggling the layer's visibility.
 */
public class LayerTreeCell extends TreeCell<Layer> {

  @Override
  public void updateItem(Layer layer, boolean empty) {
    super.updateItem(layer, empty);
    if (!empty) {

      // set the label to the layer's name
      setText(formatName(layer.getName()));

      if (layer.getName().equals("DevA_BuildingShells") || layer.getName().equals("DevB_BuildingShells")) {

        // if the layer is a building shell create a radio button and assign to the toggle group
        RadioButton radioButton = new RadioButton();
        setGraphic(radioButton);
        radioButton.setToggleGroup(GroupLayersSample.buildingsToggleGroup);

        // toggle the layer's visibility when the radio button is selected
        radioButton.setSelected(layer.isVisible());
        radioButton.selectedProperty().addListener(e -> layer.setVisible(radioButton.isSelected()));
      } else {

        // if the layer is not a building shell, create a checkbox
        CheckBox checkBox = new CheckBox();
        setGraphic(checkBox);

        // toggle the layer's visibility when the check box is toggled
        checkBox.setSelected(layer.isVisible());
        checkBox.selectedProperty().addListener(e -> layer.setVisible(checkBox.isSelected()));
      }
    } else {
      setText(null);
      setGraphic(null);
    }
  }

  /**
   * Format the layer's name property to a user-friendly string.
   *
   * @param layerName the name of the layer
   * @return a formatted string
   */
  private String formatName(String layerName) {
    switch (layerName) {
      case "DevA_Trees":
        return "Trees";
      case "DevA_Pathways":
        return "Pathways";
      case "DevA_BuildingShells":
        return "Buildings A";
      case "DevB_BuildingShells":
        return "Buildings B";
      case "DevelopmentProjectArea":
        return "Project Area";
      default:
        return layerName;
    }
  }
}
