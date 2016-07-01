package com.esri.samples.scene;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Model bean to bind to animation properties.
 */
public class AnimationModel {

  private final IntegerProperty frames;
  private final IntegerProperty keyframe;

  /**
   * Default constructor (needed for FXML injection).
   */
  public AnimationModel() {
    this.frames = new SimpleIntegerProperty(1);
    this.keyframe = new SimpleIntegerProperty(0);
  }

  /**
   * Constructs the animation model with the specified keyframe.
   *
   * @param keyframe starting animation frame
   */
  public AnimationModel(int keyframe) {
    this();
    this.setKeyframe(keyframe);
  }

  /**
   * Gets the total number of frames in the animation
   *
   * @return total frames in animation
   */
  public int getFrames() {
    return frames.get();
  }

  /**
   * Property tracking the number of frames in an animation.
   *
   * @return frames property
   */
  public IntegerProperty framesProperty() {
    return frames;
  }

  /**
   * Sets the total number of frames in the animation.
   *
   * @param frames total frames in animation
   */
  public void setFrames(int frames) {
    this.frames.set(frames);
  }

  /**
   * Gets the current keyframe in the animation.
   *
   * @return current keyframe
   */
  public int getKeyframe() {
    return keyframe.get();
  }

  /**
   * Increments and gets the next keyframe.
   *
   * @return next keyframe
   */
  public int nextKeyframe() {
    setKeyframe(getKeyframe() + 1);
    return getKeyframe();
  }

  /**
   * Property tracking the current frame of an animation.
   *
   * @return keyframe property
   */
  public IntegerProperty keyframeProperty() {
    return keyframe;
  }

  /**
   * Sets the current keyframe.
   *
   * @param keyframe index corresponding to animation keyframe
   */
  public void setKeyframe(int keyframe) {
    this.keyframe.set(keyframe % getFrames());
  }
}
