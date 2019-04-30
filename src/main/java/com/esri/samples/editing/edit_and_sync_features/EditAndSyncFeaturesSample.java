/* Copyright 2017 Esri
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

package com.esri.samples.editing.edit_and_sync_features;

import com.esri.arcgisruntime.mapping.view.MapView;
import javafx.application.Application;
import javafx.stage.Stage;

public class EditAndSyncFeaturesSample extends Application {

    private MapView mapView;


    @Override
    public void start(Stage stage) {

        try{


//            1. Create a `GeodatabaseSyncTask` from a URL.
//            1. Use `createDefaultGenerateGeodatabaseParametersAsync(...)` to create `GenerateGeodatabaseParameters` from the `GeodatabaseSyncTask`, passing in an `Envelope` argument.
//            1. Create a `GenerateGeodatabaseJob` from the `GeodatabaseSyncTask` using `generateGeodatabaseAsync(...)` passing in parameters and a path to the local geodatabase.
//            1. Start the `GenerateGeodatabaseJob` and, on success, load the `Geodatabase`.
//            1. On successful loading, call `getGeodatabaseFeatureTables()` on the `Geodatabase` and add it to the `ArcGISMap`'s operational layers.
//            1. To sync changes between the local and web geodatabases:
//            1. Define `SyncGeodatabaseParameters` including setting the `SyncGeodatabaseParameters.SyncDirection`.
//            1. Create a `SyncGeodatabaseJob` from `GeodatabaseSyncTask` using `.syncGeodatabaseAsync(...)` passing the `SyncGeodatabaseParameters` and `Geodatabase` as arguments.
//            1. Start the `SyncGeodatabaseJob`.






        } catch (Exception e) {
            // on any error, display the stack trace
            e.printStackTrace();
        }
    }

    /**
     * Stops and releases all resources used in application.
     */
    @Override
    public void stop() {

        if (mapView != null) {
            mapView.dispose();
        }
    }

    /**
     * Opens and runs application.
     *
     * @param args arguments passed to this application
     */
    public static void main(String[] args) {

        Application.launch(args);
    }
}
