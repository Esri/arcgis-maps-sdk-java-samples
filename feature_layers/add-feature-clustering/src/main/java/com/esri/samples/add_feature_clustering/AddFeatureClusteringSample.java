/*
 * Copyright 2024 Esri.
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

package com.esri.samples.add_feature_clustering;

import java.util.ArrayList;
import java.util.List;

import com.esri.arcgisruntime.arcgisservices.LabelDefinition;
import com.esri.arcgisruntime.arcgisservices.LabelingPlacement;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.labeling.SimpleLabelExpression;
import com.esri.arcgisruntime.mapping.popup.PopupDefinition;
import com.esri.arcgisruntime.mapping.reduction.AggregateField;
import com.esri.arcgisruntime.mapping.reduction.AggregateStatisticType;
import com.esri.arcgisruntime.mapping.reduction.ClusteringFeatureReduction;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.symbology.ClassBreaksRenderer;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.MapView;

public class AddFeatureClusteringSample extends Application {

  private ArcGISMap mMap;
  private CheckBox mDisplayLabelsCheckbox;
  private Label mClusterRadiusLabel;
  private Slider mClusterRadiusSlider;
  private Label mMaxScaleLabel;
  private Slider mMaxScaleSlider;
  private Label mCurrentMapScaleLabel;
  private FeatureLayer mLayer;

  ClusteringFeatureReduction mClusteringFeatureReduction;

  private MapView mapView;
  private PortalItem portalItem; // keep loadable in scope to avoid garbage collection

  private final int DEFAULT_CLUSTER_R = 60;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Feature Clustering Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a portal and portal item, using the portal and item ID
      var portal = new Portal("https://www.arcgis.com/");
      portalItem = new PortalItem(portal, "aa44e79a4836413c89908e1afdace2ea");

      mMap = new ArcGISMap(portalItem);

      // Get the Zurich buildings feature layer once the map has finished loading
      mMap.addDoneLoadingListener(() -> {
        if (mMap.getLoadStatus() == LoadStatus.LOADED) {
          LayerList l = mMap.getOperationalLayers();
          if (l.isEmpty())
            return;
          mLayer = (FeatureLayer) mMap.getOperationalLayers().get(0);

          // set up the user interface
          VBox vBox = controlsVBox();

          // add the map view to the stack pane
          stackPane.getChildren().addAll(mapView, vBox);
          StackPane.setAlignment(vBox, Pos.TOP_LEFT);
          StackPane.setMargin(vBox, new Insets(10, 0, 0, 10));

          // Add a class break for each intended value range and define a symbol to display for features in that range.
          // In this case, the average building height ranges from 0 to 8 stories.
          // For each cluster of features with a given average building height, a symbol is defined with a specified
          // color.
          List<ClassBreaksRenderer.ClassBreak> classBreaks = new ArrayList<ClassBreaksRenderer.ClassBreak>();
          classBreaks.add(new ClassBreaksRenderer.ClassBreak("0", "0", 0.0, 1.0,
              new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.rgb(4, 251, 255), 8.0f)));
          classBreaks.add(new ClassBreaksRenderer.ClassBreak("1", "1", 1.0, 2.0,
              new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.rgb(44, 211, 255), 8.0f)));
          classBreaks.add(new ClassBreaksRenderer.ClassBreak("2", "2", 2.0, 3.0,
              new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.rgb(74, 181, 255), 8.0f)));
          classBreaks.add(new ClassBreaksRenderer.ClassBreak("3", "3", 3.0, 4.0,
              new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.rgb(120, 135, 255), 8.0f)));
          classBreaks.add(new ClassBreaksRenderer.ClassBreak("4", "4", 4.0, 5.0,
              new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.rgb(165, 90, 255), 8.0f)));
          classBreaks.add(new ClassBreaksRenderer.ClassBreak("6", "6", 6.0, 7.0,
              new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.rgb(194, 61, 255), 8.0f)));
          classBreaks.add(new ClassBreaksRenderer.ClassBreak("5", "5", 5.0, 6.0,
              new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.rgb(224, 31, 255), 8.0f)));
          classBreaks.add(new ClassBreaksRenderer.ClassBreak("7", "7", 7.0, 8.0,
              new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.rgb(254, 1, 255), 8.0f)));

          // Create a class breaks renderer to apply to the custom feature reduction.
          // Define the field to use for the class breaks renderer.
          // Note that this field name must match the name of an aggregate field contained in the clustering feature
          // reduction's aggregate fields property.
          ClassBreaksRenderer classBreaksRenderer = new ClassBreaksRenderer("Average Building Height", classBreaks);

          // Define a default symbol to use for features that do not fall within any of the ranges defined by the
          // class breaks.
          classBreaksRenderer.setDefaultSymbol(new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.PINK, 8));

          // Create a new clustering feature reduction using the class breaks renderer.
          mClusteringFeatureReduction = new ClusteringFeatureReduction(classBreaksRenderer);

          // Set the feature reduction's aggregate fields. Note that the field names must match the names of fields
          // in the feature layer's dataset.
          // The aggregate fields summarize values based on the defined aggregate statistic type.
          mClusteringFeatureReduction.getAggregateFields().add(
              new AggregateField("Total Residential Buildings", "Residential_Buildings", AggregateStatisticType.SUM));

          mClusteringFeatureReduction.getAggregateFields().add(
              new AggregateField("Average Building Height", "Most_common_number_of_storeys",
                  AggregateStatisticType.MODE));

          // Enable the feature reduction.
          mClusteringFeatureReduction.setEnabled(true);

          // Set the popup definition for the custom feature reduction.
          mClusteringFeatureReduction.setPopupDefinition(new PopupDefinition(mClusteringFeatureReduction));

          // Set values for the feature reduction's cluster minimum and maximum symbol sizes.
          // Note that the default values for Max and Min symbol size are 70 and 12 respectively.
          mClusteringFeatureReduction.setMinSymbolSize(5.0);
          mClusteringFeatureReduction.setMaxSymbolSize(90.0);

          // Set the feature reduction for the layer.
          mLayer.setFeatureReduction(mClusteringFeatureReduction);

          // Set initial slider values.
          // Note that the default value for cluster radius is 60.
          // Increasing the cluster radius increases the number of features that are grouped together into a cluster.
          mClusteringFeatureReduction.setRadius(DEFAULT_CLUSTER_R);

          // Note that the default value for max scale is 0.
          // The max scale value is the maximum scale at which clustering is applied.
          mClusteringFeatureReduction.setMaxScale(0.0);
        }
      });

      // create a map view and set the map to it
      mapView = new MapView();
      mapView.setMap(mMap);

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Creates a UI with three buttons and a label.
   *
   * @return a vBox populated with buttons and labels
   */
  private VBox controlsVBox() {

    Label label = new Label("Clustering properties");
    label.setStyle("-fx-font-weight: bold");
    label.setFont(new Font(14));

    final int MIN_WIDTH = 100;
    final int MAX_WIDTH = 300;
    final int SPACING = 6;

    HBox displayHBox = new HBox(SPACING);
    Label label1 = new Label("Display Labels");
    label1.setMinWidth(MIN_WIDTH);
    // create checkbox to toggle the showing of cluster count
    mDisplayLabelsCheckbox = new CheckBox();
    mDisplayLabelsCheckbox.setSelected(false);
    mDisplayLabelsCheckbox.setTextFill(Color.WHITE);
    mDisplayLabelsCheckbox.selectedProperty().addListener(o -> {
      if (mDisplayLabelsCheckbox.isSelected()) {
        SimpleLabelExpression simpleLabelExpression = new SimpleLabelExpression("[cluster_count]");
        TextSymbol textSymbol = new TextSymbol(15.0f, "", Color.BLUE, TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
        textSymbol.setFontWeight(TextSymbol.FontWeight.BOLD);
        LabelDefinition labelDefinition = new LabelDefinition(simpleLabelExpression, textSymbol);
        labelDefinition.setPlacement(LabelingPlacement.POINT_CENTER_CENTER);
        mClusteringFeatureReduction.getLabelDefinitions().add(labelDefinition);
      }
      else {
        mClusteringFeatureReduction.getLabelDefinitions().clear();
      }
    });
    displayHBox.getChildren().addAll(label1, mDisplayLabelsCheckbox);

    HBox clusterRadiusHBox = new HBox(SPACING);
    mClusterRadiusLabel = new Label("Cluster radius: " + DEFAULT_CLUSTER_R);
    mClusterRadiusLabel.setMinWidth(MIN_WIDTH);
    mClusterRadiusSlider = new Slider();
    mClusterRadiusSlider.setValue(DEFAULT_CLUSTER_R);

    // create a slider for adjusting cluster radius
    mClusterRadiusSlider = new Slider(30, 85, DEFAULT_CLUSTER_R);
    mClusterRadiusSlider.setMaxWidth(MAX_WIDTH);
    // add a listener to the slider's value property to set the cluster radius
    mClusterRadiusSlider.valueProperty().addListener(o -> {
      mClusterRadiusLabel.setText(String.valueOf("Cluster radius: " + (int) mClusterRadiusSlider.getValue()));
      mClusteringFeatureReduction.setRadius(mClusterRadiusSlider.getValue());
    });
    clusterRadiusHBox.getChildren().addAll(mClusterRadiusLabel, mClusterRadiusSlider);

    final int DEFAULT_SCALE_WIDTH = 0;
    final int MAX_SCALE_WIDTH = 150000;
    HBox maxScaleHBox = new HBox(SPACING);
    mMaxScaleLabel = new Label("MaxScale: " + DEFAULT_SCALE_WIDTH);
    mMaxScaleLabel.setMinWidth(MIN_WIDTH);
    // create a slider for adjusting max scale
    mMaxScaleSlider = new Slider(0, MAX_SCALE_WIDTH, DEFAULT_SCALE_WIDTH);
    mMaxScaleSlider.setMaxWidth(MAX_SCALE_WIDTH);
    // add a listener to the slider's value property to set the max scale
    mMaxScaleSlider.valueProperty().addListener(o -> {
      mMaxScaleLabel.setText(String.valueOf("MaxScale: " + (int) mMaxScaleSlider.getValue()));
      mClusteringFeatureReduction.setMaxScale(mMaxScaleSlider.getValue());
    });
    maxScaleHBox.getChildren().addAll(mMaxScaleLabel, mMaxScaleSlider);

    // show current map scale in a label within the control panel
    mCurrentMapScaleLabel = new Label("Scale: 1:" + mapView.getMapScale());

    // listen for map scale changes and update the label
    mapView.mapScaleProperty().addListener((observable, oldValue, newValue) ->
        mCurrentMapScaleLabel.setText("Scale: 1:" + Math.round((double) newValue)));

    VBox controlsVBox = new VBox(10);
    controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(255,255,255,0.9)"),
        CornerRadii.EMPTY,
        Insets.EMPTY)));
    controlsVBox.setPadding(new Insets(10.0));
    controlsVBox.setMaxSize(MAX_WIDTH, MIN_WIDTH);
    controlsVBox.getStyleClass().add("panel-region");
    controlsVBox.getChildren().addAll(label, displayHBox, clusterRadiusHBox, maxScaleHBox, mCurrentMapScaleLabel);

    return controlsVBox;
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

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
