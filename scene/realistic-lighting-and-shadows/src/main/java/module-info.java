module realistic.lighting.and.shadows.main {
  requires com.esri.arcgisruntime;

  requires javafx.controls;
  requires javafx.base;
  requires javafx.graphics;
  requires javafx.media;
  requires javafx.web;
  requires javafx.fxml;

  exports com.esri.samples.realistic_lighting_and_shadows;

  opens com.esri.samples.realistic_lighting_and_shadows to javafx.fxml;
  opens realistic_lighting_and_shadows;

}