import os
import re
import json
import typing
import argparse
import glob
from pathlib import Path


def check_special_char(string: str) -> bool:
    """
    Check if a string contains special characters.
    :param string: The input string.
    :return: True if there are special characters.
    """
    regex = re.compile('[@_!#$%^&*<>?|/\\}{~:]')
    if not regex.search(string):
        return False
    return True


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


def get_folder_name_from_path(path: str) -> str:
    """
    Get the folder name from a full path.
    :param path: A string of a full/absolute path to a folder.
    :return: The folder name.
    """
    return os.path.normpath(path).split(os.path.sep)[-1]


class MetadataUpdater:

    def __init__(self, folder_path: str):
        """
        The standard format of metadata.json. Read more at:
        https://devtopia.esri.com/runtime/common-samples/wiki/README.metadata.json
        """
        self.category = ''          # Populate from json.
        self.description = ''       # Populate from README.
        self.ignore = False         # Default to False.
        self.images = []            # Populate from folder paths.
        self.keywords = []          # Populate from README.
        self.redirect_from = []     # Populate from json.
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

    def populate_from_json(self) -> None:
        """
        Read 'category' and 'redirect_from' fields from json, as they should
        not be changed.
        """
        try:
            json_file = open(self.json_path, 'r')
            json_data = json.load(json_file)
        except Exception as err:
            print(f'Error reading JSON - {self.json_path} - {err}')
            raise err
        else:
            json_file.close()

        keys = json_data.keys()
        for key in ['category']:
            if key in keys:
                setattr(self, key, json_data[key])
        if 'redirect_from' in keys:
            if isinstance(json_data['redirect_from'], str):
                self.redirect_from = [json_data['redirect_from']]
            elif isinstance(json_data['redirect_from'], typing.List):
                self.redirect_from = json_data['redirect_from']
            else:
                print(f'No redirect_from in - {self.json_path}, abort.')

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
            if check_special_char(self.title + self.description):
                print(f'Info: special char in README - {self.folder_name}')
            self.relevant_apis = parse_apis(readme_parts[api_section_index])
            self.relevant_apis = sorted(self.relevant_apis, key=str.lower)
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
        Populate source code and image filenames from a sample's folder.
        """
        try:
            self.snippets = self.get_source_code_paths()
            self.snippets = sorted(self.snippets, key=str.lower)
        except Exception as err:
            print(f"Error parsing paths - {self.folder_name} - {err}.")
            raise err

    def flush_to_json(self, path_to_json: str) -> None:
        """
        Write the metadata to a json file.
        :param path_to_json: The path to the json file.
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

        with open(path_to_json, 'w+') as json_file:
            json.dump(data, json_file, indent=4, sort_keys=True)
            json_file.write('\n')


def update_1_sample(path: str):
    """
    A handy helper function to fix 1 sample's metadata by running the script
    without passing in arguments.
    The path may look like
    '~/arcgis-runtime-samples-java/analysis/analyze-hotspots'
    """
    single_updater = MetadataUpdater(path)
    try:
        single_updater.populate_from_json()
        single_updater.populate_from_readme()
        single_updater.populate_from_paths()
    except Exception:
        print(f'Error populate failed for - {single_updater.folder_name}.')
        return
    single_updater.flush_to_json(os.path.join(path, 'README.metadata.json'))

def update_category(category_root_dir: str):
    category_name = get_folder_name_from_path(category_root_dir)
    print(f'Processing category - `{category_name}`...')
    for root, dirs, files in os.walk(category_root_dir):
        for dir_name in dirs: ## sample directories
            current_path = os.path.join(root, dir_name)
            files = os.listdir(current_path)
            if ("README.metadata.json" in files): ## only process if there is a metadata.json file in the folder
                updater = MetadataUpdater(current_path)
                if (updater.readme_path):
                    try:
                        updater.populate_from_json()
                        updater.populate_from_readme()
                        updater.populate_from_paths()
                    except Exception:
                        print(f'Error populate failed for - {updater.folder_name}.')
                        continue
                    updater.flush_to_json(updater.json_path)
                    print(f'Successfully updated README.metadata.json: {updater.folder_name}')

def update_all_categories(path: str):
    directories = [f for f in os.listdir(path) if os.path.isdir(os.path.join(path, f))]
    ignored_folders = ['.git', '.github', '.gradle', '.idea', 'gradle']
    for category in directories:
        if (category not in ignored_folders):
            update_category(os.path.join(path, category))
            print(f'Category {category} successfully updated!')

def main():
    # Initialize parser.
    msg = 'Metadata helper script. Run it against the samples repo root, top level folder of a ' \
          'category or a single sample.'
    parser = argparse.ArgumentParser(description=msg)
    parser.add_argument('-a', '--all', help='path to the samples repo root')
    parser.add_argument('-c', '--cat', help='path to a category')
    parser.add_argument('-s', '--single', help='path to a single sample')
    args = parser.parse_args()

    if args.all:
        # Updates all categories
        update_all_categories(args.all)
    elif args.cat:
        # Updates a category.
        update_category(args.cat)
    elif args.single:
        # Updates one sample.
        update_1_sample(args.single)
    else:
        print('Invalid arguments, abort.')


if __name__ == '__main__':
    main()