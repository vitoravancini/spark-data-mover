<p align="center">
  <img src="etc/data_mover.jpeg" alt="Data Mover logo"/>
</p>

**Data Mover** enables your team to move data around with no waste of time.

## Understanding Data Mover

**Data mover 1.0** moves data **from** any source **to** any destination that **SparkSQL** supports

* Get data from Amazon S3 and load it to a database
* Dump a defined number of rows of a table from a database
* Load a csv file into a database or S3

## Getting started

Data Mover is already published to docker as techindicium/spark-datamover:v0.1
For more instructions and help, run:

`docker run techindicium/spark-datamover:v0.1 --help`

## Example

* Loading a csv file to a PostgresSQL at localhost

```
docker run --network host techindicium/spark-datamover:v0.1 -s file:///home/path/your_file.csv csv 
--destination "jdbc:postgresql://localhost:PORT/DATABASE?user=USERNAME&password=PASSWD" --destination-table MY_DEST_TABLE
```

* Notes

1. If your database is in your localhost, it is necessary to set the argument `--network host` after `docker run`
2. The destination path **must be** inside quotes

## Oracle limitations: 

-    Table names and column names are truncated to 30 characters

-    String columns are truncated to 255 characteres


## Reporting bugs and contributing code

-   Want to report a bug or request a feature? Open [an issue](https://github.com/vitoravancini/spark-data-mover/issues/new).