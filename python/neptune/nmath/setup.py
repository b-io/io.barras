#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

##########################################################################################
# Goal
#   Provide the build script for setuptools.
##########################################################################################

from os import path

from setuptools import find_packages, setup

## SETUP CONSTANTS #######################################################################

__SETUP_CONSTANTS___________________________________________ = ""

NAME = "nmath"
VERSION = "1.0.0.post137"
DESCRIPTION = "Mathematical utility library"
DIR = path.abspath(path.dirname(__file__))
with open(path.join(DIR, "README.md"), encoding="utf-8") as f:
    LONG_DESCRIPTION = f.read()

AUTHOR = "Florian Barras"
AUTHOR_EMAIL = "florian@barras.io"
LICENSE = "MIT"
LICENSE_FILES = ["LICENSE"]
URL = "https://github.com/b-io/io.barras/tree/master/python/neptune/nmath"

PACKAGES = ["ngui", "nutil", "scipy"]


## SETUP #################################################################################

__SETUP_____________________________________________________ = ""

setup(
    # The project name
    # Note that there are some restrictions on what makes a valid project name:
    #   https://packaging.python.org/specifications/core-metadata/#name
    name=NAME,  # required
    # The project version
    # Note that the version should comply with PEP 440:
    #   https://www.python.org/dev/peps/pep-0440/
    # For a discussion on single-sourcing the version across setup.py and the project code, see:
    #   https://packaging.python.org/en/latest/single_source_version.html
    version=VERSION,  # required
    # The project one-line description (tagline)
    # Note that this corresponds to the "Summary" metadata field:
    #   https://packaging.python.org/specifications/core-metadata/#summary
    description=DESCRIPTION,  # optional
    # The project long description (README)
    # Note that this corresponds to the "Description" metadata field:
    #   https://packaging.python.org/specifications/core-metadata/#description-optional
    long_description=LONG_DESCRIPTION,  # optional
    # The content type of the project long description (either "text/plain", "text/x-rst" or
    # "text/markdown")
    # Note that this corresponds to the "Description-Content-Type" metadata field:
    #   https://packaging.python.org/specifications/core-metadata/#description-content-type-optional
    long_description_content_type="text/markdown",  # optional
    # The project author
    author=AUTHOR,  # optional
    # The email address of the project author
    author_email=AUTHOR_EMAIL,  # optional
    # The email address of the project author
    license=LICENSE,  # optional
    # The email address of the project author
    license_files=LICENSE_FILES,  # optional
    # The project main homepage
    # Note that this corresponds to the "Home-Page" metadata field:
    #   https://packaging.python.org/specifications/core-metadata/#home-page-optional
    url=URL,  # optional
    # The project classifiers
    #
    # For a `list` of valid classifiers, see:
    #   https://pypi.org/classifiers/
    classifiers=[  # optional
        # The project maturity
        "Development Status :: 4 - Beta",
        # The project audience
        "Intended Audience :: Developers",
        # The project platform
        "Operating System :: OS Independent",
        # The Python versions supported by the project
        "Programming Language :: Python :: 3",
        "Programming Language :: Python :: 3.8",
        "Programming Language :: Python :: 3.9",
        "Programming Language :: Python :: 3.10",
        "Programming Language :: Python :: Implementation :: CPython",
        "Programming Language :: Python :: Implementation :: PyPy",
        # The project topics
        "Topic :: Scientific/Engineering :: Mathematics",
        "Topic :: Utilities",
    ],
    # The project keywords
    keywords=["mathematics", "statistics", "utilities"],  # optional
    # The path to the project packages
    package_dir={"": "source"},  # optional
    # The project package directories (use find_packages())
    # Note that, alternatively, to distribute a single Python file, the "py_modules" argument can be
    # Used instead as follows, which will expect a file called "modules.py" to exist:
    # Py_modules=['modules'],
    packages=find_packages(),  # required
    # The Python versions supported by the project
    python_requires=">=3.8",  # required
    # The project dependencies
    install_requires=PACKAGES,  # optional
    # The environment-specific project dependencies
    # Note that the users will be able to install these additional dependencies using the "extras" syntax as follows:
    #   $ pip install nmath[dev]
    extras_require={"dev": ["check-manifest"], "test": ["coverage"]},  # optional
    # The list of project resources (included in the packages) that need to be installed
    package_data={"common": ["resources/common.properties"]},  # optional
    # The list of project resources (not included in the packages) that need to be installed
    # Note that although "package_data" is the preferred approach, in some case you may need to
    # Place resources outside of the packages. See:
    # Http://docs.python.org/distutils/setupscript.html#installing-additional-files
    data_files=[],  # optional
    # The platform-specific project entry points (in preference to the "scripts" keyword)
    # Note that these entry points provide cross-platform support and allow "pip" to create the
    # Appropriate form of executable for the target platform
    entry_points={  # optional
        "console_scripts": ["main=nmath:main"],
    },
    # The list of project URLs
    # Note that this corresponds to the "Project-URL" metadata fields:
    #   https://packaging.python.org/specifications/core-metadata/#project-url-multiple-use
    project_urls={  # optional
        "Bug Tracker": "https://github.com/b-io/io.barras/issues",
        "Documentation": "https://repo.barras.io",
        "Source Code": URL,
    },
)
