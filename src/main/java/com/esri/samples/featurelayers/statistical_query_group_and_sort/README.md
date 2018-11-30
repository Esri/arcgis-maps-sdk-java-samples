<h1>Statistical Query Group And Sort</h1>

<p>Group and sort feature statistics by different fields.</p>

<p><img src="StatisticalQueryGroupAndSort.png"></p>

<h2>How to use the sample</h2>

<p>The sample will start with some default options selected. You can immediately click the "Get Statistics" button to
 see the results for these options.</p>

<p>To change the statistic definitions, you can add statistic definitions to the top-left table using the combo boxes 
and "Add button". Select a table row and click "Remove" to remove the statistic definition.</p>

<p>To change the group-by fields, check the box by the field you want to group by in the bottom-left list view.</p>

<p>To change the order-by fields, select a group by field (it must be checked) and click the ">>" button to add it to
 the Order By table. To remove a field from the Order by table, select it and click the "<<" button. To change the 
 sort order of the order-by field, click on a cell in the Sort Order column to edit it using a ComboBox.</p>
 
 <h2>How it works</h2>
 
 <p>To query statistics from a feature table:</p>
 
 <ol>
    <li>Create and load a <code>ServiceFeatureTable</code>.</li>
    <li>Get the feature tables field names list with <code>featureTable.getFields()</code>.</li>
    <li>Create <code>StatisticDefinition</code>s specifying the field to compute statistics on and the 
    <code>StatisticType</code> to compute.</li>
    <li>Create <code>StatisticsQueryParameters</code> passing in the list of statistic definitions.</li>
    <li>To have the results grouped by fields, add the field names to the query parameters' 
    <code>groupByFieldNames</code> collection.</li>
    <li>To have the results ordered by fields, create <code>OrderBy</code>s, specifying the field name and 
    <code>SortOrder</code>. Pass these <code>OrderBy</code>s to the parameters' <code>orderByFields</code> 
    collection.</li>
    <li>To execute the query, call <code>featureTable.queryStatisticsAsync(queryParameters)</code></li>
    <li>Get the <code>StatisticQueryResult</code>. From this, you can get an iterator of 
    <code>StatisticRecord</code>s to loop through and display.</li>
 </ol>
 
 <h2>Relevant API</h2>
 
 <ul>
    <li>Field</li>
    <li>QueryParameters</li>
    <li>ServiceFeatureTable</li>
    <li>StatisticDefinition</li>
    <li>StatisticRecord</li>
    <li>StatisticType</li>
    <li>StatisticsQueryParameters</li>
    <li>StatisticsQueryResult</li>
 </ul>
