package datamover

import org.apache.spark.sql.{Dataset, Row, SaveMode}

trait Writer {
  def write(name: String, df: Dataset[Row], destination: Destination)

  def getDestinationTableName(destination: Destination, dfName: String): String = ???
}

object ConsoleWriter extends Writer {
  override def write(name: String, df: Dataset[Row], destination: Destination): Unit = {
    df.show(false)
  }
}

class FileWriter extends Writer {
  override def write(name: String, df: Dataset[Row], destination: Destination): Unit = {
    val writePath = destination.path.split("file://")(1) + name
    val preWrite = df
      .write
      .mode(SaveMode.Overwrite)

    destination.fileType.get match {
      case "json" => preWrite.json(writePath)
      case "orc" => preWrite.orc(writePath)
      case "parquet" => preWrite.parquet(writePath)
      case "csv" => preWrite.csv(writePath)
    }
  }
}

class JdbcWriter extends Writer {

  override def getDestinationTableName(destination: Destination, dfName: String): String = {
    if (destination.tableName.isDefined)
      destination.tableName.get // from file or s3 or oracle Writer
    else
      dfName.split("\\.")(0) // from jdbc, maybe schema.tablename
  }

  override def write(name: String, df: Dataset[Row], destination: Destination): Unit = {
    val saveMode = SaveMode.Overwrite
    val tableName = getDestinationTableName(destination, name)

    val tableAndSchema =
      if (destination.schema.isDefined)
        destination.schema.get + "." + tableName
      else
        tableName

    df.write.mode(saveMode)
      .option("batchsize", 100000)
      .jdbc(
        destination.path,
        tableAndSchema,
        new java.util.Properties()
      )
  }
}

class OracleWriter extends JdbcWriter {
  override def getDestinationTableName(destination: Destination, dfName: String): String = {
    val name = super.getDestinationTableName(destination, dfName)
    OracleHelper.truncateIdentifier(name)
  }
}