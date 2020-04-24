<p align="center">
  <img src="etc/data_mover.jpeg" alt="Data Mover logo"/>
</p>

**Data Mover** enables your team to move data around with no waste of time.

## Understanding Data Mover

**Data Mover 1.0** moves data **from** any source **to** any destination that **SparkSQL** supports

* Get data from Amazon S3 and load it to a database
* Dump a defined number of rows of a table from a database
* Load a csv file into a database or S3

## Getting started

Data Mover is already published to docker as techindicium/spark-datamover:v0.1.
For more instructions and help, run:

`docker run techindicium/spark-datamover:v0.1 --help`

## ExampleS

* Loading a csv file from AWS S3 to a PostgreSQL database

1. Environment variables are defined via the `-e` argument;

2. CSV options such as header, sep etc. can be defined in the `--read-options` argument;

```
docker run -e AWS_SECRET_ACCESS_KEY=MY_SECRET_KEY -e AWS_ACCESS_KEY_ID=MY_ACCESS_KEY techindicium/spark-datamover:v0.1 -s s3://folder/subfolder/subfolder/file.csv --filetype csv --read-options header=true --destination "jdbc:postgresql://localhost:PORT/DATABASE?user=USERNAME&password=PASSWD" --destination-table MY_DEST_TABLE
```

* Loading a csv file to a PostgresSQL at localhost

```
docker run --network host techindicium/spark-datamover:v0.1 -s /home/path/your_file.csv --filetype csv --destination "jdbc:postgresql://localhost:PORT/DATABASE?user=USERNAME&password=PASSWD" --destination-table MY_DEST_TABLE
```


* Notes

1. If your database is in your localhost, it is necessary to set the argument `--network host` after `docker run`

2. The destination path **must be** inside quotes

* Getting a csv file from a table in Postgres

```
docker run --network host -v /home/user:/home/user techindicium/spark-datamover:v0.1 -s "jdbc:postgresql://localhost:PORT/DATABASE?user=USERNAME&password=PASSWD" --limit 100 --destination /home/path/my_table.csv --tables my_table --destination-filetype csv

```

* Notes

1. In order to get a csv file, it is necessary the -v argument after `docker run`. It is responsible for mapping the volumes of your pc and docker.


## Sampling

  User can define how many lines to read from the specified source with --limit

## JDBC specifics: 

  Write and read options can be specified with --write-options k=v,k1=v1 and --read-options k=v,k1=v1

  Besides the options that the spark data sources allows for reading and writing ( you can check them at the respective docs: https://spark.apache.org/docs/latest/sql-data-sources-jdbc.html), you can specify save-mode as append or overwrite

  e.g.: 

  ```
  docker run --network host techindicium/spark-datamover:v0.1 -s /home/path/your_file.csv --filetype csv --destination "jdbc:postgresql://localhost:PORT/DATABASE user=USERNAME&password=PASSWD" --destination-table MY_DEST_TABLE --write-options save-mode=append
  ```


## Oracle limitations: 

-    Table names and column names are truncated to 30 characters

-    String columns are truncated to 255 characteres


## Reporting bugs and contributing code

-   Want to report a bug or request a feature? Open [an issue](https://github.com/vitoravancini/spark-data-mover/issues/new).