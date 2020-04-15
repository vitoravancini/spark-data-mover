import org.apache.spark.sql.{Dataset, Row, SaveMode}

trait Writer {
  def write(name: String, df: Dataset[Row], destination: Destination)
}

object ConsoleWriter extends Writer {
  override def write(name: String, df: Dataset[Row], destination: Destination): Unit = {
    df.show(false)
  }
}

object FileWriter extends Writer {
  override def write(name: String, df: Dataset[Row],destination: Destination): Unit = {
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

object JdbcWriter extends Writer {
  override def write(name: String, df: Dataset[Row], destination: Destination): Unit = {
    val saveMode = SaveMode.Overwrite
    val tableName = name.split("\\.")(0)

    val tableAndSchema =
      if (destination.schema.isDefined)
        destination.schema + "." + tableName
      else
        tableName

    df.write.mode(saveMode).jdbc(
      destination.path,
      tableAndSchema,
      new java.util.Properties
    )
  }
}