package com.esri.samples.mapview.scale_bar;

import com.esri.arcgisruntime.UnitSystem;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.toolkit.Scalebar;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ScaleBarSample extends Application {

    private MapView mapView;

    private static final double LATITUDE = 64.1405;
    private static final double LONGITUDE = -16.2426;

    @Override
    public void start(Stage stage) throws Exception {

        // create stack pane and application scene
        StackPane stackPane = new StackPane();
        Scene scene = new Scene(stackPane);

        // set title, size and scene to stage
        stage.setTitle("Scale Bar Sample");
        stage.setWidth(800);
        stage.setHeight(700);
        stage.setScene(scene);
        stage.show();

        // create a map view
        mapView = new MapView();
        ArcGISMap map = new ArcGISMap(Basemap.Type.IMAGERY, LATITUDE, LONGITUDE, 16);
        mapView.setMap(map);

        // create a scale bar on the map
        Scalebar scaleBar = new Scalebar(mapView);

        // specify skin style for the scale bar e.g. graduated line
        scaleBar.setSkinStyle(Scalebar.SkinStyle.GRADUATED_LINE);

        // scale bar default units are Metric (metres & kilometres), can set to Imperial (miles and feet)
        scaleBar.setUnitSystem(UnitSystem.IMPERIAL);

        // to enhance visibility of the scale bar, by making background transparent
        Color transparentWhite = new Color(1, 1, 1, 0.7);
        scaleBar.setBackground(new Background(new BackgroundFill(transparentWhite, new CornerRadii(5), Insets.EMPTY)));

        // add the map view and scale bar to stack pane
        stackPane.getChildren().addAll(mapView, scaleBar);

        // set position of scale bar
        stackPane.setAlignment(scaleBar, Pos.BOTTOM_CENTER);
        // give padding to scale bar
        stackPane.setMargin(scaleBar, new Insets(0, 0, 50, 0));

    }

    // Stops and releases all resources used in application
    @Override
    public void stop(){
        if (mapView != null) {
            mapView.dispose();
        }
    }
    // Opens and runs application
    public static void main(String[] args) {

        Application.launch(args);
    }
}
