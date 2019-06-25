<h1>Symbol Dictionary</h1>

<p>Find symbols within the mil2525d specification that match a keyword.</p>
  
<p><img src="SymbolDictionary.png"/></p>

<h2>Use case</h2>

<p>You can use support for military symbology to allow users to report changes in the field using the correct military symbols.</p>

<h2>How to use the sample</h2>

<p>By default, leaving the fields blank and searching will return all symbols.</p>

<p>To filter symbols, enter text into one or multiple seach boxes and click the Search for Symbols button.</p>

<p>Click the Clear button to clear previous search results.</p>

<h2>How it works</h2>

<ol>
  <li>Create a symbol dictionary with the mil2525d specification by passing the string "mil2525d" to the 
  <code>SymbolDictionary</code> constructor.</li>
  <li>Create <code>StyleSymbolSearchParameters</code>.</li>
  <li>Add members to the names, tags, symbolClasses, categories, and keys list fields of the search parameters.</li>
  <li>Search for symbols using the parameters with <code>symbolDictionary.searchSymbolsAsync(styleSymbolSearchParameters)</code>.</li>
  <li>Get the <code>Symbol</code> from the list of returned <code>StyleSymbolSearchResult</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
  <li>StyleSymbolSearchParameters</li>
  <li>StyleSymbolSearchResult</li>
  <li>Symbol</li>
  <li>SymbolDictionary</li>
</ul>

<h2>Tags</h2>

<p>CIM, MIL-STD-2525B, MIL-STD-2525C, MIL-STD-2525D, defense, look up, mil2525b, mil2525c, mil2525d, military, military
 symbology, symbology</p>

