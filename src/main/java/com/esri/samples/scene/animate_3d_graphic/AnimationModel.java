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

package com.esri.samples.scene.animate_3d_graphic;

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
  int nextKeyframe() {
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
