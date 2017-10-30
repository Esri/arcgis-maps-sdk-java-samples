package com.esri.samples.analysis.viewshed_location;

import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;

import org.controlsfx.control.RangeSlider;
import org.controlsfx.control.ToggleSwitch;

import com.esri.arcgisruntime.analysis.LocationViewshed;
import com.esri.arcgisruntime.analysis.Viewshed;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.AnalysisOverlay;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.LayerSceneProperties;
import com.esri.arcgisruntime.mapping.view.SceneView;

public class ViewshedLocationController {

  @FXML private SceneView sceneView;
  @FXML private ToggleSwitch moveWithMouseToggle;
  @FXML private ToggleSwitch visibilityToggle;
  @FXML private ToggleSwitch frustumToggle;
  @FXML private Slider headingSlider;
  @FXML private Slider pitchSlider;
  @FXML private Slider horizontalAngleSlider;
  @FXML private Slider verticalAngleSlider;
  @FXML private RangeSlider distanceSlider;
  @FXML private ColorPicker visibleColorPicker;
  @FXML private ColorPicker obstructedColorPicker;
  @FXML private ColorPicker frustumColorPicker;

  public void initialize() {
    // create a scene and add a basemap to it
    ArcGISScene scene = new ArcGISScene();
    scene.setBasemap(Basemap.createImagery());
    sceneView.setArcGISScene(scene);

    // set the camera
    Camera camera = new Camera(48.4, -4.50, 100.0, 10.0, 70, 0.0);
    sceneView.setViewpointCamera(camera);

    // add base surface for elevation data
    Surface surface = new Surface();
    final String localElevationImageService = "http://scene.arcgis" +
        ".com/arcgis/rest/services/BREST_DTM_1M/ImageServer";
    surface.getElevationSources().add(new ArcGISTiledElevationSource(localElevationImageService));
    scene.setBaseSurface(surface);

    // add a scene layer
    final String buildings = "http://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/Buildings_Brest/SceneServer/layers/0";
    ArcGISSceneLayer sceneLayer = new ArcGISSceneLayer(buildings);
    scene.getOperationalLayers().add(sceneLayer);

    // create a viewshed from the camera
    Point location = new Point(-4.50, 48.4,100.0);
    LocationViewshed viewshed = new LocationViewshed(location, 10.0, 70.0, 90.0, 30.0, 1.0, 500.0,
        LayerSceneProperties.SurfacePlacement.RELATIVE);

    // create an analysis overlay to add the viewshed to the scene view
    AnalysisOverlay analysisOverlay = new AnalysisOverlay();
    analysisOverlay.getAnalyses().add(viewshed);
    sceneView.getAnalysisOverlays().add(analysisOverlay);

    // toggle visibility
    visibilityToggle.selectedProperty().addListener(e -> viewshed.setVisible(visibilityToggle.isSelected()));
    // TODO: toggle frustum
    // heading slider
    headingSlider.valueProperty().addListener(e -> viewshed.setHeading(headingSlider.getValue()));
    // pitch slider
    pitchSlider.valueProperty().addListener(e -> viewshed.setPitch(pitchSlider.getValue()));
    // horizontal angle slider
    horizontalAngleSlider.valueProperty().addListener(e -> viewshed.setHorizontalAngle(horizontalAngleSlider.getValue()));
    // vertical angle slider
    verticalAngleSlider.valueProperty().addListener(e -> viewshed.setVerticalAngle(verticalAngleSlider
        .getValue()));
    // distance slider
    distanceSlider.lowValueProperty().addListener(e -> viewshed.setMinDistance(distanceSlider.getLowValue()));
    distanceSlider.highValueProperty().addListener(e -> viewshed.setMaxDistance(distanceSlider.getHighValue()));
    // TODO: color pickers
  }

  /**
   * Stops the animation and disposes of application resources.
   */
  void terminate() {

    if (sceneView != null) {
      sceneView.dispose();
    }
  }

}
