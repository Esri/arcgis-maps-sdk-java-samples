/*
 * Copyright 2020 Esri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.esri.samples.realistic_lighting_and_shadows;

import javafx.scene.control.ListCell;

import com.esri.arcgisruntime.mapping.view.LightingMode;
/**
 * Converts the LightningMode values to strings to display in the open ComboBox.
 */
public class LightingModeListCell extends ListCell<LightingMode> {

  @Override
  protected void updateItem(LightingMode mode, boolean empty) {

    super.updateItem(mode, empty);
    if (mode == LightingMode.LIGHT) setText("Sun light only");
    else if (mode == LightingMode.LIGHT_AND_SHADOWS) setText("Sun light with shadows");
    else if (mode == LightingMode.NO_LIGHT) setText("No sun light effect");

    else setText("Sun light only");
  }
}
