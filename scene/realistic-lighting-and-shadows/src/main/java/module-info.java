module realistic.lighting.and.shadows.main {
  requires com.esri.arcgisruntime;
  requires javafx.controls;
  requires javafx.graphics;
  requires javafx.fxml;
  exports com.esri.samples.realistic_lighting_and_shadows;
  opens com.esri.samples.realistic_lighting_and_shadows to javafx.fxml;
  opens realistic_lighting_and_shadows;

}