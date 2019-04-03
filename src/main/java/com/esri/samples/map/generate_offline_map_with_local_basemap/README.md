<h1>Generate offline map with local basemap</h1>

<p>Take a web map offline, but instead of downloading an online basemap, use one which is already on the device.</p>

<p><img src="GenerateOfflineMapWithLocalBasemap.png"/></p>

<h2>Use case</h2>

<p>There are a number of use-cases where you may wish to use a basemap which is already on the device, rather than downloading:</p>

<ul>
<li>You want to limit the total download size.</li>

<li>You want to be able to share a single set of basemap files between many offline maps.</li>

<li>You want to use a custom basemap (for example authored in ArcGIS Pro) which is not available online.</li>

<li>You do not wish to sign into ArcGIS.com in order to download Esri basemaps.</li>
</ul>

<p>The author of a web map can support the use of basemaps which are already on a device by configuring the web map to specify the name of a suitable basemap file. This could be a basemap which:</p>

<ul>
<li>Has been authored in ArcGIS Pro to make use of your organizations custom data.</li>

<li>Is available as a PortalItem which can be downloaded once and re-used many times.</li>
</ul>

<h2>How to use the sample</h2>

<ol>
<li>Click on the "Take Map Offline" button.</li>

<li>You will be prompted to choose whether you wish to use the "naperville_imagery.tpk" basemap (already saved in the samples-data directory) or to download the online basemap.</li>

<li>If you choose to download the online basemap, the offline map will be generated with the same (topographic) basemap as the online web map. To download the Esri basemap, you may be prompted to sign in to ArcGIS.com.</li>

<li>If you choose to use the basemap from the device, the offline map will be generated with the local imagery basemap i.e. no tiles are exported or downloaded. Since the application is not exporting online ArcGIS Online basemaps you will not need to log-in.</li>
</ol>

<h2>How it works</h2>

<ol>
<li>Create an <code>ArcGISMap</code> with a portal item pointing to the web map.</li>

<li>Create <code>GenerateOfflineMapParameters</code> specifying the download area geometry, min scale, and max scale.</li>

<li>Once the generate offline map parameters are created, check the <code>getReferenceBasemapFilename()</code> property. The author of an online web map can configure this setting to indicate the name of a suitable basemap. In this sample, the app checks the local device for the suggested "naperville_imagery.tpk" file.</li>

<li>If the user chooses to use the basemap on the device, use <code>setReferenceBasemapDirectory()</code> on the generate offline map parameters to set the absolute path of the directory which contains the .tpk file. If this property is set, no online basemap will be downloaded and instead, the mobile map will be created with a reference to the .tpk on the device.</li>

<li>A <code>GenerateOfflineMapJob</code> is created by calling <code>offlineMapTask.generateOfflineMap</code> passing the parameters and the download location for the offline map.</li>

<li>Create the offline map job and start it.</li>

<li>When the job is done, use <code>getOfflineMap</code> on the <code>GenerateOfflineMapResult</code> object to get the map.</li>
</ol>

<h2>Relevant API</h2>

<ul>
<li>OfflineMapTask</li>

<li>GenerateOfflineMapParameters</li>

<li>GenerateOfflineMapParameterOverrides</li>

<li>GenerateOfflineMapJob</li>

<li>GenerateOfflineMapResult</li>
</ul>

<h2>Tags</h2>

<p>offline, local basemap, OfflineMapTask</p>