package com.esri.samples.ogc.browse_wfs_layers;

import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.DatumTransformation;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.ogc.wfs.WfsFeatureTable;
import com.esri.arcgisruntime.ogc.wfs.WfsLayerInfo;
import com.esri.arcgisruntime.ogc.wfs.WfsService;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.List;

public class BrowseWfsLayersSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and JavaFX app scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

    // set title, size, and add JavaFX scene to stage
    stage.setTitle("Browse WFS Layers");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox controlsVBox = new VBox(6);
    controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY, Insets.EMPTY)));
    controlsVBox.setPadding(new Insets(10.0));
    controlsVBox.setMaxSize(220, 240);
    controlsVBox.getStyleClass().add("panel-region");

    CheckBox checkBox = new CheckBox("Swap coordinate order");

    // create a list to hold the names of the bookmarks
    ListView<WfsLayerInfo> wfsLayerNamesListView = new ListView<>();
    wfsLayerNamesListView.setMaxHeight(160);

    // create an ArcGISMap with topographic basemap and set it to the map view
    ArcGISMap map = new ArcGISMap(Basemap.createImagery());
    mapView = new MapView();
    mapView.setMap(map);

    // URL to the WFS service
    String serviceUrl = "https://dservices2.arcgis.com/ZQgQTuoyBrtmoGdP/arcgis/services/Seattle_Downtown_Features/WFSServer?service=wfs&request=getcapabilities";

    // create a WFS service
    WfsService wfsService = new WfsService(serviceUrl);
    wfsService.loadAsync();
    wfsService.addDoneLoadingListener(() -> {
      if (wfsService.getLoadStatus() == LoadStatus.LOADED) {
        // add the list of WFS layers to the list view
      List<WfsLayerInfo> wfsLayerInfos = wfsService.getServiceInfo().getLayerInfos();

      for (WfsLayerInfo layerInfo : wfsLayerInfos){
        wfsLayerNamesListView.getItems().add(layerInfo);
//
      }
    } else {
      Alert alert = new Alert(Alert.AlertType.ERROR, "WFS Service Failed to Load!");
      alert.show();
    }




        // when user clicks on a bookmark change to that location
//        wfsLayerNamesListView.getSelectionModel().selectedItemProperty().addListener((ov, old_val, new_val) -> {
//          int index = wfsLayerNamesListView.getSelectionModel().getSelectedIndex();
//          System.out.println(index);
////
//        });

        // create a cell factory to show the layer names in the list view
//        Callback<ListView<Layer>, ListCell<Layer>> cellFactory = list -> new ListCell<WfsLayerInfo>() {
//
//          @Override
//          protected void updateItem(WfsLayerInfo wfsLayerInfo, boolean bln) {
//
//            super.updateItem(wfsLayerInfo, bln);
//            if (wfsLayerInfo == null) {
//
//              List<WfsLayerInfo> wfsLayerInfos = wfsService.getServiceInfo().getLayerInfos();
//              System.out.println(wfsLayerInfos.size());
//              for (WfsLayerInfo layerInfo : wfsLayerInfos) {
//                String fullNameOfWfsLayer = layerInfo.getName();
//                String[] split = fullNameOfWfsLayer.split(":");
//                String smallName = split[1];
//                System.out.println(fullNameOfWfsLayer);
//                setText(fullNameOfWfsLayer);
//
//              }
//
//            } else {
//              setText(null);
//            }
//          }
//
//        };

      wfsLayerNamesListView.setCellFactory(list -> new ListCell<>() {

        @Override
        protected void updateItem(WfsLayerInfo wfsLayerInfo, boolean bln) {
          super.updateItem(wfsLayerInfo, bln);
          if (wfsLayerInfo != null) {
            String fullNameOfWfsLayer = wfsLayerInfo.getName();
            String[] split = fullNameOfWfsLayer.split(":");
            String smallName = split[1];
            System.out.println(fullNameOfWfsLayer);

            setText(smallName);
          }
        }
      });




    });

    // create a FeatureTable from the WFS service URL and name of the layer
//    WfsFeatureTable wfsFeatureTable = new WfsFeatureTable(serviceUrl);

    // set the feature request mode to manual. The table must be manually populated as panning and zooming won't request features automatically.
//    wfsFeatureTable.setFeatureRequestMode(ServiceFeatureTable.FeatureRequestMode.MANUAL_CACHE);
//
//    // create a feature layer to visualize the WFS features
//    FeatureLayer wfsFeatureLayer = new FeatureLayer(wfsFeatureTable);
//
//    // apply a renderer to the feature layer
//    SimpleRenderer renderer = new SimpleRenderer(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF0000, 3));
//    wfsFeatureLayer.setRenderer(renderer);
//
//    // add the layer to the map's operational layers
//    map.getOperationalLayers().add(wfsFeatureLayer);


    controlsVBox.getChildren().addAll(wfsLayerNamesListView, checkBox);

    // add the mapview to the stackpane
    stackPane.getChildren().addAll(mapView, controlsVBox);
    StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
    StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));


  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {
    // release resources when the application closes
    if (mapView != null) {
      mapView.dispose();
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
