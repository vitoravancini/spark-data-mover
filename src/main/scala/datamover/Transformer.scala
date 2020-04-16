package datamover

import datamover.Types.Transformation
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.DoubleType


trait Transformer {
  def trigger(source: Source, destination: Destination): Boolean

  def transformations(): Seq[Transformation]
}

object PostgresTransformer extends Transformer {

  override def trigger(source: Source, destination: Destination) = {
    destination.destinationType == "jdbc" && destination.path.contains("postgres")
  }

  override def transformations() = Seq(adaptColumnNames(), changeNonIntegerToDouble())

  def adaptColumnNames()(df: DataFrame): DataFrame = {
    val columns = df.schema
      .map(_.name)

    columns.foldLeft(df)((df, colName) => {
      val name = colName.toLowerCase()
        .replace(" ", "_")
        .replace("(", "_")
        .replace(")", "_")

      df.withColumnRenamed(colName, name.toLowerCase())
    })
  }

  def changeNonIntegerToDouble()(df: DataFrame) = {
    val decimalColumns = df.schema
      .filter(col => col.dataType.typeName.toLowerCase.contains("(decimal|float|double)"))
      .map(_.name)

    decimalColumns.foldLeft(df)((df, colName) => {
      df.withColumn(colName, col(colName).cast(DoubleType))
    })
  }
}

object OracleTransformer extends Transformer {


  override def trigger(source: Source, destination: Destination): Boolean =
    destination.destinationType == "jdbc" && destination.path.contains("oracle")

  override def transformations(): Seq[Transformation] = Seq(adaptColumnNames(), truncateStringForOracle())

  def adaptColumnNames()(df: DataFrame) = {
    val columns = df.schema
      .map(_.name)

    columns.foldLeft(df)((df, colName) => {
      val oracleTruncated = OracleHelper.truncateIdentifier(colName.toUpperCase())
      df.withColumnRenamed(colName, oracleTruncated)
    })
  }

  def truncateStringForOracle()(df: DataFrame) = {
    val textColumns = df.schema
      .filter(col => {
        col.dataType.typeName.toLowerCase.contains("string")
      })
      .map(_.name)

    textColumns.foldLeft(df)((df, colName) => {
      df.withColumn(colName, col(colName).substr(0, 253))
    })
  }
}




