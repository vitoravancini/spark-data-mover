package datamover

import org.apache.spark.sql.{Dataset, Row, SparkSession}

trait Reader {
  def read(spark: SparkSession, source: Source): Map[String, Dataset[Row]]

}

object FileReader extends Reader {
  override def read(spark: SparkSession, source: Source): Map[String, Dataset[Row]] = {
    val path = if (source.path.startsWith("s3://"))
      source.path.replace("s3://", "s3a://")
    else
      source.path


    val df = spark.read
      .format(source.fileType.get)
      .options(source.readOptions)
      .load(path)

    val sampledOrNot = if (source.limit != -1) df.limit(source.limit) else df

    Map(path -> sampledOrNot)
  }
}

object JdbcReader extends Reader {
  override def read(spark: SparkSession, source: Source): Map[String, Dataset[Row]] = {
    val driver = JdbcUtils.getJdbcDriver(source.path)
    source.tables.map(table => {

      val tableSampledOrNot = if (source.limit != -1)
        JdbcUtils.buildSampledQuery(table, source.path, source.limit)
      else
        table

      val df = spark.read
        .format("jdbc")
        .option("url", source.path)
        .option("dbtable", tableSampledOrNot)
        .option("driver", driver)
        .load()

      table -> df
    }).toMap

  }
}