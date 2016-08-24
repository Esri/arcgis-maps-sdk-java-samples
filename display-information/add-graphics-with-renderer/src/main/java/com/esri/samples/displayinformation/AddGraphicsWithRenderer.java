/*
 * Copyright 2016 Esri.
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

package com.esri.samples.displayinformation;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style;
import com.esri.arcgisruntime.symbology.UniqueValueRenderer;
import com.esri.arcgisruntime.symbology.UniqueValueRenderer.UniqueValue;

public class AddGraphicsWithRenderer extends Application {

  private MapView mapView;
  private GraphicsOverlay graphicsOverlay;

  // colors for symbols
  private static final int PURPLE = 0xFF800080;
  private static final int BLUE = 0xFF0000FF;
  private static final int RED = 0xFFFF0000;
  private static final int GREEN = 0xFF00FF00;

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Add Graphics with Renderer Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a new ArcGISMap with a light grey canvas.
      ArcGISMap map = new ArcGISMap(Basemap.Type.LIGHT_GRAY_CANVAS, 56.075844, -2.681572, 13);

      // set the map to the view
      mapView = new MapView();
      mapView.setMap(map);

      // add the graphics overlay
      graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // set nesting locations
      addNestingLocations();

      // add the map view to stack pane
      stackPane.getChildren().add(mapView);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates Graphics for the nesting location of four different seabirds and
   * adds them to the GraphicsOverlay. A UniqueValueRenderer is used to add
   * Symbols to each of these nesting locations based on bird type.
   */
  private void addNestingLocations() {

    // set spatial reference to be used in the point locations
    SpatialReference wgs84 = SpatialReference.create(4326);

    // create points to represent different bird nest locations
    Point gannet1Loc = new Point(-2.6419183006274025, 56.07737682015417, wgs84);

    Point eider1Loc = new Point(-2.6884384414498004, 56.0626208952164, wgs84);
    Point eider2Loc = new Point(-2.7189941059014124, 56.0732572277308, wgs84);

    Point puffin1Loc = new Point(-2.7203673941913724, 56.073448846445544, wgs84);
    Point puffin2Loc = new Point(-2.639171724047482, 56.07843059864234, wgs84);

    Point fulmar1Loc = new Point(-2.6690407443541138, 56.05821218553146, wgs84);
    Point fulmar2Loc = new Point(-2.6390000630112374, 56.07785581394854, wgs84);
    Point fulmar3Loc = new Point(-2.7201957331551276, 56.07440692573053, wgs84);
    Point fulmar4Loc = new Point(-2.6889534245585356, 56.06242922266836, wgs84);
    Point fulmar5Loc = new Point(-2.6390000630112374, 56.05294024052195, wgs84);
    Point fulmar6Loc = new Point(-2.6542778952370436, 56.05821218553146, wgs84);

    // create graphics for each bird using the points above
    Graphic gannet1 = new Graphic(gannet1Loc);
    Graphic eider1 = new Graphic(eider1Loc);
    Graphic eider2 = new Graphic(eider2Loc);
    Graphic puffin1 = new Graphic(puffin1Loc);
    Graphic puffin2 = new Graphic(puffin2Loc);
    Graphic fulmar1 = new Graphic(fulmar1Loc);
    Graphic fulmar2 = new Graphic(fulmar2Loc);
    Graphic fulmar3 = new Graphic(fulmar3Loc);
    Graphic fulmar4 = new Graphic(fulmar4Loc);
    Graphic fulmar5 = new Graphic(fulmar5Loc);
    Graphic fulmar6 = new Graphic(fulmar6Loc);

    // need to know what kind of bird it is when it renders
    gannet1.getAttributes().put("SEABIRD", "Gannet");
    eider1.getAttributes().put("SEABIRD", "Eider");
    eider2.getAttributes().put("SEABIRD", "Eider");
    puffin1.getAttributes().put("SEABIRD", "Puffin");
    puffin2.getAttributes().put("SEABIRD", "Puffin");
    fulmar1.getAttributes().put("SEABIRD", "Fulmar");
    fulmar2.getAttributes().put("SEABIRD", "Fulmar");
    fulmar3.getAttributes().put("SEABIRD", "Fulmar");
    fulmar4.getAttributes().put("SEABIRD", "Fulmar");
    fulmar5.getAttributes().put("SEABIRD", "Fulmar");
    fulmar6.getAttributes().put("SEABIRD", "Fulmar");

    // adds graphics to the overlay
    graphicsOverlay.getGraphics().add(gannet1);
    graphicsOverlay.getGraphics().add(eider1);
    graphicsOverlay.getGraphics().add(eider2);
    graphicsOverlay.getGraphics().add(puffin1);
    graphicsOverlay.getGraphics().add(puffin2);
    graphicsOverlay.getGraphics().add(fulmar1);
    graphicsOverlay.getGraphics().add(fulmar2);
    graphicsOverlay.getGraphics().add(fulmar3);
    graphicsOverlay.getGraphics().add(fulmar4);
    graphicsOverlay.getGraphics().add(fulmar5);
    graphicsOverlay.getGraphics().add(fulmar6);

    // renders graphics to the map view by applying symbols to them
    UniqueValueRenderer uniqueValRenderer = new UniqueValueRenderer();
    // so the renderer knows what attribute it is looking for
    uniqueValRenderer.getFieldNames().add("SEABIRD");

    // create symbols to represent the different nesting locations
    SimpleMarkerSymbol gannetMarker = new SimpleMarkerSymbol(Style.TRIANGLE, PURPLE, 10);
    SimpleMarkerSymbol eiderMarker = new SimpleMarkerSymbol(Style.DIAMOND, BLUE, 10);
    SimpleMarkerSymbol puffinMarker = new SimpleMarkerSymbol(Style.CIRCLE, RED, 10);
    SimpleMarkerSymbol fulmarMarker = new SimpleMarkerSymbol(Style.CROSS, GREEN, 10);

    // set UniqueValue renders for each nesting type
    List<Object> gannetValue = new ArrayList<>();
    gannetValue.add("Gannet"); // name of the bird we are looking for
    UniqueValue uvGannet =
        // label, description, symbol, value to match to symbol
        new UniqueValue("Gannet", "Gannet", gannetMarker, gannetValue);
    uniqueValRenderer.getUniqueValues().add(uvGannet);

    List<Object> eiderValue = new ArrayList<>();
    eiderValue.add("Eider");
    UniqueValue uvEider = new UniqueValue("Eider", "Eider", eiderMarker, eiderValue);
    uniqueValRenderer.getUniqueValues().add(uvEider);

    List<Object> puffinValue = new ArrayList<>();
    puffinValue.add("Puffin");
    UniqueValue uvPuffin = new UniqueValue("Puffin", "Puffin", puffinMarker, puffinValue);
    uniqueValRenderer.getUniqueValues().add(uvPuffin);

    List<Object> fulmarValue = new ArrayList<>();
    fulmarValue.add("Fulmar");
    UniqueValue uvFulmar = new UniqueValue("Fulmar", "Fulmar", fulmarMarker, fulmarValue);
    uniqueValRenderer.getUniqueValues().add(uvFulmar);

    // set UniqueValue renderer to the graphics in the graphics overlay
    graphicsOverlay.setRenderer(uniqueValRenderer);
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() throws Exception {

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
