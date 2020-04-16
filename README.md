Data mover 1.0 Move data from any source to any destination that spark sql supports
Usage: Data Mover [options]

  -s, --source <value>
                       Source is a source in the form of jdbc connection string, s3 or local file system
                       File:
                          file:///tmp/*/file.csv            // you can use wildcard here
                       S3:
                          s3://indicium-data/raw/*/somefile.json  // you can use wildcard here
                       if source is file type or s3, filetype must be provided
                       Jdbc alternatives and jdbc connections string example:
                          postgres:  jdbc:postgresql://<host>:<port>/<database>?user=<user>&password=<password>
                          oracle:    jdbc:oracle:thin:<user>/<password>@<host>:<port>
                          sqlserver: jdbc:sqlserver://<host>:<port>;database=<database>;user=<user>;password=<password>;


  -t, --filetype <value>   If source is file or s3, filetype must be specified, one of [json,csv,parquet,orc]
  --limit <value>          Number of rows to extract
  --read-options <value>   example for csv: --read_options sep=';' header=true...
  --destination <value>    Destination of the data, can be the same of the source or 'console' for printing the output to the console
  --tables <value>         Tables to read, if jdbc source defined this is required
  --destination-schema <value>
                           Schema to write the table at, if none is specified will be write to default, usually public
  --destination-filetype <value>
                           Type of file to write one of [csv, parquet, json, orc]. If destination is of file this is argument is required
  --destination-table <value>
                           Name of the table to be created, used in conjuction with source filetype and destination jdbc
  --write-options <value>  spark options for writing, example for csv: --write_options sep=';' header=true..., for more options read the docs


oracle limitations:

    Table names and column names are truncated to 30 characters

    String columns are truncated to 255 characteres