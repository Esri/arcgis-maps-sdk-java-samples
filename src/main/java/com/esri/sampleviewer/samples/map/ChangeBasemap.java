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

package main.java.com.esri.sampleviewer.samples.map;

import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.BasemapType;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.MapView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class ChangeBasemap extends Application {

    private MapView mapView;
    private Map map;

    // set the strings for the combo box
    private final String streets = "Streets";
    private final String topo = "Topographic";
    private final String gray = "Gray";
    private final String oceans = "Oceans";

    @Override
    public void start(Stage stage) {
        // create a border pane
        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane);

        // size the stage and add a title
        stage.setTitle("Change Basemaps Sample");
        stage.setWidth(800);
        stage.setHeight(600);

        // create a HBox layout pane
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(5, 5, 5, 5));
        hbox.setSpacing(10);
        // set background primary color blue
        hbox.setStyle("-fx-background-color: #2196F3");
        // create a label
        Label label = new Label("Choose Basemap: ");
        label.setTextFill(Color.web("#FFFFFF"));

        // create a combo box
        ComboBox basemapComboBox = new ComboBox();
        basemapComboBox.getItems().addAll(streets, topo, gray, oceans);
        basemapComboBox.getSelectionModel().select(topo);
        basemapComboBox.setEditable(false);
        // add change listener on combo box
        basemapComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String value, String name) {
                // handle combo box item selection
                switch (name) {
                    case streets:
                        // create a map with Streets Basemap
                        map.setBasemap(Basemap.createStreets());
                        break;
                    case topo:
                        // create a map with Topographic Basemap
                        map.setBasemap(Basemap.createTopographic());
                        break;
                    case gray:
                        // create a map with Gray Basemap
                        map.setBasemap(Basemap.createLightGrayCanvas());
                        break;
                    case oceans:
                        // create a map with Oceans Basemap
                        map.setBasemap(Basemap.createOceans());
                        break;
                    default:
                        System.out.println(name);
                }
            }
        });

        // add lable and combo box to hbox
        hbox.getChildren().addAll(label, basemapComboBox);

        //make a new map using Topographic mapping centred over East Scotland at zoom level 10.
        map = new Map(BasemapType.TOPOGRAPHIC, 56.075844,-2.681572, 10);

        // create the MapView JavaFX control and assign its map
        mapView = new MapView();
        mapView.setMap(map);

        // set the panes to the border
        borderPane.setTop(hbox);
        borderPane.setCenter(mapView);
        // show stage
        stage.setScene(scene);
        stage.show();

    }

    @Override
    public void stop() throws Exception {
        // release resources when the application closes
        mapView.dispose();
        map.dispose();
        System.exit(0);

    }

    public static void main(String[] args) {
        launch(args);
    }

}
