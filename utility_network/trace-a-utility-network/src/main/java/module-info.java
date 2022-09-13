/*
 * Copyright 2022 Esri.
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

module trace.a.utility.network.main {
  // require ArcGIS Runtime module
  requires com.esri.arcgisruntime;

  // require JavaFX modules
  requires javafx.base;
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.graphics;

  // open fxml module for annotated objects to be reflectively accessible
  opens com.esri.samples.trace_a_utility_network to javafx.fxml;
  exports com.esri.samples.trace_a_utility_network;
}
