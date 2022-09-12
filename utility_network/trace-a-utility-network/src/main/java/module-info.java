module trace.a.utility.network.main {
  requires com.esri.arcgisruntime;
  requires javafx.base;
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.graphics;
  exports com.esri.samples.trace_a_utility_network;
  opens com.esri.samples.trace_a_utility_network to javafx.fxml;
}