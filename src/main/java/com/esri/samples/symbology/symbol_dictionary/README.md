<h1>Symbol Dictionary</h1>

<p>Demonstrates how to search for symbol primitives within a Symbol Dictionary using the mil2525d specification and 
display those symbols.</p>
  
<p><img src="SymbolDictionary.png"/></p>
  
<p>There are five ways to search through a Symbol Dictionary:
  - Name
  - Tags
  - Symbol Class
  - Category
  - Key</p>

<h2>How to use the sample</h2>

<p>By default, leaving the fields blank and hitting search will find all symbols.</p>

<p>To search for certain symbols:
  - enter text into one or multiple seach boxes and hit <code>search for symbols</code>
  - this will seach for symbols containing the information that was entered </p>

<p>Clear button:
  - will clear any previous search results and start fresh</p>

<h2>How it works</h2>

<p>How to search through a <code>SymbolDictionary</code>:</p>

<ol>
  <li>Create a symbol dictionary with the mil2525d specification, <code>SymbolDictionary("mil2525d")</code></li>
  <li>Load the dictionary asynchronously, <code>DictionarySymbol.loadAsync()</code>
    <ul><li>this will allows the application to continue working while the dictionary loads all symbol primitives found within the mil2525d specification</li></ul></li>
  <li>Create search parameters for the dictionary, <code>StyleSymbolSearchParameters()</code>.</li>
  <li>Set the parameters to search through the dictionary.
    <ul><li>Name, <code>StyleSymbolSearchParameters.getNames().add()</code></li>
      <li>Tag, <code>StyleSymbolSearchParameters.getTags().add()</code></li>
      <li>Symbol Class, <code>StyleSymbolSearchParameters.getSymbolClasses().add()</code></li>
      <li>Category, <code>StyleSymbolSearchParameters.getCategories().add()</code></li>
      <li>Key, <code>StyleSymbolSearchParameters.getKeys().add()</code></li></ul></li>
  <li>Search through dictionary using parameters, <code>DictionarySymbol.searchSymbolsAsync(StyleSymbolSearchParameters)</code>.</li>
  <li>Cycle through the <code>StyleSymbolSearchResult</code> list that was returned and display it to screen.
    <ul><li>get <code>CimSymbol</code>,<code>StyleSymbolSearchResult.getSymbol()</code>, and create an image from it, <code>CimSymbol.createSwatchAsync()</code></li>
      <li>use other get methods for the symbol's name, tags, symbolClass, category, and key, like  <code>StyleSymbolSearchResult.getName()</code></li></ul></li>
</ol>

<h2>Features</h2>

<ul>
  <li>CimSymbol</li>
  <li>StyleSymbolSearchParameters</li>
  <li>StyleSymbolSearchResult</li>
  <li>SymbolDictionary</li>
</ul>



