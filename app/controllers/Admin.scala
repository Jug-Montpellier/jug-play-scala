package controllers

import scala.slick.driver.BasicDriver.simple.Database.threadLocalSession
import play.api.mvc._
import securesocial.core.{ Identity, Authorization }
import play.Logger
import models.Test
import models.Tests
import play.api.libs.json._
import scala.collection.mutable.MutableList
import service.MetadataService
import service.Entity
import service.Member
import service.Constraints
import service.AuthorizationService
import models._

object Admin extends Controller
  with securesocial.core.SecureSocial
  with DBSession
  with JsonAction {

  implicit val cosntaintFormat = Json.format[Constraints]
  implicit val memberFormat = Json.format[Member]
  implicit val entityFormat = Json.format[Entity]

  def index = SecuredAction(AuthorizationService.HasAdminRole) {
    implicit request =>
      Ok(views.html.admin.index())
  }

  def testsInsert =
    SecuredJsonAction(OnlyMe("olivier.nouguier@gmail.com")) {
      json =>
        json.validate[Test].map {
          (test) =>
            withSession {
              Tests.autoInc.insert(test.name, test.nickname)
              Ok(Json.toJson(test))
            }
        }

    }

  import play.crude.CrudeMacro._

  //  def zozo(json: JsValue) = insert[User](json) 

  def insertIntoTable(table: String) =
    //SecuredJsonAction(OnlyMe("olivier.nouguier@gmail.com")) {
    JsonAction {
      json =>
        withSession {
          table match {
            case "user" => 
              insert(json, _.validate[User], Users, (u:User) => Ok(Json.toJson(u)))
              
              
            case "event" =>
                   insert(json, _.validate[Event], Events, (u:Event) => Ok(Json.toJson(u)))
              
            case "talk" =>
              json.validate[Talk].map {
                (talk) =>
                  val v = Talks.insert(talk)
                  Ok(Json.toJson(v))
              }
          }
        }

    }

  def tables //= SecuredAction(OnlyMe("olivier.nouguier@gmail.com")) {
  = Action {
    implicit request =>
      withSession {

        Ok(Json.toJson(MetadataService.allTables))

      }

  }
}

case class OnlyMe(me: String) extends Authorization {
  def isAuthorized(user: Identity) = {
    user.email.getOrElse("") == me
  }

}




