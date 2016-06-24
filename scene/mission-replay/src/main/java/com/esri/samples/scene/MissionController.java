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

    private static final String ELEVATION_IMAGE_SERVICE =
            "http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer";

    public void initialize() {

        try {
            // create a scene
            ArcGISScene scene = new ArcGISScene(Basemap.createImagery());
            sceneView.setArcGISScene(scene);

            camera = new Camera(28.4, 83.9, 10010.0, 10.0, 80.0, 300.0);
            sceneView.setViewpointCamera(camera);

            // add elevation data
            Surface surface = new Surface();
            surface.getElevationSources().add(new ArcGISTiledElevationSource(ELEVATION_IMAGE_SERVICE));
            scene.setBaseSurface(surface);

            // create a graphics overlay
            sceneOverlay = new GraphicsOverlay();
            sceneOverlay.setRenderer(new SimpleRenderer());
            sceneOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.ABSOLUTE);
            sceneView.getGraphicsOverlays().add(sceneOverlay);

            // set up map
            ArcGISMap map = new ArcGISMap(Basemap.createImagery());
            mapView.setMap(map);
            mapView.setViewpointScaleAsync(1000);
            mapOverlay = new GraphicsOverlay();
            mapView.getGraphicsOverlays().add(mapOverlay);

            // create 2D and 3D graphics
            create2DPlane();
            create3DPlane();

            // setup animation
            timer = new Timeline(new KeyFrame(Duration.millis(20), e -> animate(animationModel.nextKeyframe())));
            timer.setCycleCount(Animation.INDEFINITE);

            // bindings
            cameraModel.distanceProperty().bind(zoomSlider.valueProperty());
            cameraModel.angleProperty().bind(angleSlider.valueProperty());
            progressSlider.valueProperty().bind(animationModel.progressProperty());
            progressSlider.maxProperty().bind(animationModel.framesProperty());
            timer.rateProperty().bind(speedSlider.valueProperty());
            followButton.disableProperty().bind(Bindings.not(playButton.selectedProperty()));
            followButton.textProperty().bind(Bindings.createStringBinding(() -> followButton.isSelected() ?
                "Free cam" : "Follow", followButton.selectedProperty()));
            playButton.textProperty().bind(Bindings.createStringBinding(() -> playButton.isSelected() ?
                "Stop" : "Play", followButton.selectedProperty()));

            // disable scroll zoom in follow mode
            sceneView.addEventFilter(ScrollEvent.ANY, e -> {
                if (!followButton.isDisabled() && cameraModel.getFollow()) {
                    e.consume();
                }
            });

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void create3DPlane() throws URISyntaxException {
        String modelURI = Paths.get(getClass().getResource("/SkyCrane/SkyCrane.lwo").toURI()).toString();
        ModelSceneSymbol plane3DSymbol = new ModelSceneSymbol(modelURI, 0.01);
        plane3DSymbol.setHeading(-180);
        plane3DSymbol.loadAsync();
        plane3D = new Graphic(new Point(0, 0, 0, SpatialReferences.getWgs84()), plane3DSymbol);

        // create renderer to handle updating plane rotation
        SimpleRenderer renderer3D = new SimpleRenderer();
        Renderer.SceneProperties renderProperties = renderer3D.getSceneProperties();
        renderProperties.setHeadingExpression("HEADING");
        renderProperties.setPitchExpression("PITCH");
        renderProperties.setRollExpression("ROLL");

        sceneOverlay.setRenderer(renderer3D);
        sceneOverlay.getGraphics().add(plane3D);
    }

    private void create2DPlane() {
        SimpleMarkerSymbol plane2DSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.TRIANGLE, 0xFFFF0000, 10);
        plane2D = new Graphic(new Point(0, 0, SpatialReferences.getWgs84()), plane2DSymbol);
        mapOverlay.getGraphics().add(plane2D);
    }

    @FXML
    private void changeMission() {

        // clear previous mission
        missionData = new ArrayList<>();

        // get mission data
        String mission = missionSelector.getSelectionModel().getSelectedItem();
        missionData = getMissionData(mission);
        animationModel.setFrames(missionData.size());

        // draw mission route on mini map
        PointCollection points = new PointCollection(SpatialReferences.getWgs84());
        points.addAll(missionData.stream().map(m -> (Point) m.get("position")).collect(Collectors.toList()));
        SimpleLineSymbol routeSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF0000, 2);
        Polyline route = new Polyline(points);
        Graphic routeGraphic = new Graphic(route, routeSymbol);
        mapOverlay.getGraphics().add(routeGraphic);

        // show initial frame
        animate(0);

        // enable play
        playButton.setDisable(false);
    }

    private List<Map<String, Object>> getMissionData(String mission) {

        try (BufferedReader missionFile = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/" + mission)))) {
            List<Map<String, Object>> missionData = new ArrayList<>();
            missionFile.lines()
               .map(l -> l.split(","))
               .map( l -> {
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
        return new ArrayList<>();
    }

    @FXML
    private void togglePlay() {

        if (playButton.isSelected() && missionData != null) {
            timer.play();
        } else {
            timer.stop();
        }
    }

    @FXML
    private void toggleFollow() {
        cameraModel.setFollow(followButton.isSelected());
    }

    @FXML
    private void zoomInMap() {
        mapView.setViewpointScaleAsync(mapView.getMapScale() / 25);
    }

    @FXML
    private void zoomOutMap() {
        mapView.setViewpointScaleAsync(mapView.getMapScale() * 25);
    }

    private void animate(int frame) {
        Map<String, Object> datum = missionData.get(frame);
        Point position = (Point) datum.get("position");

        //parameters
        planeModel.setAltitude(position.getZ());
        planeModel.setHeading((double) datum.get("heading"));
        planeModel.setPitch((double) datum.get("pitch"));
        planeModel.setRoll((double) datum.get("roll"));

        //2d
        plane2D.setGeometry(position);

        //3d
        plane3D.setGeometry(position);
        plane2D.getAttributes().put("HEADING", planeModel.getHeading());
        plane3D.getAttributes().put("PITCH", planeModel.getPitch());
        plane3D.getAttributes().put("ROLL", planeModel.getRoll());

        if (cameraModel.getFollow()) {
            // 3d camera
            camera = new Camera(position, cameraModel.getDistance(), planeModel.getHeading(), cameraModel.getAngle(),
                planeModel.getRoll());
            sceneView.setViewpointCamera(camera);

            //2d
            mapView.setViewpoint(new Viewpoint(position, mapView.getMapScale(), 360 + planeModel.getHeading()));
        }
    }
}
