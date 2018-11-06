<h1>Change Atmosphere</h1>

<p>Define how the sky looks by changing the visual appearance of the atmosphere in a 3D map (scene).</p>

<p><img src="ChangeAtmosphereEffect.gif" alt="Image" /></p>

<h2>How to use the sample</h2>

<p>Select one of the three available atmosphere effect options. The sky will change to display the selected atmosphere effect. </p>

<h2>How it works</h2>

<p>To change the atmosphere effect:</p>

<ol>
<li>Create an <code>ArcGISScene</code> and display it in a <code>SceneView</code>.</li>

<li>Change the atmosphere effect with <code>SceneView.setAtmosphereEffect()</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
<li><code>ArcGISScene</code></li>

<li><code>SceneView</code></li>

<li><code>AtmosphereEffect</code></li>
</ul>

<h2>Tags</h2>

<p>3D, Scene, AtmosphereEffect. </p>

<h2> Additional Information</h2>
There are three effect options:

<ul>
<li><strong> Realistic</strong> -
<li><strong> Horizon only</strong -
<li><strong> None</strong> - No atmosphere effect. The sky is rendered black with a starfield consisting of randomly placed white dots.
