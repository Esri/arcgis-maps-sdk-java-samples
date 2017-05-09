package com.esri.samples.localserver.local_server_dynamic_workspace_raster;


import java.io.File;
import java.util.Arrays;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISMapImageSublayer;
import com.esri.arcgisruntime.layers.RasterSublayerSource;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.localserver.DynamicWorkspace;
import com.esri.arcgisruntime.localserver.LocalMapService;
import com.esri.arcgisruntime.localserver.LocalServer;
import com.esri.arcgisruntime.localserver.LocalServerStatus;
import com.esri.arcgisruntime.localserver.LocalService;
import com.esri.arcgisruntime.localserver.RasterWorkspace;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.sun.javafx.css.FontFace;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class LocalServerDynamicWorkspaceRaster extends Application {

	private MapView mapView;
	private static final LocalServer server = LocalServer.INSTANCE;
	private LocalMapService localMapService;
	private ArcGISMapImageSublayer newSL;
	
	
	@Override
	public void start(Stage stage) throws Exception {
		
		
	    try {
	        // create stack pane and application scene
	        StackPane stackPane = new StackPane();
	        Scene scene = new Scene(stackPane);
	        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

	        // set title, size, and add scene to stage
	        stage.setTitle("Dynamic workspaces: raster");
	        stage.setWidth(800);
	        stage.setHeight(700);
	        stage.setScene(scene);
	        stage.show();

	        // create a control panel
	        VBox vBoxControl = new VBox(6);
	        vBoxControl.setMaxSize(240, 150);
	        vBoxControl.getStyleClass().add("panel-region");
	        vBoxControl.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
	       
	        // create the descriptive label
	        Label description = new Label("This application shows how to create a dynamic workspace connection " +
	                "to a raster folder and display raster data in a map. Click the " +
	                "button to select a local raster and add it to the map.");
	        description.setFont(new Font("veranda",12));
	        description.setTextAlignment(TextAlignment.LEFT);
	        description.setWrapText(true);

	        
	        // create Add Raster button
	        Button addButton = new Button("Add Raster");
	        addButton.setMaxWidth(Double.MAX_VALUE);
	        addButton.setDisable(true);


	        // choose the file, then start the Local Server instance and the local map service...
	        addButton.setOnAction(e -> {
	          openFileChooser(stage);        
	        });

	        

	        // add button to the control panel
	        vBoxControl.getChildren().addAll(description, addButton);

	        ArcGISMap map = new ArcGISMap(Basemap.createTopographic());
	      
	        map.addDoneLoadingListener(() -> addButton.setDisable(false));

	        mapView = new MapView();
	        mapView.setMap(map);

	        // add the map view and control panel to stack pane
	        stackPane.getChildren().addAll(mapView, vBoxControl);
	        StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
	        StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));

	      } catch (Exception e) {
	        // on any error, display the stack trace
	        e.printStackTrace();
	      }
		
	}
	
	private void openFileChooser(Stage mainStage) {
		
		// Browse to the raster file
		 FileChooser fileChooser = new FileChooser();
		 fileChooser.setTitle("Open Resource File");
		 fileChooser.getExtensionFilters().addAll(
		         new ExtensionFilter("Image Files", "*.tif"));
		 
		 fileChooser.setInitialDirectory(new File("./samples-data/raster/"));
		 File selectedFile = fileChooser.showOpenDialog(mainStage);
		 
		 if(selectedFile != null){
		 
			 String fileName = selectedFile.getName();
			 String path = selectedFile.getParent();
		 
			 startLocalService(fileName, path);
		 }
		 
		
	}

	private void startLocalService(String fileName, String path) {
		
		 // start local server
	      server.startAsync();
	      server.addStatusChangedListener(status -> {
	        if (server.getStatus() == LocalServerStatus.STARTED) {
	        	
	          // start a service from the blank MPK...
	          String mapServiceURL = "./samples-data/local_server/mpk_blank.mpk";
	          localMapService = new LocalMapService(mapServiceURL);
	          
	          // Can't add a dynamic workspace to a running service, so do that first...
	          RasterWorkspace rasterWS = new RasterWorkspace("raster_wkspc", path);
	          RasterSublayerSource source = new RasterSublayerSource(rasterWS.getId(),fileName);
	          newSL = new ArcGISMapImageSublayer(101, source);
	          Iterable<DynamicWorkspace> dynamicWorkspaces = Arrays.asList(rasterWS);
	          localMapService.setDynamicWorkspaces(dynamicWorkspaces);
	          
	          
	          localMapService.addStatusChangedListener(new LocalService.StatusChangedListener(){

				@Override
				public void statusChanged(LocalService.StatusChangedEvent event) {
					if (event.getNewStatus() == LocalServerStatus.STARTED){
						
						// Now, we're ready to add the raster layer...
						addLocalMapImageLayer();
					}
				}});
	          
	          localMapService.startAsync();
	        }
	      });
		
	}

	private void addLocalMapImageLayer() {

	      
	      // create a map image layer using url
	      ArcGISMapImageLayer imageLayer = new ArcGISMapImageLayer(localMapService.getUrl());
	      
	      // set viewpoint once layer has loaded
	      imageLayer.addDoneLoadingListener(() -> {
	        if (imageLayer.getLoadStatus() == LoadStatus.LOADED && imageLayer.getFullExtent() != null) {
	          
	        	
	          imageLayer.getSublayers().add(newSL);
	          
	          
	        }
	      });
	      
	      imageLayer.loadAsync();
	      
	      // add image layer to map
	      mapView.getMap().getOperationalLayers().add(imageLayer);
	        
	      
	}
	
	public static void main(String[] args) {

	    Application.launch(args);
    }
	

}