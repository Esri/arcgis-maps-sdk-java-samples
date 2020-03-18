/*
 * Copyright 2020 Esri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.esri.samples.configure_subnetwork_trace;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.CodedValue;
import com.esri.arcgisruntime.data.CodedValueDomain;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.view.MapView;
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
import com.esri.arcgisruntime.utilitynetworks.UtilityTerminal;
import com.esri.arcgisruntime.utilitynetworks.UtilityTier;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceAndCondition;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceConditionalExpression;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceConfiguration;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceOrCondition;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceParameters;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceResult;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceType;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraversability;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraversabilityScope;

public class ConfigureSubnetworkTraceController {

  @FXML private CheckBox includeBarriersCheckBox;
  @FXML private CheckBox includeContainersCheckBox;
  @FXML private ComboBox<CodedValue> comparisonValuesComboBox;
  @FXML private ComboBox<UtilityAttributeComparisonOperator> comparisonOperatorsComboBox;
  @FXML private ComboBox<UtilityNetworkAttribute> comparisonSourcesComboBox;
  @FXML private TextArea traceConditionsTextArea;
  @FXML private TextField comparisonValuesTextField;

  private UtilityElement startingLocation;
  private UtilityNetwork utilityNetwork;
  private UtilityTerminal startingTerminal;
  private UtilityTraceConditionalExpression initialExpression;
  private UtilityTraceConfiguration initialUtilityTraceConfiguration;
  private UtilityTraceConfiguration utilityTraceConfiguration;
  private UtilityTraversability utilityTraversability;

  @FXML
  public void initialize() {

    try {

      // add a listener to the comparison value text field, so that it only accepts numerical input separated by a decimal
      comparisonValuesTextField.textProperty().addListener((observable, oldValue, newValue) -> {
        if (!newValue.matches("\\d*([.]\\d*)?")) {
          comparisonValuesTextField.setText(oldValue);
        }
      });

      // load the utility network
      utilityNetwork = new UtilityNetwork(
          "https://sampleserver7.arcgisonline.com/arcgis/rest/services/UtilityNetwork/NapervilleElectric" +
              "/FeatureServer");
      utilityNetwork.loadAsync();
      utilityNetwork.addDoneLoadingListener(() -> {
        if (utilityNetwork.getLoadStatus() == LoadStatus.LOADED) {

          // build the choice list for the network attribute comparison sources
          List<UtilityNetworkAttribute> comparisonSources = utilityNetwork.getDefinition()
              .getNetworkAttributes()
              .stream()
              .filter(value -> !value.isSystemDefined())
              .collect(Collectors.toList());
          comparisonSourcesComboBox.getItems().addAll(comparisonSources);
          comparisonSourcesComboBox.getSelectionModel().select(0);
          // display the name of the comparison sources in the ComboBox
          comparisonSourcesComboBox.setButtonCell(new ComparisonSourceListCell());
          comparisonSourcesComboBox.setCellFactory(c -> new ComparisonSourceListCell());

          // build the choice list for the comparison operators
          comparisonOperatorsComboBox.getItems().addAll(UtilityAttributeComparisonOperator.values());
          comparisonOperatorsComboBox.getSelectionModel().select(0);

          // display the name of the comparison values in the ComboBox
          comparisonValuesComboBox.setButtonCell(new CodedValueListCell());
          comparisonValuesComboBox.setCellFactory(c -> new CodedValueListCell());

          // create a default starting location
          UtilityNetworkSource utilityNetworkSource =
              utilityNetwork.getDefinition().getNetworkSource("Electric Distribution Device");
          UtilityAssetGroup utilityAssetGroup = utilityNetworkSource.getAssetGroup("Circuit Breaker");
          UtilityAssetType utilityAssetType = utilityAssetGroup.getAssetType("Three Phase");
          startingLocation =
              utilityNetwork.createElement(utilityAssetType, UUID.fromString("1CAF7740-0BF4-4113-8DB2-654E18800028"));

          // set the terminal for the starting location. (For our case, we use the 'Load' terminal.)
          List<UtilityTerminal> terminals = startingLocation.getAssetType().getTerminalConfiguration().getTerminals();
          terminals.forEach(terminal -> {
            if (terminal.getName().equals("Load")) {
              startingTerminal = terminal;
            }
          });
          startingLocation.setTerminal(startingTerminal);

          // get a default trace configuration from a tier to update the UI
          UtilityDomainNetwork utilityDomainNetwork =
              utilityNetwork.getDefinition().getDomainNetwork("ElectricDistribution");
          UtilityTier utilityTier = utilityDomainNetwork.getTier("Medium Voltage Radial");
          utilityTraceConfiguration = utilityTier.getTraceConfiguration();

          // save the default trace configuration to restore when the application is reset
          initialUtilityTraceConfiguration = utilityTraceConfiguration;

          // save the initial expression
          initialExpression =
              (UtilityTraceConditionalExpression) utilityTier.getTraceConfiguration().getTraversability().getBarriers();

          // show the initial expression in the text area
          traceConditionsTextArea.setText(expressionToString(initialExpression));

          // set the traversability scope
          utilityTraversability = utilityTraceConfiguration.getTraversability();
          utilityTraversability.setScope(UtilityTraversabilityScope.JUNCTIONS);
        }
      });
    } catch (Exception e) {
      new Alert(Alert.AlertType.ERROR, "Error loading Utility Network.").show();
    }
  }

  /**
   * Uses the selected parameters to add a barrier expression to the utility trace configuration.
   */
  @FXML
  private void onAddConditionClick() {
    try {
      // get the selected utility network attribute and attribute comparison operator
      UtilityNetworkAttribute selectedAttribute = comparisonSourcesComboBox.getSelectionModel().getSelectedItem();
      UtilityAttributeComparisonOperator selectedOperator =
              comparisonOperatorsComboBox.getSelectionModel().getSelectedItem();

      // check if a comparison value was specified, and capture it to use as the last parameter of the
      // UtilityNetworkAttributeComparison
      // NOTE: since the type of the comparison value is dictated by the selected comparison attribute, we must store
      // it in a generic Object variable and convert it appropriately
      Object otherValue;
      // if a comparison value is selected from the ComboBox, use it as the third parameter
      if (selectedAttribute.getDomain() instanceof CodedValueDomain &&
              comparisonValuesComboBox.getSelectionModel().getSelectedItem() != null) {
        // convert the selected comparison value to the data type defined by the selected attribute
        otherValue = convertObjectDataType(comparisonValuesComboBox.getSelectionModel().getSelectedItem().getCode(),
                selectedAttribute.getDataType());
      } else if (!comparisonValuesTextField.getText().equals("")) {
        // otherwise, a comparison value will be specified as text input to be used as the third parameter
        otherValue = convertObjectDataType(comparisonValuesTextField.getText(), selectedAttribute.getDataType());
      } else {
        new Alert(Alert.AlertType.WARNING, "No valid comparison value entered").show();
        return;
      }

      // create the utility network attribute comparison expression using the specified parameters
      // NOTE: You may also create a UtilityNetworkAttributeComparison with another NetworkAttribute.
      UtilityTraceConditionalExpression expression =
              new UtilityNetworkAttributeComparison(selectedAttribute, selectedOperator, otherValue);

      // check if an expression is already defined for the traversability barriers
      if (utilityTraversability.getBarriers() instanceof UtilityTraceConditionalExpression) {
        UtilityTraceConditionalExpression otherExpression =
                (UtilityTraceConditionalExpression) utilityTraversability.getBarriers();
        // use the existing expression to create an `or` expression with the user-defined expression
        expression = new UtilityTraceOrCondition(otherExpression, expression);
      }

      // set the new expression to the traversability's barriers
      utilityTraversability.setBarriers(expression);

      // show the expression in the text area
      traceConditionsTextArea.setText(expressionToString(expression));

    } catch (Exception e) {
      new Alert(Alert.AlertType.ERROR, "Error adding comparison value.").show();
    }
  }

  /**
   * Parses a utility trace conditional expression into text and returns it.
   *
   * @param expression a UtilityTraceConditionalExpression
   * @return string representing the expression
   */
  private String expressionToString(UtilityTraceConditionalExpression expression) {

    StringBuilder stringBuilder = new StringBuilder();

    // for category comparison expressions, add the category name and comparison operator
    if (expression instanceof UtilityCategoryComparison) {
      UtilityCategoryComparison categoryComparison = (UtilityCategoryComparison) expression;
      stringBuilder.append(
          String.format("'%1$s' %2$s", categoryComparison.getCategory().getName(),
              categoryComparison.getComparisonOperator().name()));
    }

    // for network attribute comparison expressions, add the network attribute name and comparison operator
    else if (expression instanceof UtilityNetworkAttributeComparison) {
      UtilityNetworkAttributeComparison attributeComparison = (UtilityNetworkAttributeComparison) expression;

      UtilityNetworkAttribute utilityNetworkAttribute = attributeComparison.getNetworkAttribute();

      stringBuilder.append(
          String.format("'%1$s' %2$s", utilityNetworkAttribute.getName(),
              attributeComparison.getComparisonOperator().name()));

      if (utilityNetworkAttribute.getDomain() instanceof CodedValueDomain) {
        CodedValueDomain codedValueDomain = (CodedValueDomain) utilityNetworkAttribute.getDomain();

        if (!codedValueDomain.getCodedValues().isEmpty()) {
          // get the data type of the used network attribute comparison
          UtilityNetworkAttribute.DataType attributeComparisonDataType = utilityNetworkAttribute.getDataType();

          // get the coded values from the domain and find the ones where the value matches the network attribute's
          // comparison value
          List<CodedValue> list = codedValueDomain.getCodedValues()
              .stream()
              .filter(value -> convertObjectDataType(value.getCode(), attributeComparisonDataType).equals(
                  convertObjectDataType(attributeComparison.getValue(), attributeComparisonDataType)))
              .collect(Collectors.toList());

          if (!list.isEmpty()) {
            // get the first coded value and add it's name to the string
            CodedValue codedValue = list.get(0);
            stringBuilder.append(String.format(" '%1$s'", codedValue.getName()));
          }
        }

      } else {
        if (attributeComparison.getOtherNetworkAttribute() != null) {
          stringBuilder.append(
              String.format(" '%1$s'", attributeComparison.getOtherNetworkAttribute().getName()));
        } else {
          stringBuilder.append(
              String.format(" '%1$s'", attributeComparison.getValue().toString()));
        }
      }
    }

    // for 'and'/'or' conditions, generate the expression for both sides
    else if (expression instanceof UtilityTraceAndCondition) {
      UtilityTraceAndCondition andCondition = (UtilityTraceAndCondition) expression;
      stringBuilder.append(
          String.format("%1$s AND%n %2$s", expressionToString(andCondition.getLeftExpression()),
              expressionToString(andCondition.getRightExpression())));
    } else if (expression instanceof UtilityTraceOrCondition) {
      UtilityTraceOrCondition orCondition = (UtilityTraceOrCondition) expression;
      stringBuilder.append(
          String.format("%1$s OR%n %2$s", expressionToString(orCondition.getLeftExpression()),
              expressionToString(orCondition.getRightExpression())));
    }

    return stringBuilder.toString();
  }

  /**
   * Builds trace parameters using the constructed trace configurations and runs the trace in the utility network. On
   * completion, shows an alert with the number of found elements.
   */
  @FXML
  private void onTraceClick() {

    try {
      // build utility trace parameters for a subnetwork trace using the prepared starting location
      UtilityTraceParameters utilityTraceParameters =
          new UtilityTraceParameters(UtilityTraceType.SUBNETWORK, Collections.singletonList(startingLocation));

      // set the defined trace configuration to the trace parameters
      utilityTraceParameters.setTraceConfiguration(utilityTraceConfiguration);

      // apply the include barriers/containers settings according to the checkboxes
      utilityTraceParameters.getTraceConfiguration().setIncludeBarriers(includeBarriersCheckBox.isSelected());
      utilityTraceParameters.getTraceConfiguration().setIncludeContainers(includeContainersCheckBox.isSelected());

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
            Alert resultsDialog = new Alert(Alert.AlertType.INFORMATION, elementsFound + " " + "elements found.");
            resultsDialog.setHeaderText("Trace completed successfully.");
            resultsDialog.setTitle("Trace Complete");
            resultsDialog.show();

          } else {
            new Alert(Alert.AlertType.ERROR, "Trace result not a utility element.").show();
          }
        } catch (Exception e) {
          new Alert(Alert.AlertType.ERROR,
              "Error running utility network trace. For a working barrier condition, try \"Transformer Load\" Equal \"15\".")
              .show();
        }
      });
    } catch (Exception e) {
      new Alert(Alert.AlertType.ERROR, "Error building trace parameters / configuration.").show();
    }
  }

  /**
   * Resets the trace configuration and UI back to the state at application start.
   */
  @FXML
  private void onResetClick() {

    // reset the utility trace configuration and traversability to the state at application start
    utilityTraceConfiguration = initialUtilityTraceConfiguration;
    utilityTraversability.setBarriers(initialExpression);

    // show the configuration expression from the application start in the text area
    traceConditionsTextArea.setText(expressionToString(initialExpression));

    // un-check the checkboxes for including barriers and containers
    includeContainersCheckBox.setSelected(false);
    includeBarriersCheckBox.setSelected(false);

    // select the first item in each ComboBox
    comparisonSourcesComboBox.getSelectionModel().select(0);
    comparisonOperatorsComboBox.getSelectionModel().select(0);
  }

  /**
   * Updates the contents of the comparison value choices ComboBox depending on the selected comparison source.
   */
  @FXML
  private void onComparisonSourceChanged() {

    // clear any previous text input
    comparisonValuesTextField.clear();

    if (comparisonSourcesComboBox.getSelectionModel().getSelectedItem() != null) {

      // determine if we need to show a selection of values in the combo box, or a text entry field
      UtilityNetworkAttribute selectedAttribute = comparisonSourcesComboBox.getSelectionModel().getSelectedItem();
      if (selectedAttribute.getDomain() instanceof CodedValueDomain) {

        // populate and show the comparison values combo box
        List<CodedValue> comparisonValues = ((CodedValueDomain) selectedAttribute.getDomain()).getCodedValues();
        comparisonValuesComboBox.getItems().clear();
        comparisonValuesComboBox.getItems().addAll(comparisonValues);
        comparisonValuesComboBox.getSelectionModel().select(0);

      } else {
        comparisonValuesComboBox.getItems().clear();
      }

      // toggle the selection combo box to be visible if it has any items
      comparisonValuesComboBox.setVisible(!comparisonValuesComboBox.getItems().isEmpty());
      // toggle the text field to be hidden if the combo box is visible, or show it if the combo box is invisible
      comparisonValuesTextField.setVisible(!comparisonValuesComboBox.isVisible());
    }
  }

  /**
   * Converts the data type of a provided Object into the data type specified through a UtilityNetworkAttribute.DataType.
   *
   * @param object the Object of which to convert the data type
   * @param dataType the requested data type to which to convert
   * @return the Object with a converted data type
   */
  private Object convertObjectDataType(Object object, UtilityNetworkAttribute.DataType dataType) {

    Object converted = null;

    switch (dataType) {
      case BOOLEAN:
        converted = Boolean.valueOf(object.toString());
        break;
      case DOUBLE:
        converted = Double.valueOf(object.toString());
        break;
      case FLOAT:
        converted = Float.valueOf(object.toString());
        break;
      case INTEGER:
        converted = Integer.parseInt(object.toString());
        break;
    }

    return converted;
  }
}
