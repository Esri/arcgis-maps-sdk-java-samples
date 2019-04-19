# Symbol Dictionary

Find symbols within mil2525d specification that match a keyword.

![](SymbolDictionary.png)

There are five ways to search through a Symbol Dictionary: - Name - Tags

  - Symbol Class - Category - Key

## How to use the sample

By default, leaving the fields blank and hitting search will find all
symbols.

To search for certain symbols: - enter text into one or multiple seach
boxes and hit `search for symbols` - this will seach for symbols
containing the information that was entered

Clear button: - will clear any previous search results and start fresh

## How it works

How to search through a `SymbolDictionary`:

1.  Create a symbol dictionary with the mil2525d specification,
    `SymbolDictionary(“mil2525d”)`
2.  Load the dictionary asynchronously, `DictionarySymbol.loadAsync()`
      - this will allows the application to continue working while the
        dictionary loads all symbol primitives found within the mil2525d
        specification
3.  Create search parameters for the dictionary,
    `StyleSymbolSearchParameters()`.
4.  Set the parameters to search through the dictionary.
5.  Name, `StyleSymbolSearchParameters.getNames().add()`
6.  Search through dictionary using parameters,
    `DictionarySymbol.searchSymbolsAsync(StyleSymbolSearchParameters)`.
7.  Cycle through the `StyleSymbolSearchResult` list that was returned
    and display it to screen.
8.  get `CimSymbol`,`StyleSymbolSearchResult.getSymbol()`, and create an
    image from it, `CimSymbol.createSwatchAsync()`
