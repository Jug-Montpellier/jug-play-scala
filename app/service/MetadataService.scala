package service

import scala.slick.driver.BasicDriver.simple.Database.threadLocalSession
import scala.collection.mutable.MutableList
import java.sql.Types
import java.sql.ResultSet
import java.sql.DatabaseMetaData

case class Constraints(size: Option[Int], cols: Option[Int], rows: Option[Int])
case class Entity(name: String, tableName: String, uri: String , members: List[Member], props: Map[String, String])
case class Member(name: String, dataType: String, pk: Boolean, nullable: Boolean, autoInc: Boolean, constraints: Constraints, props: Map[String, String]) {
  def option = nullable || autoInc
}

object MetadataService {

  def allTables = {

    
    val metadata = threadLocalSession.metaData
    val tables = metadata.getTables(threadLocalSession.conn.getCatalog(), "public", null, Array("TABLE"))
    val entities = MutableList[Entity]()
    while (tables.next()) {
      
      val entity = dumpTable(metadata, tables.getString("table_name"))
      if(entity != null)
        entities += entity

    }
    entities

  }

  def entityName(tableName: String) = {
    tableName.substring(0, 1).toUpperCase() + tableName.substring(1)
  }
  
  def uri(tableName: String) = {
    tableName match {
      case name if name.endsWith("s") => name.toLowerCase()
      case name if name.endsWith("y") => name.substring(0, name.length()-1) + "ies"
      case name => name.toLowerCase() + "s"
    }
  }

  def columnType(t: Int) = {
    t match {
      case Types.INTEGER => "Int"
      case Types.BIGINT => "Long"
      case Types.VARCHAR => "String"
      case Types.CLOB => "String"
      case Types.BIT => "Boolean"
      case Types.BOOLEAN => "Boolean"
      case Types.TIMESTAMP => "Timestamp"
      case Types.DATE => "Calendar"
      case Types.CHAR => "Char"
    }
  }

  def dumpTable(metadata: DatabaseMetaData, tableName: String): Entity = {

    val rs = metadata.getTables(threadLocalSession.conn.getCatalog(), "public", tableName, null)
    val props = if (rs.next()) {
      getPropertiesFromRemarks(rs.getString("REMARKS"))
    } else {
      Map[String,String]()
    }
    
    if(props.isEmpty)
      return null

    def getPks(): Set[String] = {
      val rs = metadata.getPrimaryKeys(threadLocalSession.conn.getCatalog(), "public", tableName);
      val set = MutableList[String]()
      while (rs.next()) {
        set += rs.getString("COLUMN_NAME")
      }
      set.toSet
    }

    val members = MutableList[Member]()

    val pks = getPks()

    val columns = metadata.getColumns(threadLocalSession.conn.getCatalog(), "public", tableName, null);
    while (columns.next()) {
      val columnName = columns.getString("COLUMN_NAME")
      val remarks = columns.getString("REMARKS")

      val props = getPropertiesFromRemarks(remarks)

      def p(key: String) = {
        props.get(key).map(Integer.parseInt(_))
      }
      val c = Constraints(p("size"), p("cols"), p("rows"))

      members += Member(columnName, columnType(columns.getInt("DATA_TYPE")), pks.contains(columnName), columns.getBoolean("NULLABLE"), columns.getString("IS_AUTOINCREMENT") == "YES", c, props)

    }

    
    
    Entity(entityName(tableName), tableName, uri(tableName), members.toList, props)

  }

  def getPropertiesFromRemarks(remarks: String) = if (remarks == null)
    Map[String, String]()
  else
    remarks.split(",").map(_.trim()).map(_.split("=")).map(a => (a(0) -> a(1))).toMap

}