package com.esri.samples.analysis.viewshed_location;

import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

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

    // set the camera
    Camera camera = new Camera(location, 200.0, 20.0, 70.0, 0.0);
    sceneView.setViewpointCamera(camera);

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
    // colors
    visibleColorPicker.valueProperty().addListener(e -> Viewshed.setVisibleColor(colorToInt(visibleColorPicker
        .getValue())));
    obstructedColorPicker.valueProperty().addListener(e -> Viewshed.setObstructedColor(colorToInt(obstructedColorPicker
        .getValue())));
    frustumColorPicker.valueProperty().addListener(e -> Viewshed.setFrustumOutlineColor(colorToInt(frustumColorPicker
        .getValue())));
  }

  /**
   * Parses a Color into an ARGB "packed" integer.
   *
   * @param c color
   * @return packed integer representation of the color
   */
  private int colorToInt(Color c) {
    String hex = String.format("0x%02X%02X%02X%02X",
        (int)(c.getOpacity() * 255),
        (int)(c.getRed() * 255),
        (int)(c.getGreen() * 255),
        (int)(c.getBlue() * 255)
    );
    return Long.decode(hex).intValue();
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
