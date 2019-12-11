/*
 * Copyright 2017 Esri.
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

package com.esri.samples.configure_subnetwork_trace;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.CodedValue;
import com.esri.arcgisruntime.data.CodedValueDomain;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.utilitynetworks.UtilityAssetGroup;
import com.esri.arcgisruntime.utilitynetworks.UtilityAssetType;
import com.esri.arcgisruntime.utilitynetworks.UtilityAttributeComparisonOperator;
import com.esri.arcgisruntime.utilitynetworks.UtilityCategoryComparison;
import com.esri.arcgisruntime.utilitynetworks.UtilityDomainNetwork;
import com.esri.arcgisruntime.utilitynetworks.UtilityElement;
import com.esri.arcgisruntime.utilitynetworks.UtilityElementTraceResult;
import com.esri.arcgisruntime.utilitynetworks.UtilityNetwork;
import com.esri.arcgisruntime.utilitynetworks.UtilityNetworkAttribute;
import com.esri.arcgisruntime.utilitynetworks.UtilityNetworkAttributeComparison;
import com.esri.arcgisruntime.utilitynetworks.UtilityNetworkSource;
import com.esri.arcgisruntime.utilitynetworks.UtilityTier;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceAndCondition;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceConditionalExpression;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceConfiguration;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceOrCondition;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceParameters;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceResult;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceType;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraversabilityScope;

public class ConfigureSubnetworkTraceController {

  @FXML
  private CheckBox includeBarriersCheckBox;
  @FXML
  private CheckBox includeContainersCheckBox;
  @FXML
  private TextArea traceConditionsTextArea;
  @FXML
  private ComboBox<CodedValue> comparisonValuesComboBox;
  @FXML
  private ComboBox<UtilityNetworkAttribute> comparisonSourcesComboBox;
  @FXML
  private ComboBox<UtilityAttributeComparisonOperator> comparisonOperatorsComboBox;
  private UtilityNetwork utilityNetwork;
  private UtilityTraceConfiguration initialUtilityTraceConfiguration;
  private UtilityTraceConfiguration utilityTraceConfiguration;
  private UtilityElement startingLocation;

  private UtilityTraceConditionalExpression initialExpression;

  @FXML
  public void initialize() {
    try {
      // load the utility network
      utilityNetwork = new UtilityNetwork(
          "https://sampleserver7.arcgisonline.com/arcgis/rest/services/UtilityNetwork/NapervilleElectric" +
              "/FeatureServer");
      utilityNetwork.loadAsync();
      utilityNetwork.addDoneLoadingListener(() -> {
        if (utilityNetwork.getLoadStatus() == LoadStatus.LOADED) {

          // build the choice lists for network attribute comparison
          List<UtilityNetworkAttribute> comparisonSources = utilityNetwork.getDefinition()
              .getNetworkAttributes()
              .stream()
              .filter(UtilityNetworkAttribute::isSystemDefined)
              .collect(Collectors.toList());
          comparisonSourcesComboBox.getItems().addAll(comparisonSources);
          comparisonSourcesComboBox.getSelectionModel().select(0);

          comparisonOperatorsComboBox.getItems().addAll(UtilityAttributeComparisonOperator.values());
          comparisonOperatorsComboBox.getSelectionModel().select(0);

          // create a default starting location
          UtilityNetworkSource utilityNetworkSource =
              utilityNetwork.getDefinition().getNetworkSource("Electric Distribution Device");
          UtilityAssetGroup utilityAssetGroup = utilityNetworkSource.getAssetGroup("Service Point");
          UtilityAssetType utilityAssetType = utilityAssetGroup.getAssetType("Three Phase Low Voltage Meter");
          startingLocation =
              utilityNetwork.createElement(utilityAssetType, UUID.fromString("3AEC2649-D867-4EA7-965F-DBFE1F64B090"));

          // get a default trace configuration from a tier to update the UI
          UtilityDomainNetwork utilityDomainNetwork =
              utilityNetwork.getDefinition().getDomainNetwork("ElectricDistribution");
          UtilityTier utilityTier = utilityDomainNetwork.getTier("Medium Voltage Radial");

          // get the utilityTrace configuration
          initialUtilityTraceConfiguration = utilityTier.getTraceConfiguration();

          // save the initial expression
          UtilityTraceConditionalExpression initialExpression =
              (UtilityTraceConditionalExpression) utilityTier.getTraceConfiguration().getTraversability().getBarriers();

          // show the initial expression in the text area
          traceConditionsTextArea.setText(getExpression(initialExpression));

          // set the traversability scope
          utilityTier.getTraceConfiguration().getTraversability().setScope(UtilityTraversabilityScope.JUNCTIONS);
        }
      });
    } catch (Exception e) {
      new Alert(Alert.AlertType.ERROR, "Error loading Utility Network.").show();
    }
  }

  @FXML
  private void resolveAddConditionClick(ActionEvent actionEvent) {
    try {

      UtilityNetworkAttribute selectedSourceAttribute = comparisonSourcesComboBox.getSelectionModel().getSelectedItem();
      UtilityAttributeComparisonOperator selectedSourceOperator =
          comparisonOperatorsComboBox.getSelectionModel().getSelectedItem();

      Object otherValue;
      if (selectedSourceAttribute.getDomain() instanceof CodedValueDomain) {

      }


    } catch (Exception e) {
    }
  }

  private String getExpression(UtilityTraceConditionalExpression expression) {

    StringBuilder stringBuilder = new StringBuilder();

    if (expression instanceof UtilityCategoryComparison) {
      UtilityCategoryComparison categoryComparison = (UtilityCategoryComparison) expression;
      stringBuilder.append(categoryComparison.getCategory().getName())
          .append(" ")
          .append(categoryComparison.getComparisonOperator());

    }

    if (expression instanceof UtilityNetworkAttributeComparison) {
      UtilityNetworkAttributeComparison attributeComparison = (UtilityNetworkAttributeComparison) expression;

      stringBuilder.append(attributeComparison.getNetworkAttribute().getName())
          .append(" ")
          .append(attributeComparison.getComparisonOperator())
          .append(" ");

      if (attributeComparison.getNetworkAttribute().getDomain() instanceof CodedValueDomain) {
        CodedValueDomain codedValueDomain = (CodedValueDomain) attributeComparison.getNetworkAttribute().getDomain();
        if (!codedValueDomain.getCodedValues().isEmpty()) {

          // get the data type of the used network attribute
          UtilityNetworkAttribute.DataType dataType = attributeComparison.getNetworkAttribute().getDataType();

          // get the coded values from the domain and find the ones where the code matches the network attribute's data type
          List<CodedValue> list = codedValueDomain.getCodedValues()
              .stream()
              .filter(value -> value.getCode().equals(dataType))
              .collect(Collectors.toList());

          if (!list.isEmpty()) {
            // get the first coded value and add it's name
            CodedValue codedValue = list.get(0);
            stringBuilder.append(codedValue);
          }
        }
      } else {
        if (attributeComparison.getOtherNetworkAttribute() != null) {
          stringBuilder.append(attributeComparison.getOtherNetworkAttribute().getName());
        } else {
          stringBuilder.append(attributeComparison.getValue());
        }
      }
    }

    if (expression instanceof UtilityTraceAndCondition) {
      UtilityTraceAndCondition andCondition = (UtilityTraceAndCondition) expression;
      stringBuilder.append(andCondition.getLeftExpression());
      stringBuilder.append("AND \n");
      stringBuilder.append(andCondition.getRightExpression());
    }

    if (expression instanceof UtilityTraceOrCondition) {
      UtilityTraceAndCondition orCondition = (UtilityTraceAndCondition) expression;
      stringBuilder.append(orCondition.getLeftExpression());
      stringBuilder.append("OR \n");
      stringBuilder.append(orCondition.getRightExpression());
    }

    return stringBuilder.toString();
  }

  /**
   * Builds trace parameters using the constructed trace configurations and runs the trace in the utility network. On
   * completion, shows an alert with the number of found elements.
   */
  @FXML
  private void resolveTraceClick() {
    try {
      // build utility trace parameters for a subnetwork trace using the prepared starting location
      UtilityTraceParameters utilityTraceParameters =
          new UtilityTraceParameters(UtilityTraceType.SUBNETWORK, Collections.singletonList(startingLocation));

      // set the defined trace configuration to the trace parameters
      utilityTraceParameters.setTraceConfiguration(initialUtilityTraceConfiguration);

      // run the utility trace and get the results
      ListenableFuture<List<UtilityTraceResult>> utilityTraceResultsFuture =
          utilityNetwork.traceAsync(utilityTraceParameters);
      utilityTraceResultsFuture.addDoneListener(() -> {
        try {
          List<UtilityTraceResult> utilityTraceResults = utilityTraceResultsFuture.get();

          if (utilityTraceResults.get(0) instanceof UtilityElementTraceResult) {
            UtilityElementTraceResult utilityElementTraceResult =
                (UtilityElementTraceResult) utilityTraceResults.get(0);

            // show an alert with the number of elements found
            int elementsFound = utilityElementTraceResult.getElements().size();
            new Alert(Alert.AlertType.INFORMATION, elementsFound + "elements found.").show();

          } else {
            new Alert(Alert.AlertType.ERROR, "Trace result not a utility element.").show();
          }

        } catch (Exception e) {
          new Alert(Alert.AlertType.ERROR, "Error running utility network trace.").show();
        }
      });
    } catch (Exception e) {
      new Alert(Alert.AlertType.ERROR, "Error building trace parameters / configuration.").show();
    }
  }

  @FXML
  private void resolveResetClick(ActionEvent actionEvent) {
    utilityTraceConfiguration = initialUtilityTraceConfiguration;
  }

  /**
   * Updates the contents of the comparison value choices ComboBox depending on the selected comparison source
   */
  public void onComparisonSourceChanged() {

    if (comparisonSourcesComboBox.getSelectionModel().getSelectedItem() != null) {

      UtilityNetworkAttribute selectedAttribute = comparisonSourcesComboBox.getSelectionModel().getSelectedItem();
      if (selectedAttribute.getDomain() instanceof CodedValueDomain) {

        List<CodedValue> comparisonValues = ((CodedValueDomain) selectedAttribute.getDomain()).getCodedValues();

        comparisonValuesComboBox.getItems().clear();
        comparisonValuesComboBox.getItems().addAll(comparisonValues);
      }
    }
    //    comparisonValuesComboBox.setVisible(!comparisonValuesComboBox.getItems().isEmpty());
  }


  /**
   * Stops and releases all resources used in application.
   */
  void terminate() {

  }
}
