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
/**
 * Converts time values to strings to display on the slider.
 */
public class SliderStringConverter extends StringConverter<Double> {

  @Override
  public String toString(Double hour) {
    if (hour == 240) return "4am";
    if (hour == 480) return "8am";
    if (hour == 720) return "Midday";
    if (hour == 960) return "4pm";
    if (hour == 1200) return "8pm";

    return "Midnight";
  }

  @Override
  public Double fromString(String string) {
    return null;
  }
}
