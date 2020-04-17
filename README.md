Data mover 1.0 Move data from any source to any destination that spark sql supports

Its already published to docker as techindicium/spark-datamover:v0.1 

Run 

  docker run techindicium/spark-datamover:v0.1 --help

for instructions.

If using pointing to localhost databases, remember to use --network host

oracle limitations:

    Table names and column names are truncated to 30 characters

    String columns are truncated to 255 characteres
