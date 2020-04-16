import datamover.OracleTransformer
import org.apache.spark.sql.types.StringType
import utest._

object OracleTransformerTests extends TestSuite with SparkTestWrapper {
  val tests = Tests {
    'TestOracleWriter - {

      "Truncates column names larger than 30 chars" - {
        import spark.implicits._

        val df = Seq(
          ("jets", "football"),
          ("nacional", "soccer")
        ).toDF("columnlargerthanthirtycharsssherismorethanthirty", "othercolumn")


        val truncated = OracleTransformer.adaptColumnNames()(df)
        assert(truncated.columns(0) == "COLUMNLARGERTHANTHIRTYCHARSSS")
      }
    }
  }
}