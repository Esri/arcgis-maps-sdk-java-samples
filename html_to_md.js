// Import Turndown module
var TurndownService = require('turndown')

// Create an instance of the turndown service
var turndownService = new TurndownService()

var converter = require('html-to-markdown');

const fs = require('fs');

function openDir() {
  fs.readdirSync('./').forEach( category => {
    if (fs.lstatSync(category).isDirectory()){
      fs.readdirSync('./'+category).forEach(subfolder => {
        if (fs.lstatSync('./'+category+'/'+subfolder).isDirectory()){
          fs.readdirSync('./'+category+'/'+subfolder).forEach(file => {
            if (getExtension(file) == 'md'){
              filePath = './'+category+'/'+subfolder+'/'+file
              // convertHtmlToMd(filePath);
              replacechars(filePath, '  1. ', '1. ')
              replacechars(filePath, '  2. ', '2. ')
              replacechars(filePath, '  3. ', '3. ')
              replacechars(filePath, '  4. ', '4. ')
              replacechars(filePath, '  5. ', '5. ')
              replacechars(filePath, '  6. ', '6. ')
              replacechars(filePath, '  7. ', '7. ')
              replacechars(filePath, '  8. ', '8. ')
              replacechars(filePath, '  9. ', '9. ')
              replacechars(filePath, '  10. ', '10. ')
            }
          })
        }
      })
    }
  })
}

openDir();


function getExtension(file){
  return file.split('.').pop();
}

function convertHtmlToMd(filePath)
{
  const html = fs.readFileSync(filePath, 'utf-8');
  const markdown = converter.convert(html);
  fs.writeFileSync(filePath, markdown, 'utf-8');
  console.log('readFileSync complete');
}

function replacechars(filePath, find, replace)
{
  const oldFile = fs.readFileSync(filePath, 'utf-8');
  const newFile = replaceAll(oldFile, find, replace);
  fs.writeFileSync(filePath, newFile, 'utf-8');
  console.log('readFileSync complete');
}

function replaceAll(str, find, replace) {
  return str.replace(new RegExp(find, 'g'), replace);
}
