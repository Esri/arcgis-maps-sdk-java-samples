/* Copyright 2015 Esri.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
limitations under the License.  */

package com.esri.sampleviewer.samples.map;


import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.BasemapType;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class ChangeBasemap extends Application {

    private MapView mapView;
    private Map map;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // create a border pane
        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane);

        // size the stage and add a title
        stage.setTitle("Change Basemaps Sample");
        stage.setWidth(800);
        stage.setHeight(600);

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(5, 5, 5, 5));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #2196F3");

        Label label = new Label("Choose Basemap: ");
        label.setTextFill(Color.web("#212121"));

        ComboBox basemapComboBox = new ComboBox();
        basemapComboBox.getItems().addAll("Streets", "Topographic", "Gray", "Oceans");
        basemapComboBox.setPromptText("Choose Basemap");
        basemapComboBox.setEditable(true);
        basemapComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String value, String name) {
                // handle combo box item selection
                switch(name){
                    case "Streets":
                        // create a map with Streets Basemap
                        map.setBasemap(Basemap.createStreets());
                        break;
                    case "Topographic":
                        // create a map with Topographic Basemap
                        map.setBasemap(Basemap.createTopographic());
                        break;
                    case "Gray":
                        // create a map with Gray Basemap
                        map.setBasemap(Basemap.createLightGrayCanvas());
                        break;
                    case "Oceans":
                        // create a map with Oceans Basemap
                        map.setBasemap(Basemap.createOceans());
                        break;
                    default:
                        System.out.println(name);
                }
            }
        });

        hbox.getChildren().addAll(label, basemapComboBox);

        //make a new map using National Geographic mapping centred over East Scotland at zoom level 10.
        map = new Map(BasemapType.TOPOGRAPHIC, 56.075844,-2.681572, 10);

        // create the MapView JavaFX control and assign its map
        mapView = new MapView();
        mapView.setMap(map);

        borderPane.setTop(hbox);
        borderPane.setCenter(mapView);

        stage.setScene(scene);
        stage.show();

    }

    @Override
    public void stop() throws Exception {
        // release resources when the application closes
        mapView.dispose();
        map.dispose();
        System.exit(0);

    };

}
