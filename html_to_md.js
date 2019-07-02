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
              replacechars(filePath, '  \\*', '\*')
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
