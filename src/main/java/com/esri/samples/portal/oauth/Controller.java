package com.esri.samples.portal.oauth;

import com.esri.arcgisruntime.portal.PortalUser;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Main controller to manage different views throughout the lifecycle of the application.
 */
public final class Controller {
  public static Controller INSTANCE;

  private Scene scene;
  private Stage stage;

  /**
   * Creates a new instance.
   * @param scene main scene of the application
   * @param stage main stage of the application
   */
  private Controller(Scene scene, Stage stage) {
    this.scene = scene;
    this.stage = stage;
  }

  /**
   * Creates a new instance.
   * @param scene main scene of the application
   * @param stage main stage of the application
   */
  public static synchronized void create(Scene scene, Stage stage) {
    if (INSTANCE != null) {
      throw new RuntimeException("An instance of controller exists already.");
    }
    INSTANCE = new Controller(scene, stage);
  }

  /**
   * Sets the main scene of the application to display user information.
   * @param portalUser user whose information is to be displayed
   */
  public static void show(PortalUser portalUser)  {
    checkInstance();
    FXMLLoader loader = new FXMLLoader(Controller.class.getResource("/fxml/oauth_signed_in_view.fxml"));
    Parent root = null;
    try {
      root = loader.load();
      ((Label) root.lookup("#fullName")).setText(portalUser.getFullName());
      ((Label) root.lookup("#username")).setText(portalUser.getUsername());
      ((Label) root.lookup("#email")).setText(portalUser.getEmail());
      ((Label) root.lookup("#memberSince")).setText(format((portalUser.getCreated().getTime())));
      ((Label) root.lookup("#role")).setText(portalUser.getRole().name());

      INSTANCE.scene.setRoot(root);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Displays a new dialog containing the content.
   * @param dialogContent content to be displayed in a dialog
   * @param width width of the dialog
   * @param height height of the dialog
   * @return returns the stage of the new dialog
   */
  public static Stage showDialog(Parent dialogContent, int width, int height)  {
    checkInstance();
    Stage dialog = new Stage();
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialog.initOwner(INSTANCE.stage);
    dialog.setScene(new Scene(dialogContent, width, height));
    dialog.show();
    return dialog;
  }

  /**
   * Displays the error message.
   * @param message error message
   */
  public static void showError(String message) {
    checkInstance();
    if (message == null || message.isEmpty()) {
      return;
    }
    TextArea messageArea = new TextArea("Error: " + message);
    messageArea.setWrapText(true);
    messageArea.setEditable(false);
    showDialog(messageArea, 420, 120);
  }

  /**
   * Formats date.
   * @param date date to be formatted
   * @return formatted date
   */
  private static String format(Date date) {
    return new SimpleDateFormat("MMM dd, yyyy").format(date);
  }

  /**
   * Checks whether an instance of controller exists.
   * @throws  RuntimeException if an instance does not exist.
   */
  private static void checkInstance() {
    if (INSTANCE == null) {
      throw new RuntimeException("Create an instance first.");
    }
  }
}
