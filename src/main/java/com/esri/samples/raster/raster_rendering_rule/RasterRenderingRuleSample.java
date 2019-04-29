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

package com.esri.samples.raster.raster_rendering_rule;

import java.util.List;

import com.esri.arcgisruntime.arcgisservices.RenderingRuleInfo;
import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.raster.ImageServiceRaster;
import com.esri.arcgisruntime.raster.RenderingRule;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class RasterRenderingRuleSample extends Application {

    private ComboBox<String> renderingRuleDropDownMenu;
    private MapView mapView;
    private ArcGISMap map;
    private final String imageServiceUrl = "http://sampleserver6.arcgisonline.com/arcgis/rest/services/CharlotteLAS/ImageServer";

    @Override
    public void start(Stage stage) {
        try {
            // create stack pane and application scene
            StackPane stackPane = new StackPane();
            Scene scene = new Scene(stackPane);

            // set title, size, and add scene to stage
            stage.setTitle("Raster Rendering Rule Sample");
            stage.setWidth(800);
            stage.setHeight(700);
            stage.setScene(scene);
            stage.show();

            // create drop down menu of Rendering Rules
            renderingRuleDropDownMenu = new ComboBox<>();
            renderingRuleDropDownMenu.setPromptText("Select a Raster Rendering Rule");
            renderingRuleDropDownMenu.setEditable(false);
            renderingRuleDropDownMenu.setMaxWidth(260.0);

            // create a Streets BaseMap
            map = new ArcGISMap(Basemap.createStreets());

            // add the map to a new map view
            mapView = new MapView();
            mapView.setMap(map);

            // create an Image Service Raster as a raster layer and add to map
            final ImageServiceRaster imageServiceRaster = new ImageServiceRaster(imageServiceUrl);
            final RasterLayer imageRasterLayer = new RasterLayer(imageServiceRaster);
            map.getOperationalLayers().add(imageRasterLayer);

            imageServiceRaster.loadAsync();

            // add event listener to loading of Image Service Raster and wait until loaded
            imageRasterLayer.addDoneLoadingListener(() -> {
                if (imageRasterLayer.getLoadStatus() == LoadStatus.LOADED){
                    // zoom to extent of the raster
                    mapView.setViewpointGeometryAsync(imageServiceRaster.getServiceInfo().getFullExtent());

                    // get the predefined rendering rules
                    List<RenderingRuleInfo> renderingRuleInfos = imageServiceRaster.getServiceInfo().getRenderingRuleInfos();

                    //populate the drop down menu with the rendering rule names
                    populateDropDownMenu(renderingRuleInfos);

                    // listen to selection in the drop-down menu
                    renderingRuleDropDownMenu.getSelectionModel().selectedItemProperty().addListener(o -> {
                        // get the index of the selected rendering rule
                        int selectedRenderingRuleIndex = renderingRuleDropDownMenu.getSelectionModel().getSelectedIndex();
                        // apply the selected rendering rule
                        applyRenderingRule(renderingRuleInfos, selectedRenderingRuleIndex);
                    });
                }
            });

            // add the map view to the stack pane
            stackPane.getChildren().addAll(mapView, renderingRuleDropDownMenu);
            StackPane.setAlignment(renderingRuleDropDownMenu, Pos.TOP_LEFT);
            StackPane.setMargin(renderingRuleDropDownMenu, new Insets(10, 0, 0, 10));
        } catch (Exception e) {
            // on any error, display the stack trace.
            e.printStackTrace();
        }
    }

    /*
     * Populate the drop down menu with the rendering rule names
     * @param renderingRuleInfos    list of rendering rule infos to add to the drop-down
     */

    private void populateDropDownMenu(List<RenderingRuleInfo> renderingRuleInfos){
        for (RenderingRuleInfo renderingRuleInfo : renderingRuleInfos){
            // get the name of the rendering rule
            String renderingRuleName = renderingRuleInfo.getName();
            // add the name of the rendering rule to the drop-down menu
            renderingRuleDropDownMenu.getItems().add(renderingRuleName);
        }
    }

    /*
     * Apply a rendering rule on a Raster and add it to the map
     * @param renderingRuleInfos    list of rendering rule infos to select desired rule from from
     * @param index                 selected drop-down menu item representing the rule to apply
     */

    private void applyRenderingRule(List<RenderingRuleInfo> renderingRuleInfos, int index){
        // get the requested rendering rule info from the list
        RenderingRuleInfo selectedRenderingRuleInfo = renderingRuleInfos.get(index);
        // clear all rasters
        map.getOperationalLayers().clear();
        // create a rendering rule object using the rendering rule info
        RenderingRule renderingRule = new RenderingRule(selectedRenderingRuleInfo);
        // create a new image service raster
        ImageServiceRaster appliedImageServiceRaster = new ImageServiceRaster(imageServiceUrl);
        // apply the rendering rule
        appliedImageServiceRaster.setRenderingRule(renderingRule);
        RasterLayer rasterLayer = new RasterLayer(appliedImageServiceRaster);
        map.getOperationalLayers().add(rasterLayer);
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
