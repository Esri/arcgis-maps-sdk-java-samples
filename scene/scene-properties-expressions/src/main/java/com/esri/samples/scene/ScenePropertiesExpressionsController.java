package com.esri.samples.scene;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.SimpleMarkerSceneSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

public class ScenePropertiesExpressionsController {
  @FXML private SceneView sceneView;
  @FXML private Slider headingSlider;
  @FXML private Slider pitchSlider;

  public void initialize() {

    // create a scene and add a basemap to it
    ArcGISScene scene = new ArcGISScene();
    scene.setBasemap(Basemap.createImagery());

    // add the SceneView to the stack pane
    sceneView.setArcGISScene(scene);

    // add a camera and initial camera position
    Point point = new Point(83.9, 28.4, 1000, SpatialReferences.getWgs84());
    Camera camera = new Camera(point, 1000, 0, 50, 0);
    sceneView.setViewpointCamera(camera);

    // create a graphics overlay
    GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
    sceneView.getGraphicsOverlays().add(graphicsOverlay);

    // add renderer using rotation expressions
    SimpleRenderer renderer = new SimpleRenderer();
    renderer.getSceneProperties().setHeadingExpression("HEADING");
    renderer.getSceneProperties().setPitchExpression("PITCH");
    graphicsOverlay.setRenderer(renderer);

    // create a red (0xFFFF0000) cone graphic
    SimpleMarkerSceneSymbol coneSymbol = SimpleMarkerSceneSymbol.createCone(0xFFFF0000, 100, 100);
    coneSymbol.setPitch(-90);  // correct symbol's default pitch
    Graphic cone = new Graphic(new Point(83.9, 28.41, 1000, SpatialReferences.getWgs84()), coneSymbol);
    graphicsOverlay.getGraphics().add(cone);

    // bind attribute values to sliders
    headingSlider.valueProperty().addListener(o -> cone.getAttributes().put("HEADING", headingSlider.getValue()));
    pitchSlider.valueProperty().addListener(o -> cone.getAttributes().put("PITCH", pitchSlider.getValue()));
  }

  /**
   * Disposes application resources.
   */
  void terminate() {
    if (sceneView != null) sceneView.dispose();
  }
}
