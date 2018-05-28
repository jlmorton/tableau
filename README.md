# Tableau SDK Wrapper
This utility wraps the Tableau SDK to parse CSV files and convert them into Tableau Data Extracts.

The Tableau Extract API is not thread-safe when inserting a row to the extract, but the work of parsing the CSV and generating a Tableau Row can be multi-threaded.  This utility allows you to specify the number of threads to use when generating an extract.  Inserting rows to the extract is synchronized, so there are diminishing returns to higher thread counts.

Included in the utility is a thin wrapper to publish an extract to Tableau.

# Performance
On my dual-core Macbook Pro, I see the following performance:
```
 1 Thread: 28,286 rows/second
 2 Threads: 40,072 rows/second
 3 Threads: 44,624 rows/second
```
# Dependencies

This library uses the Tableau SDK to create and publish Tableau extracts.  This SDK is not available in Central Maven repositories.  The SDK license allows distribution, but I've chosen to exclude it from this repository.

Instead, there is a small shell script included in this repository, `bin/install_tableau_sdk.sh`.  This shell script will download the SDK, extract it to the `lib` folder within the top-level repository directory, and then `mvn install` the Java dependencies to your local Maven installation.

# Installation
Download the release distribution and unzip.  Run the `install_tableau_sdk.sh` to download the Tableau libraries.

# Building
After installing the Tableau SDK, simply run `mvn install`

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
