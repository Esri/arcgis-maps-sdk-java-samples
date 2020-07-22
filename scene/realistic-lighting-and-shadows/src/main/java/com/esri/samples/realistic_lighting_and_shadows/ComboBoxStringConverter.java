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

import javafx.util.StringConverter;

import com.esri.arcgisruntime.mapping.view.LightingMode;
/**
 * Converts the LightningMode values to strings to display in the open ComboBox.
 */
public class ComboBoxStringConverter extends StringConverter<LightingMode> {

  @Override
  public String toString(LightingMode mode) {
    if (mode == LightingMode.LIGHT) return "Sun light only";
    if (mode == LightingMode.LIGHT_AND_SHADOWS) return "Sun light with shadows";
    if (mode == LightingMode.NO_LIGHT) return "No sun light effect";

    else return "Sun light only";
  }

  @Override
  public LightingMode fromString(String string) {
    return null;
  }
}
