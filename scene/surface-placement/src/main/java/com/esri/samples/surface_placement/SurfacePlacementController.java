/*
 * Copyright 2017 Esri.
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

package com.esri.samples.surface_placement;

import java.util.Arrays;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LayerSceneProperties;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;

public class SurfacePlacementController {

  @FXML private SceneView sceneView;
  @FXML private Slider zValueSlider;
  @FXML private ToggleGroup toggleGroup;
  @FXML private RadioButton drapedBillboardedRadioButton;
  @FXML private RadioButton drapedFlatRadioButton;
  private Surface surface;
  private Point surfaceRelatedPoint;
  private Point sceneRelatedPoint;
  private GraphicsOverlay drapedBillboardedOverlay;
  private GraphicsOverlay drapedFlatOverlay;
  private GraphicsOverlay absoluteOverlay;
  private GraphicsOverlay relativeOverlay;
  private GraphicsOverlay relativeToSceneOverlay;

  public void initialize() {

    try {

      // create a scene and add a basemap to it
      ArcGISScene scene = new ArcGISScene(Basemap.Type.IMAGERY);

      // add the SceneView to the stack pane
      sceneView.setArcGISScene(scene);

      // add base surface for elevation data
      surface = new Surface();
      ArcGISTiledElevationSource elevationSource = new ArcGISTiledElevationSource(
        "https://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer");
      surface.getElevationSources().add(elevationSource);
      scene.setBaseSurface(surface);

      // add a scene layer
      ArcGISSceneLayer sceneLayer = new
        ArcGISSceneLayer("http://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/Buildings_Brest/SceneServer/layers/0");
      scene.getOperationalLayers().add(sceneLayer);

      // add a camera and initial camera position
      sceneView.setViewpointCamera(new Camera(48.3889, -4.4595, 90, 330, 90, 0));

      // create overlays with surface placement types
      drapedBillboardedOverlay = new GraphicsOverlay();
      drapedBillboardedOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.DRAPED_BILLBOARDED);

      drapedFlatOverlay = new GraphicsOverlay();
      drapedFlatOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.DRAPED_FLAT);

      relativeOverlay = new GraphicsOverlay();
      relativeOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.RELATIVE);

      absoluteOverlay = new GraphicsOverlay();
      absoluteOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.ABSOLUTE);

      relativeToSceneOverlay = new GraphicsOverlay();
      relativeToSceneOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.RELATIVE_TO_SCENE);

      // create points for graphic locations
      surfaceRelatedPoint = new Point(-4.4609257, 48.3903965, 70, SpatialReferences.getWgs84());

      sceneRelatedPoint = new Point(-4.4610562, 48.3902727, 70, SpatialReferences.getWgs84());

      // create a red triangle symbol
      SimpleMarkerSymbol triangleSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.TRIANGLE, 0xFFFF0000, 12);

      // create a text symbol for each surface placement type
      TextSymbol drapedBillboardedText =
          new TextSymbol(14, "DRAPED BILLBOARDED", 0xFF0000FF, TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.MIDDLE);
      drapedBillboardedText.setOffsetX(20);

      TextSymbol drapedFlatText =
          new TextSymbol(14, "DRAPED FLAT", 0xFF0000FF, TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.MIDDLE);
      drapedFlatText.setOffsetX(20);

      TextSymbol relativeText =
          new TextSymbol(14, "RELATIVE", 0xFF0000FF, TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.MIDDLE);
      relativeText.setOffsetX(20);

      TextSymbol absoluteText =
          new TextSymbol(14, "ABSOLUTE", 0xFF0000FF, TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.MIDDLE);
      absoluteText.setOffsetX(20);

      TextSymbol relativeToSceneText =
        new TextSymbol(14, "RELATIVE TO SCENE", 0xFF0000FF, TextSymbol.HorizontalAlignment.RIGHT, TextSymbol.VerticalAlignment.MIDDLE);
      relativeToSceneText.setOffsetX(-20);

      // add the point graphic and text graphic to the corresponding graphics overlay
      drapedBillboardedOverlay.getGraphics().add(new Graphic(surfaceRelatedPoint, triangleSymbol));
      drapedBillboardedOverlay.getGraphics().add(new Graphic(surfaceRelatedPoint, drapedBillboardedText));

      drapedFlatOverlay.getGraphics().add(new Graphic(surfaceRelatedPoint, triangleSymbol));
      drapedFlatOverlay.getGraphics().add(new Graphic(surfaceRelatedPoint, drapedFlatText));

      relativeOverlay.getGraphics().add(new Graphic(surfaceRelatedPoint, triangleSymbol));
      relativeOverlay.getGraphics().add(new Graphic(surfaceRelatedPoint, relativeText));

      absoluteOverlay.getGraphics().add(new Graphic(surfaceRelatedPoint, triangleSymbol));
      absoluteOverlay.getGraphics().add(new Graphic(surfaceRelatedPoint, absoluteText));

      relativeToSceneOverlay.getGraphics().add(new Graphic(sceneRelatedPoint, triangleSymbol));
      relativeToSceneOverlay.getGraphics().add(new Graphic(sceneRelatedPoint, relativeToSceneText));

      // add graphics overlays to the scene view
      sceneView.getGraphicsOverlays().addAll(Arrays.asList(drapedBillboardedOverlay, relativeOverlay, absoluteOverlay, relativeToSceneOverlay));

      // link up the ToggleGroup RadioButtons to the corresponding draped overlay
      drapedBillboardedRadioButton.setUserData(drapedBillboardedOverlay);
      drapedFlatRadioButton.setUserData(drapedFlatOverlay);

      // listener for toggles between billboarded and flat draped surface placement modes on the JavaFX ToggleGroup
      toggleGroup.selectedToggleProperty().addListener((observableValue, oldToggle, newToggle) -> {
        if (toggleGroup.getSelectedToggle() != null) {
          sceneView.getGraphicsOverlays().remove(oldToggle.getUserData());
          sceneView.getGraphicsOverlays().add((GraphicsOverlay) toggleGroup.getSelectedToggle().getUserData());
        }
      });

    } catch (Exception e) {
      // on any exception, print the stack trace
      e.printStackTrace();
    }

  }

  /**
   * Sets the Z-Value to the value selected by the JavaFX slider.
   */

  @FXML
  private void changeZValue(){
        // get z-value from slider
        double zValueFromSlider = zValueSlider.getValue();

        // create new points with updated z-value
        Point newSurfaceRelatedPoint = new Point(surfaceRelatedPoint.getX(), surfaceRelatedPoint.getY(), zValueFromSlider, surfaceRelatedPoint.getSpatialReference());
        Point newSceneRelatedPoint = new Point(sceneRelatedPoint.getX(), sceneRelatedPoint.getY(), zValueFromSlider, sceneRelatedPoint.getSpatialReference());

        // update the geometry of each of the existing graphics (both the symbol and label) to the relevant new point
        drapedBillboardedOverlay.getGraphics().get(0).setGeometry(newSurfaceRelatedPoint);
        drapedBillboardedOverlay.getGraphics().get(1).setGeometry(newSurfaceRelatedPoint);

        drapedFlatOverlay.getGraphics().get(0).setGeometry(newSurfaceRelatedPoint);
        drapedFlatOverlay.getGraphics().get(1).setGeometry(newSurfaceRelatedPoint);

        relativeOverlay.getGraphics().get(0).setGeometry(newSurfaceRelatedPoint);
        relativeOverlay.getGraphics().get(1).setGeometry(newSurfaceRelatedPoint);

        absoluteOverlay.getGraphics().get(0).setGeometry(newSurfaceRelatedPoint);
        absoluteOverlay.getGraphics().get(1).setGeometry(newSurfaceRelatedPoint);

        relativeToSceneOverlay.getGraphics().get(0).setGeometry(newSceneRelatedPoint);
        relativeToSceneOverlay.getGraphics().get(1).setGeometry(newSceneRelatedPoint);
  }

  /**
   * Disposes application resources.
   */
  void terminate() {
    if (sceneView != null) {
      sceneView.dispose();
    }
  }
}
