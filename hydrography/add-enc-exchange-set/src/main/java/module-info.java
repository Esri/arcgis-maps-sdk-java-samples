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
module add.enc.exchange.set.main {
  // require ArcGIS Runtime module
  requires com.esri.arcgisruntime;

  // NOTE: only require the JavaFX modules that the sample uses, all samples will use graphics.
  // require JavaFX modules
  requires javafx.graphics;
  requires javafx.controls;

  exports com.esri.samples.add_enc_exchange_set;
}