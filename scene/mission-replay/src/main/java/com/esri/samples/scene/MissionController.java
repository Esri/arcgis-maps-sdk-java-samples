package com.esri.samples.scene;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.*;
import com.esri.arcgisruntime.mapping.view.*;
import com.esri.arcgisruntime.symbology.*;

public class MissionController {
  // injected elements from fxml
  @FXML private CameraModel cameraModel;
  @FXML private AnimationModel animationModel;
  @FXML private PlaneModel planeModel;
  @FXML private SceneView sceneView;
  @FXML private MapView mapView;
  @FXML private ComboBox<String> missionSelector;
  @FXML private Slider progressSlider;
  @FXML private ToggleButton playButton;
  @FXML private ToggleButton followButton;
  @FXML private Slider zoomSlider;
  @FXML private Slider angleSlider;
  @FXML private Slider speedSlider;

  private Camera camera;
  private GraphicsOverlay sceneOverlay;
  private GraphicsOverlay mapOverlay;
  private Timeline timer;
  private Graphic plane3D;
  private Graphic plane2D;
  private List<Map<String, Object>> missionData;
  private Graphic routeGraphic;

  private static final String ELEVATION_IMAGE_SERVICE =
      "http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer";

  /**
   * Called after FXML loads. Sets up scene and map and configures property bindings.
   */
  public void initialize() {

    try {
      // create a scene
      ArcGISScene scene = new ArcGISScene(Basemap.createImagery());
      sceneView.setArcGISScene(scene);

      // set initial camera viewpoint
      camera = new Camera(28.4, 83.9, 10010.0, 10.0, 80.0, 300.0);
      sceneView.setViewpointCamera(camera);

      // add elevation data
      Surface surface = new Surface();
      surface.getElevationSources().add(new ArcGISTiledElevationSource(ELEVATION_IMAGE_SERVICE));
      scene.setBaseSurface(surface);

      // create a graphics overlay for the scene
      sceneOverlay = new GraphicsOverlay();
      sceneOverlay.setRenderer(new SimpleRenderer());
      sceneOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.ABSOLUTE);
      sceneView.getGraphicsOverlays().add(sceneOverlay);

      // set up mini map
      ArcGISMap map = new ArcGISMap(Basemap.createImagery());
      mapView.setMap(map);
      mapView.setViewpointScaleAsync(10000);
      mapOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(mapOverlay);

      // create 2D and 3D graphics
      create2DPlane();
      create3DPlane();

      // setup animation to render a new frame every 20 ms by default
      timer = new Timeline(new KeyFrame(Duration.millis(20), e -> animate(animationModel.nextKeyframe())));
      timer.setCycleCount(Animation.INDEFINITE);

      // bind camera slider controls to camera model properties
      cameraModel.distanceProperty().bind(zoomSlider.valueProperty());
      cameraModel.angleProperty().bind(angleSlider.valueProperty());

      // bind animation properties
      progressSlider.maxProperty().bind(animationModel.framesProperty());
      progressSlider.valueProperty().bindBidirectional(animationModel.keyframeProperty());
      timer.rateProperty().bind(speedSlider.valueProperty());

      // bind button properties
      followButton.disableProperty().bind(Bindings.not(playButton.selectedProperty()));
      followButton.textProperty().bind(Bindings.createStringBinding(() -> followButton.isSelected() ?
          "Free cam" : "Follow", followButton.selectedProperty()));
      playButton.textProperty().bind(Bindings.createStringBinding(() -> playButton.isSelected() ?
          "Stop" : "Play", playButton.selectedProperty()));

      // disable scroll zoom in follow mode
      sceneView.addEventFilter(ScrollEvent.ANY, e -> {
        if (!followButton.isDisabled() && cameraModel.getFollow()) {
          e.consume();
        }
      });

    } catch (Exception e) {
      // on any exception, print the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Creates a 3D graphic representing the plane in the scene. Adds the graphic to the scene view's graphics overlay.
   * @throws URISyntaxException if model cannot be loaded
   */
  private void create3DPlane() throws URISyntaxException {
    // load the plane's 3D model symbol
    String modelURI = Paths.get(getClass().getResource("/SkyCrane/SkyCrane.lwo").toURI()).toString();
    ModelSceneSymbol plane3DSymbol = new ModelSceneSymbol(modelURI, 0.01);
    plane3DSymbol.setHeading(180); // correct the symbol's orientation to match the graphic's orientation
    plane3DSymbol.loadAsync();

    // create renderer to handle updating plane rotation using the graphics card
    SimpleRenderer renderer3D = new SimpleRenderer();
    Renderer.SceneProperties renderProperties = renderer3D.getSceneProperties();
    renderProperties.setHeadingExpression("HEADING");
    renderProperties.setPitchExpression("PITCH");
    renderProperties.setRollExpression("ROLL");
    sceneOverlay.setRenderer(renderer3D);

    // add the graphic to the scene view graphic overlay
    plane3D = new Graphic(new Point(0, 0, 0, SpatialReferences.getWgs84()), plane3DSymbol);
    sceneOverlay.getGraphics().add(plane3D);
  }

  /**
   * Creates a 2D graphic representing the plane on the mini map. Adds the graphic to the map view's graphics overlay.
   */
  private void create2DPlane() {
    // create a red (0xFFFF0000) triangle symbol to represent the plane on the mini map
    SimpleMarkerSymbol plane2DSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.TRIANGLE, 0xFFFF0000, 10);

    // create renderer to handle updating plane heading using the graphics card
    SimpleRenderer renderer2D = new SimpleRenderer();
    renderer2D.setRotationExpression("[ANGLE]");
    mapOverlay.setRenderer(renderer2D);

    // create a graphic with the symbol and add it to the graphics overlay
    plane2D = new Graphic(new Point(0, 0, SpatialReferences.getWgs84()), plane2DSymbol);
    plane2D.getAttributes().put("ANGLE", 0f);
    mapOverlay.getGraphics().add(plane2D);
  }

  /**
   * Called when a new mission is selected from the dropdown.
   */
  @FXML
  private void changeMission() {

    // clear previous mission data
    missionData = new ArrayList<>();
    if (routeGraphic != null) mapOverlay.getGraphics().remove(routeGraphic);

    // get mission data
    String mission = missionSelector.getSelectionModel().getSelectedItem();
    missionData = getMissionData(mission);
    animationModel.setFrames(missionData.size());
    animationModel.setKeyframe(0);

    // draw mission route on mini map
    PointCollection points = new PointCollection(SpatialReferences.getWgs84());
    points.addAll(missionData.stream().map(m -> (Point) m.get("position")).collect(Collectors.toList()));
    SimpleLineSymbol routeSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF0000, 2);
    Polyline route = new Polyline(points);
    routeGraphic = new Graphic(route, routeSymbol);
    mapOverlay.getGraphics().add(0, routeGraphic);

    // show initial keyframe
    animate(0);
    timer.stop();

    // enable play button
    playButton.setSelected(false);
    playButton.setDisable(false);
  }

  /**
   * Loads the mission data from a .csv file into memory.
   *
   * @param mission .csv file name containing the mission data
   * @return ordered list of mapped key value pairs representing coordinates and rotation parameters for each step of
   * the mission
   */
  private List<Map<String, Object>> getMissionData(String mission) {

    // open a file reader to the mission file that automatically closes after read
    try (BufferedReader missionFile = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/" + mission)))) {
      List<Map<String, Object>> missionData = new ArrayList<>();
      missionFile.lines()
          .map(l -> l.split(","))
          .map(l -> {
            // create a map of parameters (ordinates) to values
            Map<String, Object> ordinates = new HashMap<>();
            ordinates.put("position", new Point(Double.valueOf(l[0]), Double.valueOf(l[1]), Double
                .valueOf(l[2]), SpatialReferences.getWgs84()));
            ordinates.put("heading", Double.valueOf(l[3]));
            ordinates.put("pitch", Double.valueOf(l[4]));
            ordinates.put("roll", Double.valueOf(l[5]));
            return ordinates;
          })
          .collect(Collectors.toCollection(() -> missionData));

      return missionData;
    } catch (IOException e) {
      e.printStackTrace();
    }
    throw new RuntimeException("Error reading mission file: " + mission);
  }


  /**
   * Switches the animation on or off depending on the toggled state of the play button.
   */
  @FXML
  private void togglePlay() {

    if (playButton.isSelected()) {
      timer.play();
    } else {
      timer.stop();
    }
  }

  /**
   * Switches the toggle mode on the camera model when the toggle button is clicked.
   */
  @FXML
  private void toggleFollow() {
    if (followButton.isSelected()) plane2D.getAttributes().put("ANGLE", 0f);
    cameraModel.setFollow(followButton.isSelected());
  }

  /**
   * Sets the map view scale to zoom in one exponential step.
   */
  @FXML
  private void zoomInMap() {
    mapView.setViewpointScaleAsync(mapView.getMapScale() / 25);
  }

  /**
   * Sets the map view scale to zoom out one exponential step.
   */
  @FXML
  private void zoomOutMap() {
    mapView.setViewpointScaleAsync(mapView.getMapScale() * 25);
  }

  /**
   * Animates a single keyframe corresponding to the index in the mission data profile. Updates the position and
   * rotation of the 2D/3D plane graphic and sets the camera viewpoint.
   *
   * @param keyframe index in mission data to show
   */
  private void animate(int keyframe) {
    // get the next position
    Map<String, Object> datum = missionData.get(keyframe);
    Point position = (Point) datum.get("position");

    // update the model bean with new parameters
    planeModel.setAltitude(position.getZ());
    planeModel.setHeading((double) datum.get("heading"));
    planeModel.setPitch((double) datum.get("pitch"));
    planeModel.setRoll((double) datum.get("roll"));

    // move 2D plane to next position
    plane2D.setGeometry(position);

    // move 3D plane to next position
    plane3D.setGeometry(position);
    // update attribute expressions to immediately update rotation
    plane3D.getAttributes().put("HEADING", planeModel.getHeading());
    plane3D.getAttributes().put("PITCH", planeModel.getPitch());
    plane3D.getAttributes().put("ROLL", planeModel.getRoll());

    if (cameraModel.getFollow()) {
      // move the camera to follow the plane
      camera = new Camera(position, cameraModel.getDistance(), planeModel.getHeading(), cameraModel.getAngle(),
          planeModel.getRoll());
      sceneView.setViewpointCamera(camera);

      // rotate the map view about the direction of motion
      mapView.setViewpoint(new Viewpoint(position, mapView.getMapScale(), 360 + planeModel.getHeading()));
    } else {
      plane2D.getAttributes().put("ANGLE", -270 + planeModel.getHeading());
    }
  }

  /**
   * Stops the animation and disposes of application resources.
   */
  protected void terminate() {
    timer.stop();
    if (sceneView != null) sceneView.dispose();
    if (mapView != null) mapView.dispose();
  }
}
