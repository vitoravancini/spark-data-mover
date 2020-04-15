object JdbcUtils {

  private val JDBC_SQL_SERVER_PATTERN = "sqlserver"
  private val JDBC_POSTGRES_PATTERN = "postgres"
  private val JDBC_FIREBIRD_PATTERN = "firebird"
  private val JDBC_MYSQL_PATTERN = "mysql"
  private val JDBC_ORACLE_PATTERN = "oracle"


  def buildSampledQuery(tableName: String, databaseType: String, sample: Int): String = {
    val tableFromQuery = if (databaseType.contains("oracle"))
      s"(select * from ( select * from $tableName) where ROWNUM <= $sample)"
    else
      s"(select * from $tableName limit $sample)"
    tableFromQuery
  }

  def getJdbcDriver(databaseType: String): String = {
    if (databaseType.toLowerCase().contains(JDBC_SQL_SERVER_PATTERN))
      s"com.microsoft.sqlserver.jdbc.SQLServerDriver"
    else if (databaseType.toLowerCase().contains(JDBC_FIREBIRD_PATTERN))
      "org.firebirdsql.jdbc.FBDriver"
    else if (databaseType.toLowerCase().contains(JDBC_MYSQL_PATTERN))
      "com.mysql.cj.jdbc.Driver"
    else if (databaseType.toLowerCase().contains(JDBC_POSTGRES_PATTERN))
      "org.postgresql.Driver"
    else if (databaseType.toLowerCase().contains(JDBC_ORACLE_PATTERN))
      "oracle.jdbc.driver.OracleDriver"
    else
      throw new IllegalArgumentException(s"driver $databaseType not configured")

  }

}