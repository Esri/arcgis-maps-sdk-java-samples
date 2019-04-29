package com.esri.samples.raster.raster_rendering_rule;

import com.esri.arcgisruntime.arcgisservices.RenderingRuleInfo;
import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.raster.ImageServiceRaster;
import com.esri.arcgisruntime.raster.RenderingRule;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;

public class RasterRenderingRuleSample extends Application {

    private MapView mapView;
    private ComboBox<String> renderingRuleDropDownMenu;

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
            ArcGISMap map = new ArcGISMap(Basemap.createStreets());

            // add the map to a new map view
            mapView = new MapView();
            mapView.setMap(map);

            // create an Image Service Raster as a raster layer and add to map
            final ImageServiceRaster imageServiceRaster = new ImageServiceRaster("http://sampleserver6.arcgisonline" +
                    ".com/arcgis/rest/services/CharlotteLAS/ImageServer");
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

                        // build a rendering rule hashmap and add the names of the rules to the drop down menu
                        HashMap<String, RenderingRuleInfo> renderingRuleInfoHashMap = new HashMap<>();

                        for (RenderingRuleInfo renderingRuleInfo : renderingRuleInfos){
                            // get the name of the rendering rule
                            String renderingRuleName = renderingRuleInfo.getName();

                            // add the rendering rule to the hash map with the name as key
                            renderingRuleInfoHashMap.put(renderingRuleName, renderingRuleInfo);

                            // add the name of the rendering rule to the drop-down menu
                            renderingRuleDropDownMenu.getItems().add(renderingRuleName);
                        }

                        // change the renderingRule when selected and render the raster
                        renderingRuleDropDownMenu.getSelectionModel().selectedItemProperty().addListener(o -> {
                            // save the selected rendering rule name to a string
                            String selectedRenderingRuleName = renderingRuleDropDownMenu.getSelectionModel().getSelectedItem();
                            // find the appropriate rendering rule in the hash map of rendering rule infos
                            RenderingRuleInfo selectedRenderingRuleInfo = renderingRuleInfoHashMap.get(selectedRenderingRuleName);
                            // clear all rasters
                            map.getOperationalLayers().clear();
                            // create a rendering rule object using the rendering rule info
                            RenderingRule renderingRule = new RenderingRule(selectedRenderingRuleInfo);
                            // create a new image service raster
                            ImageServiceRaster appliedImageServiceRaster = new ImageServiceRaster("http://sampleserver6.arcgisonline" +
                                    ".com/arcgis/rest/services/CharlotteLAS/ImageServer");
                            // apply the rendering rule
                            appliedImageServiceRaster.setRenderingRule(renderingRule);
                            RasterLayer rasterLayer = new RasterLayer(appliedImageServiceRaster);
                            map.getOperationalLayers().add(rasterLayer);
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
