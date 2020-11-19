import os
import re
import json
import typing
import argparse
import glob
from pathlib import Path

# region Global sets
# A set of category folder names in current sample viewer.
categories = {
    'analysis',
    'display_information',
    'editing',
    'feature_layers',
    'geometry',
    'group_layers',
    'hydrography',
    'image_layers',
    'kml',
    'local_server',
    'map',
    'map_view',
    'network_analysis',
    'ogc',
    'portal',
    'raster',
    'scene',
    'search',
    'symbology',
    'tiled_layers',
    'utility_network'
}
# endregion

def sub_special_char(string: str) -> str:
    """
    Check and substitute if a string contains special characters.
    :param string: The input string.
    :return: A new string
    """
    regex = re.compile(r'[@_!#$%^&*<>?|/\\}{~:]')
    return re.sub(regex, '', string)

def parse_head(head_string: str) -> (str, str):
    """
    Parse the `Title` section of README file and get the title and description.
    :param head_string: A string containing title, description and images.
    :return: Stripped title and description strings.
    """
    parts = list(filter(bool, head_string.splitlines()))
    if len(parts) < 3:
        raise Exception('README description parse failure!')
    title = parts[0].lstrip('# ').rstrip()
    description = parts[1].strip()
    return title, description

def parse_apis(apis_string: str) -> typing.List[str]:
    """
    Parse the `Relevant API` section and get a list of APIs.
    :param apis_string: A string containing all APIs.
    :return: A sorted list of stripped API names.
    """
    apis = list(filter(bool, apis_string.splitlines()))
    if not apis:
        raise Exception('README Relevant API parse failure!')

    expression = re.compile(r' \(.*\)')
    apis = list(map(lambda x: re.sub(expression, '', x), apis)) # remove text in brackets behind relevant api entries

    return sorted(api.lstrip('*- ') for api in apis)

def parse_tags(tags_string: str) -> typing.List[str]:
    """
    Parse the `Tags` section and get a list of tags.
    :param tags_string: A string containing all tags, with comma as delimiter.
    :return: A sorted list of stripped tags.
    """
    tags = tags_string.split(',')
    if not tags:
        raise Exception('README Tags parse failure!')
    return sorted([tag.strip() for tag in tags])

def get_folder_name_from_path(path: str, index: int = -1) -> str:
    """
    Get the folder name from a full path.
    :param path: A string of a full/absolute path to a folder.
    :return: The folder name.
    """
    return os.path.normpath(path).split(os.path.sep)[index]

def format_category_name_from_folder_name(folder_name: str) -> str:
    """
    Format the category name so that it is as required by the developer site
    and sample viewer.
    e.g.  'map_view' becomes 'Map view', 'scene' becomes 'Scene'.
    :param folder_name: A string of the category folder name.
    :return: A formatted string of the category name.
    """
    if folder_name not in categories:
        raise Exception("Invalid category folder name.")

    if folder_name == "ogc" or folder_name == "kml":
        return folder_name.upper()
    else:
        if "_" not in folder_name:
            return folder_name.capitalize()
        else:
            insert_spaces = folder_name.replace("_", " ")
            return insert_spaces.capitalize()

class MetadataCreator:

    def __init__(self, folder_path: str):
        """
        The standard format of metadata.json. Read more at:
        /common-samples/wiki/README.metadata.json
        """
        self.category = ''          # Populate from folder path.
        self.description = ''       # Populate from README.
        self.ignore = False         # Default to False.
        self.images = []            # Populate from folder paths.
        self.keywords = []          # Populate from README.
        self.redirect_from = ""     # Default to empty string.
        self.relevant_apis = []     # Populate from README.
        self.snippets = []          # Populate from folder paths.
        self.title = ''             # Populate from README.

        self.folder_path = folder_path
        self.folder_name = get_folder_name_from_path(folder_path)
        self.readme_path = os.path.join(folder_path, 'README.md')
        self.json_path = os.path.join(folder_path, 'README.metadata.json')

    def get_source_code_paths(self) -> typing.List[str]:
        """
        Traverse the directory and get all filenames for source code.
        :return: A list of java source code filenames.
        """
        results = []

        paths = Path(self.folder_path).glob('**/*.java')
        for path in paths:
            results.append(os.path.relpath(path, self.folder_path))

        paths = Path(self.folder_path).glob('**/*.fxml')
        for path in paths:
            results.append(os.path.relpath(path, self.folder_path))

        if not results:
            raise Exception('Unable to get java source code paths.')

        results = list(filter(lambda x: 'build/' not in x, results)) # exclude \build folder
        results = list(filter(lambda x: 'out/' not in x, results)) # exclude \out folder
        results = list(filter(lambda x: 'Launcher' not in x, results)) # exclude *Launcher.java
        results = list(map(lambda x: x.replace(os.sep, '/'), results)) # eliminate double backslashes in the paths

        return sorted(results)

    def populate_from_readme(self) -> None:
        """
        Read and parse the sections from README, and fill in the 'title',
        'description', 'relevant_apis' and 'keywords' fields in the dictionary
        for output json.
        """
        try:
            readme_file = open(self.readme_path, 'r')
            # read the readme content into a string
            readme_contents = readme_file.read()
        except Exception as err:
            print(f"Error reading README - {self.readme_path} - {err}.")
            raise err
        else:
            readme_file.close()

        # Use regex to split the README by exactly 2 pound marks, so that they
        # are separated into paragraphs.
        pattern = re.compile(r'^#{2}(?!#)\s(.*)', re.MULTILINE)
        readme_parts = re.split(pattern, readme_contents)
        try:
            api_section_index = readme_parts.index('Relevant API') + 1
            tags_section_index = readme_parts.index('Tags') + 1
            self.title, self.description = parse_head(readme_parts[0])
            self.relevant_apis = parse_apis(readme_parts[api_section_index])
            keywords = parse_tags(readme_parts[tags_section_index])
            # De-duplicate API names in README's Tags section.
            self.keywords = [w for w in keywords if w not in self.relevant_apis]

            # "It combines the Tags and the Relevant APIs in the README."
            # See /runtime/common-samples/wiki/README.metadata.json#keywords
            self.keywords += self.relevant_apis
        except Exception as err:
            print(f'Error parsing README - {self.readme_path} - {err}.')
            raise err

        # extract the image hyperlink to get the image file name
        try:
        ## save the image file name
            image_link = re.search('\\!\\[.*\\]\\(.*\\.*\\)', readme_contents).group(0)
            image_name = re.sub(r'\!\[.*\]\(', '', str(image_link))
            image_name = re.sub(r'\)', '', image_name)
            self.images = [image_name]
        except Exception as err:
            print(f'Error getting image path from README - {self.readme_path} - {err}.')
            raise err

    def populate_from_paths(self) -> None:
        """
        Populate category name and snippets from a sample's folder.
        """
        try:
            category_folder = get_folder_name_from_path(self.folder_path, -2)
            self.category = format_category_name_from_folder_name(category_folder)
        except Exception as err:
            print(f'Error getting category folder name from path - {self.folder_path} - {err}')
            raise err

        try:
            self.snippets = self.get_source_code_paths()
            self.snippets = sorted(self.snippets, key=str.lower)
        except Exception as err:
            print(f"Error parsing paths - {self.folder_name} - {err}.")
            raise err

    def flush_to_json_string(self) -> str:
        """
        Write the metadata to a json string.
        :return: json string
        """
        data = dict()

        data["category"] = self.category
        data["description"] = self.description
        data["ignore"] = self.ignore
        data["images"] = self.images
        data["keywords"] = self.keywords
        data["redirect_from"] = self.redirect_from
        data["relevant_apis"] = self.relevant_apis
        data["snippets"] = self.snippets
        data["title"] = self.title

        return json.dumps(data, indent=4, sort_keys=True)

def compare_one_metadata(folder_path: str):
    """
    A handy helper function to create 1 sample's metadata by running the script
    without passing in arguments, and write to a separate json for comparison.
    The path may look like
    '~/arcgis-runtime-samples-java/analysis/analyze-hotspots'
    """
    single_updater = MetadataCreator(folder_path)
    try:
        single_updater.populate_from_readme()
        single_updater.populate_from_paths()

    except Exception as err:
        print(f'Error populate failed for - {single_updater.folder_name}.')
        raise err

    json_path = os.path.join(folder_path, 'README.metadata.json')

    try:
        json_file = open(json_path, 'r')
        json_data = json.load(json_file)
    except Exception as err:
        print(f'Error reading JSON - {folder_path} = {err}')
        raise err
    else:
        json_file.close()

    # Specific check to ensure redirect_from is not [""] which breaks the website build
    if json_data['redirect_from'] != [""]:
        single_updater.redirect_from = json_data['redirect_from']
    else:
        raise Exception('Error in metadata: redirect_from cannot be [""]')

    new = single_updater.flush_to_json_string()
    original = json.dumps(json_data, indent=4, sort_keys=True)
    if new != original:
        raise Exception(f'Error inconsistent metadata - {folder_path}')

def all_samples(path: str):
    """
    Run the check on all samples.

    :param path: The path to root Java samples folder.
    :return: None. Throws if exception occurs.
    """
    exception_count = 0
    for root, dirs, files in os.walk(path):
        # Get the sample directories and directories to ignore
        directories = [f for f in os.listdir(path) if os.path.isdir(os.path.join(path, f))]
        ignored_folders = ['.git', '.github', '.gradle', '.idea', 'gradle']
        # If parent folder name is a valid category name.
        for category in directories:
          if (category not in ignored_folders and category in categories):
            for dir_name in dirs:
                sample_path = os.path.join(root, dir_name)
                try:
                    compare_one_metadata(sample_path)
                except Exception as err:
                    exception_count += 1
                    print(f'{exception_count}. {err}')

    # Throw once if there are exceptions.
    if exception_count > 0:
        raise Exception('Error(s) occurred during checking all samples.')

def main():
    # Initialize parser.
    msg = 'Check metadata style. Run it against the samples repo root, or a single sample folder. ' \
          'On success: Script will exit with zero. ' \
          'On failure: Title inconsistency will print to console and the ' \
          'script will exit with non-zero code.'
    parser = argparse.ArgumentParser(description=msg)
    parser.add_argument('-a', '--all', help='path to the samples repo root')
    parser.add_argument('-s', '--single', help='path to a single sample')
    args = parser.parse_args()

    if args.single:
        try:
            compare_one_metadata(args.single)
        except Exception as err:
            raise err
    elif args.all:
        try:
            all_samples(args.all)
        except Exception as err:
            raise err
    else:
        raise Exception('Invalid arguments, abort.')

if __name__ == '__main__':
    try:
        main()
    except Exception as error:
        print(f'{error}')
        exit(1)
