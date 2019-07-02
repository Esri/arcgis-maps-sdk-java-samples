# Symbol Dictionary

Find symbols within the mil2525d specification that match a keyword.
  
![](SymbolDictionary.png)

## Use case

You can use support for military symbology to allow users to report changes in the field using the correct military symbols.

## How to use the sample

By default, leaving the fields blank and searching will return all symbols.

To filter symbols, enter text into one or multiple seach boxes and click the Search for Symbols button.

Click the Clear button to clear previous search results.

## How it works


  1. Create a symbol dictionary with the mil2525d specification by passing the string "mil2525d" to the 
  `SymbolDictionary` constructor.
  2. Create `StyleSymbolSearchParameters`.
  3. Add members to the names, tags, symbolClasses, categories, and keys list fields of the search parameters.
  4. Search for symbols using the parameters with `symbolDictionary.searchSymbolsAsync(styleSymbolSearchParameters)`.
  5. Get the `Symbol` from the list of returned `StyleSymbolSearchResult`.


## Relevant API


*   StyleSymbolSearchParameters
*   StyleSymbolSearchResult
*   Symbol
*   SymbolDictionary


## Tags

CIM, MIL-STD-2525B, MIL-STD-2525C, MIL-STD-2525D, defense, look up, mil2525b, mil2525c, mil2525d, military, military
 symbology, symbology

