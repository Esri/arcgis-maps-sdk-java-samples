/*
 * Copyright 2018 Esri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.esri.samples.featurelayers.feature_layer_extrusion;

import java.util.Arrays;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.ClassBreaksRenderer;
import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.symbology.Renderer;
import com.esri.arcgisruntime.symbology.SceneSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSceneSymbol;

public class FeatureLayerExtrusionSample extends Application {

  private SceneView sceneView;

  @Override
  public void start(Stage stage) {

    StackPane stackPane = new StackPane();
    Scene fxScene = new Scene(stackPane);
    // for adding styling to any controls that are added 
    fxScene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Feature Layer Extrusion Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(fxScene);
    stage.show();

    // so scene can be visible within application
    ArcGISScene scene = new ArcGISScene(Basemap.createImagery());
    sceneView = new SceneView();
    sceneView.setArcGISScene(scene);
    stackPane.getChildren().add(sceneView);

    // add base surface for elevation data
    Surface surface = new Surface();
    surface.getElevationSources().add(new ArcGISTiledElevationSource("http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer"));
    scene.setBaseSurface(surface);

    // get us census data as a service feature table
    ServiceFeatureTable enchantmentsFeatureTable = new ServiceFeatureTable("https://services2.arcgis.com/ZQgQTuoyBrtmoGdP/arcgis/rest/services/The_Enchantments/FeatureServer/0");

    // creates feature layer from table and add to scene
    final FeatureLayer enchantmentsFeatureLayer = new FeatureLayer(enchantmentsFeatureTable);
    // feature layer must be rendered dynamically for extrusion to work
    enchantmentsFeatureLayer.setRenderingMode(FeatureLayer.RenderingMode.DYNAMIC);
    scene.getOperationalLayers().add(enchantmentsFeatureLayer);

    final Renderer renderer = createClassBreaksRenderer();
    // set the extrusion mode to absolute height
    renderer.getSceneProperties().setExtrusionMode(Renderer.SceneProperties.ExtrusionMode.BASE_HEIGHT);
    enchantmentsFeatureLayer.setRenderer(renderer);

    // set camera to focus on state features
    enchantmentsFeatureLayer.addDoneLoadingListener(() ->
      sceneView.setViewpoint(new Viewpoint(enchantmentsFeatureLayer.getFullExtent()))
    );

    // scale down outlier populations
    String extrusionExpression = "([Heartrate] - 86) * 5";
    renderer.getSceneProperties().setExtrusionExpression(extrusionExpression);
  }

  private ClassBreaksRenderer createClassBreaksRenderer() {
    final int gray = ColorUtil.colorToArgb(Color.color(0.0, 0.6, 0.0, 1.0));
    final int blue1 = ColorUtil.colorToArgb(Color.color(0.6, 0.6, 0.0, 1.0));
    final int blue2 = ColorUtil.colorToArgb(Color.color(0.8, 0.6, 0.0, 1.0));
    final int blue3 = ColorUtil.colorToArgb(Color.color(0.9, 0.4, 0.0, 1.0));
    final int blue4 = ColorUtil.colorToArgb(Color.color(1.0, 0.0, 0.0, 1.0));

    // create 5 fill symbols with different shades of blue and a gray outline
    SimpleLineSymbol outline = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, gray, 1);
    SimpleFillSymbol classSymbol1 = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, blue1, outline);
    SimpleFillSymbol classSymbol2 = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, blue2, outline);
    SimpleFillSymbol classSymbol3 = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, blue3, outline);
    SimpleFillSymbol classSymbol4 = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, blue4, outline);

    // create 5 classes for different population ranges
    ClassBreaksRenderer.ClassBreak classBreak1 = new ClassBreaksRenderer.ClassBreak( "recovery", "80 to 130", 80, 130, classSymbol1);
    ClassBreaksRenderer.ClassBreak classBreak2 = new ClassBreaksRenderer.ClassBreak("aerobic", "130 to 150", 130, 165, classSymbol2);
    ClassBreaksRenderer.ClassBreak classBreak3 = new ClassBreaksRenderer.ClassBreak("endurance", "150 to 165", 165, 180, classSymbol3);
    ClassBreaksRenderer.ClassBreak classBreak4 = new ClassBreaksRenderer.ClassBreak( "maximum aerobic", "165 to 180", 180, 200, classSymbol4);
    // create the renderer for the POP2007 field
    return new ClassBreaksRenderer("Heartrate", Arrays.asList(classBreak1, classBreak2, classBreak3, classBreak4));
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    if (sceneView != null) {
      sceneView.dispose();
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
