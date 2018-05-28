# Tableau SDK Wrapper
This utility wraps the Tableau SDK to parse CSV files and convert them into Tableau Data Extracts.

The Tableau Extract API is not thread-safe when inserting a row to the extract, but the work of parsing the CSV and generating a Tableau Row can be multi-threaded.  This utility allows you to specify the number of threads to use when generating an extract.  Inserting rows to the extract is synchronized, so there are diminishing returns to higher thread counts.

Included in the utility is a thin wrapper to publish an extract to Tableau.

The latest version is 1.0, which is [available here](https://github.com/jlmorton/tableau/releases/download/1.0/tableau-sdk-wrapper-1.0.zip).

# Performance
On my small dual core Macbook Pro, I see the following performance:
```
 1 Thread: 28,286 rows/second
 2 Threads: 40,072 rows/second
 3 Threads: 44,624 rows/second
```
# Platforms
I have tested this on CentOS 7, and OS X High Sierra.  The Tableau SDK supports Fedora 18 and later, CentOS 7 and later, and Ubuntu 12.04 and later.  Support for CentOS 6 was removed from the Tableau SDK in version 10.2 of the SDK.

If you encounter a problem such as `java.lang.UnsatisfiedLinkError: Unable to load library TableauCommon: /lib64/libc.so.6: version 'GLIBC_2.14' not found`, your OS is unfortunately not supported.  Docker containers may help in this situation.

I have not tested this on Windows.  Certainly the various shell scripts will not work, but you should be able to install the SDK for Windows and invoke Java directly.  Any pull requests to add better support for Windows would be appreciated.

# Dependencies

This library uses the Tableau SDK to create and publish Tableau extracts.  This SDK is not available in Central Maven repositories.  The SDK license allows distribution, but I've chosen to exclude it from this repository.

Instead, there is a small shell script included in this repository, `bin/install_tableau_sdk.sh`.  This shell script will download the SDK, extract it to the `lib` folder within the top-level repository directory, and then `mvn install` the Java dependencies to your local Maven installation.

The utility requires Java 8.

# Installation
Download the [current release distribution](https://github.com/jlmorton/tableau/releases/download/1.0/tableau-sdk-wrapper-1.0.zip) and unzip.  Run the `install_tableau_sdk.sh` to download the Tableau libraries.

# Building
After installing the Tableau SDK, simply run `mvn install`

# Schema
This utility expects a schema file which describes the data types in the CSV file.  The schema is in JSON format.  The utility handles strings (`CHAR_STRING`), booleans (`BOOLEAN`), dates (`DATE`), date & times (`DATETIME`), integers (`INTEGER`) and doubles (`DOUBLE`).

Here is a sample schema file:
```
{
  "schemaName": "Sample",
  "schema": {
    "foo": "CHAR_STRING",
    "bar": "INTEGER",
    "baz": "BOOLEAN",
    "bax": "DOUBLE",
    "test": "DATE",
    "test_time": "DATETIME"
  }
}
```

# Usage
```
  usage: java -jar tableau.jar
   -a,--append             Append to existing extract
   -c,--project <arg>      Project name to publish to
   -d,--datasource <arg>   Name of datasource to publish
   -e,--extract <arg>      Filename of extract to publish
   -f,--file <arg>         CSV file to import
   -h,--help
   -n,--username <arg>     Tableau Server username for publishing
   -o,--output <arg>       Output file name, or name of existing extract in
                         append mode
   -p,--publish            Publish an extract to Tableau (requires
                         --extract, --site, --project, --datasource,
                         --username --password, and --url,
   -s,--site <arg>         Tableau site name to publish
   -t,--threads <arg>      Number of threads (default: 1)
   -u,--url <arg>          Tableau Server URL for publishing
   -x,--password <arg>     Tableau Server password for publishing`
 ```
## Creating an Extract
`./bin/extract.sh -o MyExtract.tde -s samples/test.schema -f samples/test.csv -t 2`

## Publishing an Extract
`./bin/publish.sh -e MyExtract.tde -u https://my-tableau-server -n username	-x password -s tableau-site-name -p project-name -d datasource-name`

Note: If you require using a proxy server to publish the extracts, the Tableau SDK resepects the standard `http_proxy` and `https_proxy` environment variables to specify the proxy server.  The SDK also exposes hooks to set the proxy username and password, but this wrapper does not currently implement that.

The Tableau Server user used to publish the SDK must have permission to publish a datasource.

# License
This software is licensed under the Apache 2.0 license.
