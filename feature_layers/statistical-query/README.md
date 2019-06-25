<h1>Statistical Query</h1>

<p>Get aggregated feature statistics for a specified field.</p>

<p><img src="StatisticalQuery.png"/></p>

<h2>How to use the sample</h2>

<p>Check the boxes for the filters you want to use in the query (a spatial filter and an attribute filter). Click 
the "Get Statistics" button to execute the query. A dialog will open with the statistics result.</p>

<h2>How it works</h2>

<p>To query statistics on a <code>FeatureTable</code> field:</p>

<ol>
    <li>Create a <code>ServiceFeatureTable</code> from a URL.</li>
    <li>Create a list of <code>StatisticDefinition</code>s specifying which field to query against, the aggregate 
    type, and an optional alias.</li>
    <li>Create <code>StatisticsQueryParameters</code> passing in the definitions. You can also use the setters to 
    specify an area for the query (geometry) or a custom where clause.</li>
    <li>Call <code>featureTable.queryStatisticsAsync(queryParameters)</code> to make the query.</li>
    <li>Get the <code>StatisticsQueryResult</code> from the <code>ListenableFuture</code>. To see the stastics, 
    iterate through <code>statisticsQueryResult.iterator()</code> to get the <code>StatisticRecord</code>s.</li>
    <li>The map of statistics can be retreived with <code>record.getStatistics()</code> for printing or showing in 
    a list view.</li>
</ol>

<h2>Relevant API</h2>

<ul>
    <li>ArcGISMap</li>
    <li>FeatureLayer</li>
    <li>QueryParameters</li>
    <li>ServiceFeatureTable</li>
    <li>StatisticDefinition</li>
    <li>StatisticRecord</li>
    <li>StatisticType</li>
    <li>StatisticsQueryParameters</li>
    <li>StatisticsQueryResult</li>
</ul>
