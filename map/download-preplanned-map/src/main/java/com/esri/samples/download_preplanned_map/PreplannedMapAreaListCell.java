/* Copyright 2019 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.esri.samples.download_preplanned_map;

import javafx.scene.control.ListCell;

import com.esri.arcgisruntime.tasks.offlinemap.PreplannedMapArea;

/**
 * Shows the title of the PreplannedMapArea in the selection list view.
 */
class PreplannedMapAreaListCell extends ListCell<PreplannedMapArea> {

  @Override
  protected void updateItem(PreplannedMapArea preplannedMapArea, boolean empty) {
    super.updateItem(preplannedMapArea, empty);
    if (preplannedMapArea != null) {

      setText(preplannedMapArea.getPortalItem().getTitle());

    } else {
      setGraphic(null);
      setText(null);
    }
  }
}
