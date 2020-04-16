package datamover

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.col

object OracleHelper {

  def truncateIdentifier(identifier: String) = {
    if (identifier.length > 29 && identifier.contains("__")) {
      val subIdentifiers = identifier.split("__") // very specific for target_postgres generated tables and error prone
      val maxLengthSubIdentifier = 30 / subIdentifiers.length
      val subIdentifierTruncate = subIdentifiers.map((ident: String) => {
        if (subIdentifiers.lastIndexOf(ident) == subIdentifiers.length - 1) // Last identifier on the list
          splitTwoWays(ident, maxLengthSubIdentifier)
        else
          ident.substring(0, Integer.min(maxLengthSubIdentifier - 2, ident.length - 2))
      })

      subIdentifierTruncate.mkString("_") + "_"
    }
    else if (identifier.length > 29)
      identifier.substring(0, 29)
    else
      identifier
  }

  def splitTwoWays(identifier: String, maxLength: Integer): String = {
    if (identifier.length < maxLength) return identifier
    val half = maxLength / 2
    if (identifier.length <= half)
      return identifier
    else {
      return identifier.substring(0, half - 1) +
        "_" +
        identifier.substring(identifier.length - half, identifier.length - 1)
    }
  }
}