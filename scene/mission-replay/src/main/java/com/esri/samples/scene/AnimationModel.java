package com.esri.samples.scene;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class AnimationModel {

  private final IntegerProperty frames;
  private final IntegerProperty keyframe;
  private final DoubleProperty progress;

  public AnimationModel() {
    this.frames = new SimpleIntegerProperty(1);
    this.keyframe = new SimpleIntegerProperty(0);
    this.progress = new SimpleDoubleProperty(0);

    this.progress.bind(Bindings.createIntegerBinding(() -> getKeyframe() / getFrames(), this.keyframe, this.frames));
  }

  public AnimationModel(int keyframe) {
    this();
    this.setKeyframe(keyframe);
  }

  public int getFrames() {
    return frames.get();
  }

  public IntegerProperty framesProperty() {
    return frames;
  }

  public void setFrames(int frames) {
    this.frames.set(frames);
  }

  public double getProgress() {
    return progress.get();
  }

  public DoubleProperty progressProperty() {
    return progress;
  }

  public void setProgress(double progress) {
    this.progress.set(progress);
  }

  public int getKeyframe() {
    return keyframe.get();
  }

  public int nextKeyframe() {
    setKeyframe(getKeyframe() + 1);
    return getKeyframe();
  }

  public IntegerProperty keyframeProperty() {
    return keyframe;
  }

  public void setKeyframe(int keyframe) {
    this.keyframe.set(keyframe % getFrames());
  }
}
