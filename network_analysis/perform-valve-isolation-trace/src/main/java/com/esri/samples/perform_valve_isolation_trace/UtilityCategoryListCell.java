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

package com.esri.samples.perform_valve_isolation_trace;

import com.esri.arcgisruntime.utilitynetworks.UtilityCategory;
import javafx.scene.control.ListCell;

/**
 * Shows the name of the UtilityCategory in the selection combobox.
 */
public class UtilityCategoryListCell extends ListCell<UtilityCategory> {
    @Override
    protected void updateItem(UtilityCategory item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
        } else {
            setText(item.getName());
        }
    }
}




