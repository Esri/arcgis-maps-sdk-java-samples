/*
 * Copyright 2023 Esri.
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

package com.esri.samples.add_custom_dynamic_entity_data_source;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.Gson;

import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.realtime.DynamicEntityDataSource;
import com.esri.arcgisruntime.realtime.DynamicEntityDataSourceInfo;

/**
 * A custom DynamicEntityDataSource for processing observations read from a given json file.
 */
class SimulatedDataSource extends DynamicEntityDataSource {
  private final String fileName;
  private final String entityIdFieldName;
  private final long delay;
  private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
  private ScheduledFuture<?> observationProcessing;
  private final AtomicInteger currentLineNumber = new AtomicInteger();
  private final List<String> allJsonLines = new ArrayList<>();
  private final Gson gson = new Gson();

  /**
   * Construct a custom DynamicEntityDataSource.
   *
   * @param fileName name of a file with json observation data
   * @param entityIdFieldName the name of the field containing values that uniquely identifies each entity
   * @param delay millisecond delay between observations being added to the data source
   */
  SimulatedDataSource(String fileName, String entityIdFieldName, long delay) {
    this.fileName = fileName;
    this.entityIdFieldName = entityIdFieldName;
    this.delay = delay;
  }

  @Override
  protected CompletableFuture<Void> onConnectAsync() {
    // Store all lines from file in a list
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        allJsonLines.add(line);
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }

    // Process file data a line at a time
    observationProcessing = executorService.scheduleAtFixedRate(this::processNextObservation, 0L, delay, TimeUnit.MILLISECONDS);
    return CompletableFuture.completedFuture(null);
  }

  /**
   * Process the next observation, canceling the task when all lines have been processed.
   */
  private void processNextObservation() {
    int lineNumber = currentLineNumber.getAndAdd(1);
    if (lineNumber >= allJsonLines.size()) {
      // Finished all the lines
      observationProcessing.cancel(false);
    } else {
      // Process the line by parsing the json, and creating the observation, and adding to the data source.
      Observation observation = gson.fromJson(allJsonLines.get(lineNumber), Observation.class);
      Point point = new Point(observation.geometry.x, observation.geometry.y, SpatialReferences.getWgs84());
      addObservation(point, observation.attributes);
    }
  }

  @Override
  protected CompletableFuture<Void> onDisconnectAsync() {
    observationProcessing.cancel(true);
    return CompletableFuture.completedFuture(null);
  }

  @Override
  protected CompletableFuture<DynamicEntityDataSourceInfo> onLoadAsync() {
    var dynamicEntityDataSourceInfo = new DynamicEntityDataSourceInfo(entityIdFieldName,
      List.of(
        Field.createString("MMSI", null, 256),
        Field.createDouble("BaseDateTime", null),
        Field.createDouble("LAT", null),
        Field.createDouble("LONG", null),
        Field.createDouble("SOG", null),
        Field.createDouble("COG", null),
        Field.createDouble("Heading", null),
        Field.createString("VesselName", null, 256),
        Field.createString("IMO", null, 256),
        Field.createString("CallSign", null, 256),
        Field.createString("VesselType", null, 256),
        Field.createString("Status", null, 256),
        Field.createDouble("Length", null),
        Field.createDouble("Width", null),
        Field.createString("Cargo", null, 256),
        Field.createString("globalid", null, 256)
      ));
    dynamicEntityDataSourceInfo.setSpatialReference(SpatialReferences.getWgs84());
    return CompletableFuture.completedFuture(dynamicEntityDataSourceInfo);
  }

  @Override
  public String getUri() {
    return null;
  }

  /**
   * Stop processing observations from the file.
   */
  public void stopObservations() {
    if (observationProcessing != null) {
      observationProcessing.cancel(true);
    }
    executorService.shutdown();
  }

  /**
   * Used by Gson for parsing the data.
   */
  public static class ObservationSpatialReference {
    public int wkid;
  }

  /**
   * Used by Gson for parsing the data.
   */
  public static class ObservationGeometry {
    public double x;
    public double y;
    public ObservationSpatialReference spatialReference;
  }

  /**
   * Used by Gson for parsing the data.
   */
  public static class Observation {
    public ObservationGeometry geometry;
    public HashMap<String, Object> attributes;
  }
}
