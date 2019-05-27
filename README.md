# Mining Framework
Framework for mining git projects.

## Getting Started
* This project uses [Apache Groovy](http://groovy-lang.org/). Install it to execute the program
* It also uses a [Python](https://www.python.org/) script to convert the output to a SOOT compatible format and fetch the project build files. Install the version 3.7.x or newer to run with the SOOT output. 

* If you want to run the tests, you must use the command to clone the repository:
 ``` git clone --recursive https://github.com/spgroup/miningframework ```

## Dependency Injection
This framework uses [Google Guice](https://github.com/google/guice) to deal with dependency injection.

It's necessary to implement three interfaces:
* **Commit Filter** defines conditions (filter) to analyze a commit.
* **Statistics Collector** retrieves commits' metrics for statistical analysis.
* **Data Collector** retrieves the data one wants to study from the commits.
* **Project Processor** does some pre processing in the projects list

The [services/](https://github.com/spgroup/miningframework/tree/master/src/services/) directory contains models for these dependencies. Also, the [MiningModule](https://github.com/spgroup/miningframework/blob/master/src/services/MiningModule.groovy) class acts as the dependency injector.

## Projects List
Another input file is a `.csv` file, that must contain information about the projects to be analyzed. Its lines should have the following structure (similar to the [projects](https://github.com/spgroup/miningframework/blob/master/projects.csv) file):

**output name**,**path**[,**relative**]

Where:
* **output name** refers to the name that should appear in output files;
* **path** is a local path or it's an url of a git project (https://github.com/...);
* **relative** (`true|false`), optional, indicates if **path** is a directory containing multiple projects or it is a project directory. The default is `false`.

## Running
One can run the framework by including `src` in the classpath and executing `src/main/script/MiningFramework.groovy`.

This can be done by configuring an IDE or executing the following command in a terminal:
* Windows/Linux/Mac: `groovy -cp src src/main/script/MiningFramework.groovy [options] [input] [output]`

`[input]` is a mandatory argument and refers to the path of the projects list's file. It's useful to type `--help` in the `[options]` field to see more details, including information about parameterization of the input files.

To get the SOOT framework output format execute the following command:
* Windows/Linux/Mac `groovy -cp src src/main/script/MiningFramework.groovy --post-script "python scripts/parse_to_soot.py [output] " [options] [input] [output]`

To get the build files in the output pass a github token to execution:
* Windows/Linux/Mac `groovy -cp src src/main/script/MiningFramework.groovy --access-key "github-token" [options] [input] [output]`
> Obs: The Github account must be registered in [Travis](https://travis-ci.org/) also. Forks will be created for each project, the builds will be generated via travis, and deployed to the forks github releases

To automatically download the build files, wait for the builds succeced in travis then run the script:
* Windows/Linux/Mac `python scripts/fetch_jars.py <input file> <output path> <github token>`

## Testing
One can the framework tests by including `src` in the classpath and executing `src/test/TestSuite.groovy`

This can be done by configuring an IDE or executing the following command in a terminal:
* Windows/Linux/Mac: `groovy -cp src src/test/TestSuite.groovy`

To create new tests, you have to create a git repository with a merge scenario simulating, add it to the `test_repositories` directory and add it to `src/test/input.csv` like a project and then create the Test class.

## Scripts
* `parse_to_soot.py` - This script receives as input the path to a directory generated by the miningframework, it reads the output files and creates a [output]/data/results-soot.csv with the output in a format suported by a SOOT analysis framework
* `fetch_jars.py`- This script receives as input the path to a framework input file, the path to a directory generated by the miningframework and a github acess token, it downloads the release files from github and moves the files to the directory passed as input.



