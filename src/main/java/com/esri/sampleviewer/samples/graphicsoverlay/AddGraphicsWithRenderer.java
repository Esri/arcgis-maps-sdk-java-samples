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

package com.esri.sampleviewer.samples.graphicsoverlay;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.mapping.BasemapType;
import com.esri.arcgisruntime.mapping.Map;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.RgbColor;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.UniqueValue;
import com.esri.arcgisruntime.symbology.UniqueValueRenderer;

/**
 * This sample shows how you can add a graphics overlay to your map view which contain different types of graphic.
 */

public class AddGraphicsWithRenderer extends Application {

  private MapView mapView;
  private Map map;
  private SpatialReference wgs84 = SpatialReference.create(4326);

  @Override
  public void start(Stage stage) throws Exception {
    // create a border pane
    BorderPane borderPane = new BorderPane();
    Scene scene = new Scene(borderPane);

    // size the stage and add a title
    stage.setTitle("Add graphics with renderers");
    stage.setWidth(700);
    stage.setHeight(800);
    stage.setScene(scene);
    stage.show();
    
    try {
      // create a new map with a light grey canvas.
      map = new Map(BasemapType.LIGHT_GRAY_CANVAS, 56.075844,-2.681572, 13);
      
      // create the MapView JavaFX control and assign its map
      mapView = new MapView();
      mapView.setMap(map);
      
      // add the MapView
      borderPane.setCenter(mapView);

      // creates the graphics overlay
      GraphicsOverlay graphicsOvelay = new GraphicsOverlay();

      // adds the overlay to the map view
      mapView.getGraphicsOverlays().add(graphicsOvelay);
      
      // add nesting locations rendered per bird
      addNestingLocations(graphicsOvelay);
      
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  @Override
  public void stop() throws Exception {
    // release resources when the application closes
    mapView.dispose();
    map.dispose();
    Platform.exit();
    System.exit(0);
  }

  public static void main(String[] args) {
    Application.launch(args);
  }
  
  private void addNestingLocations(GraphicsOverlay graphicsOverlay) {
    
    // Gannet locations
    Point gannet1Loc = new Point(-2.6419183006274025,56.07737682015417, wgs84);
    
    // Fulmers locations
    Point fulmar1Loc = new Point(-2.6690407443541138,56.05821218553146, wgs84);
    Point fulmar2Loc = new Point(-2.6390000630112374,56.07785581394854, wgs84);
    Point fulmar3Loc = new Point(-2.7201957331551276,56.074406925730536, wgs84);
    Point fulmar4Loc = new Point(-2.6889534245585356,56.06242922266836, wgs84);
    Point fulmar5Loc = new Point(-2.6390000630112374,56.052940240521956, wgs84);
    Point fulmar6Loc = new Point(-2.6542778952370436,56.05821218553146, wgs84);
    
    // Eider Duck locations
    Point eider1Loc = new Point(-2.6884384414498004,56.0626208952164, wgs84);
    Point eider2Loc = new Point(-2.7189941059014124,56.07325722773041, wgs84);
    
    // Puffin locations
    Point puffin1Loc = new Point(-2.7203673941913724,56.073448846445544, wgs84);
    Point puffin2Loc = new Point(-2.639171724047482,56.07843059864234, wgs84);
    
    // markers used for different sea birds
    SimpleMarkerSymbol puffinMarker = 
        new SimpleMarkerSymbol(
            new RgbColor(255, 0, 0, 255), 
            10, 
            SimpleMarkerSymbol.Style.CIRCLE);
    SimpleMarkerSymbol gannetMarker = 
        new SimpleMarkerSymbol(
            new RgbColor(128, 0, 128, 255), 
            10, 
            SimpleMarkerSymbol.Style.TRIANGLE);
    SimpleMarkerSymbol fulmarMarker = 
        new SimpleMarkerSymbol(
            new RgbColor(0, 255, 0, 255), 
            10, 
            SimpleMarkerSymbol.Style.CROSS);
    SimpleMarkerSymbol eiderMarker = 
        new SimpleMarkerSymbol(
            new RgbColor(0, 0, 255, 255), 
            10, 
            SimpleMarkerSymbol.Style.DIAMOND);
    
    // a unique value renderer using the SEABIRD attribute
    UniqueValueRenderer uniqueValRenderer = new UniqueValueRenderer();
    uniqueValRenderer.getFieldNames().add("SEABIRD");
    
    // unique value for Puffin
    List<Object> puffinValue = new ArrayList<>();
    puffinValue.add("Puffin");
    UniqueValue uvPuffin = new UniqueValue("Puffin", "Puffin", puffinMarker, puffinValue);
    uniqueValRenderer.getUniqueValues().add(uvPuffin);
    
    // unique value for Gannet
    List<Object> gannetValue = new ArrayList<>();
    gannetValue.add("Gannet");
    UniqueValue uvGannet = new UniqueValue("Gannet", "Gannet", gannetMarker, gannetValue);
    uniqueValRenderer.getUniqueValues().add(uvGannet);
    
    // unique value for Fulmar
    List<Object> fulmarValue = new ArrayList<>();
    fulmarValue.add("Fulmar");
    UniqueValue uvFulmar = new UniqueValue("Fulmar", "Fulmar", fulmarMarker, fulmarValue);
    uniqueValRenderer.getUniqueValues().add(uvFulmar);
    
    // unique value for Eider
    List<Object> eiderValue = new ArrayList<>();
    eiderValue.add("Eider");
    UniqueValue uvEider = new UniqueValue("Eider", "Eider", eiderMarker, eiderValue);
    uniqueValRenderer.getUniqueValues().add(uvEider);
    
    // apply the renderer to the graphics overlay
    graphicsOverlay.setRenderer(uniqueValRenderer);
    

    // graphics for Eider Ducks
    Graphic eider1 = new Graphic(eider1Loc);
    eider1.getAttributes().put("SEABIRD", "Eider");
    Graphic eider2 = new Graphic(eider2Loc);
    eider2.getAttributes().put("SEABIRD", "Eider");
    
    // graphics for Gannets
    Graphic gannet1 = new Graphic(gannet1Loc);
    gannet1.getAttributes().put("SEABIRD", "Gannet");
    
    // graphics for Fulmars
    Graphic fulmar1 = new Graphic(fulmar1Loc);
    fulmar1.getAttributes().put("SEABIRD", "Fulmar");
    Graphic fulmar2 = new Graphic(fulmar2Loc);
    fulmar2.getAttributes().put("SEABIRD", "Fulmar");
    Graphic fulmar3 = new Graphic(fulmar3Loc);
    fulmar3.getAttributes().put("SEABIRD", "Fulmar");
    Graphic fulmar4 = new Graphic(fulmar4Loc);
    fulmar4.getAttributes().put("SEABIRD", "Fulmar");
    Graphic fulmar5 = new Graphic(fulmar5Loc);
    fulmar5.getAttributes().put("SEABIRD", "Fulmar");
    Graphic fulmar6 = new Graphic(fulmar6Loc);
    fulmar6.getAttributes().put("SEABIRD", "Fulmar");
    
    // graphics for Puffins
    Graphic puffin1 = new Graphic(puffin1Loc);
    puffin1.getAttributes().put("SEABIRD", "Puffin");
    Graphic puffin2 = new Graphic(puffin2Loc);
    puffin2.getAttributes().put("SEABIRD", "Puffin");
    
    // add all sea birds to graphics overlay
    graphicsOverlay.getGraphics().add(puffin1);
    graphicsOverlay.getGraphics().add(puffin2);
    graphicsOverlay.getGraphics().add(fulmar1);
    graphicsOverlay.getGraphics().add(fulmar2);
    graphicsOverlay.getGraphics().add(fulmar3);
    graphicsOverlay.getGraphics().add(fulmar4);
    graphicsOverlay.getGraphics().add(fulmar5);
    graphicsOverlay.getGraphics().add(fulmar6);
    graphicsOverlay.getGraphics().add(gannet1);
    graphicsOverlay.getGraphics().add(eider1);
    graphicsOverlay.getGraphics().add(eider2);
  }
}
