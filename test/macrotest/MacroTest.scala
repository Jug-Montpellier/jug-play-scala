package macrotest

import play.api.libs.json.JsValue
import play.api.libs.json.JsString
//import play.crude.CrudeMacro._
import models._
import play.api.libs.json.Json
import scala.slick.session.Database

object MacroTest extends App {

  implicit val userFormat = Json.format[User]
  implicit val answerFormat = Json.format[Answer]
  val jsValue = JsString("eoeo")

  Database.forURL("jdbc:postgresql:jug", "test", "test", driver = "org.postgresql.Driver") withSession {

   // insert(jsValue, _.validate[User], Users, Json.toJson(_))
  }
}