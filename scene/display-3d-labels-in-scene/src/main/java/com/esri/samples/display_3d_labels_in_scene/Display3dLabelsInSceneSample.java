/*
 * Copyright 2021 Esri.
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


package com.esri.samples.display_3d_labels_in_scene;

import com.esri.arcgisruntime.arcgisservices.LabelingPlacement;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.labeling.ArcadeLabelExpression;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.arcgisservices.LabelDefinition;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.symbology.TextSymbol;

public class Display3dLabelsInSceneSample extends Application {

  private SceneView sceneView;

  @Override
  public void start(Stage stage) {

    try {

      // create stack pane and JavaFX app scene
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);

      // set title, size, and add the JavaFX scene to stage
      stage.setTitle("Display 3D Labels in Scene Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(fxScene);
      stage.show();

      // create a scene from an ArcGISOnline portal item
      PortalItem portalItem = new PortalItem(
        new Portal("https://www.arcgis.com", false), "850dfee7d30f4d9da0ebca34a533c169");
      ArcGISScene scene = new ArcGISScene(portalItem);

      // set the scene to the sceneview, and add the sceneview to the JavaFX stack pane
      sceneView = new SceneView();
      sceneView.setArcGISScene(scene);
      stackPane.getChildren().addAll(sceneView);

      scene.addDoneLoadingListener(() -> {
        if (scene.getLoadStatus() == LoadStatus.LOADED) {

          // filter through the scene's operational layers to find the layer named "Gas",
          // and find the sublayer within that called "Gas Main" to apply the label definition to
          scene.getOperationalLayers().stream()
            .filter(layer -> layer.getName().equals("Gas"))
            .flatMap(gasLayer -> gasLayer.getSubLayerContents().stream())
            .forEach(subLayer -> {
              if (subLayer.getName().equals("Gas Main")) {
                FeatureLayer featureLayer = (FeatureLayer) subLayer;
                featureLayer.setLabelsEnabled(true);
                // clear any existing label definitions on the layer
                featureLayer.getLabelDefinitions().clear();
                featureLayer.getLabelDefinitions().add(createLabelDefinition());
              }
            });

        } else {
          System.out.println("Scene failed to load " + scene.getLoadError().getCause());
        }

      });
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  private LabelDefinition createLabelDefinition() {

    TextSymbol textSymbol = new TextSymbol();
    textSymbol.setColor(0xFFFFAB00); //orange
    textSymbol.setHaloColor(0xFFFFFFFF); //white
    textSymbol.setHaloWidth(2);
    textSymbol.setSize(16);

    LabelDefinition labelDefinition = new LabelDefinition();
    labelDefinition.setExpression(new ArcadeLabelExpression("Text($feature.INSTALLATIONDATE, `DD MMM YY`)"));
    labelDefinition.setPlacement(LabelingPlacement.LINE_ABOVE_ALONG);
    labelDefinition.setUseCodedValues(true);
    labelDefinition.setTextSymbol(textSymbol);

    return labelDefinition;
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

//          // filter the scene's list of layers to retrieve the "Gas" layer: there is only one in the data
//          List<Layer> gasLayers = scene.getOperationalLayers()
//            .stream()
//            .filter(layer -> layer.getName().equals("Gas"))
//            .collect(Collectors.toList());
//
//          // access the "Gas Main" feature layer from the first gas layer returned
//          if (!gasLayers.isEmpty()) {
//            // for all the layers under "Gas"
//            for (Layer layer : gasLayers) {
//              // loop through the layers under gas for how many times there are layers and convert them to feature layers
//              for (int i = 0; i < layer.getSubLayerContents().size(); i++) {
//                FeatureLayer featureLayer = (FeatureLayer) layer.getSubLayerContents().get(i);
//
//                // find the layer which has 3D labels enabled - "Gas Main" - and add a label definition
//                if (featureLayer.getName().equals("Gas Main")) {
//                  featureLayer.setLabelsEnabled(true);
//                  featureLayer.getLabelDefinitions().add(createLabelDefinition());
//                }
//              }
//            }
//          }