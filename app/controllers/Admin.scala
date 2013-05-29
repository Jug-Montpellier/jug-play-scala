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

 // import play.crude.CrudeMacro._

  def insert[A](v: JsValue => JsResult[A], cruded: Cruded[A])(implicit json: JsValue): JsResult[SimpleResult[JsValue]] = v(json).map(a=>cruded.insert(a)).map(r=>Ok(Json.toJson(r)))
    

  def all(table: String): Action[AnyContent] =
   //SecuredAction(AuthorizationService.HasAdminRole) {
    Action {
      implicit request =>
        withSession {
          table match {
            case "users" => Ok(Json.toJson(Users.all))
            case "events" => Ok(Json.toJson(Events.all))
            case "talks" => Ok(Json.toJson(Talks.all))
            case "speakers" => Ok(Json.toJson(Speakers.all))
            case "news" => Ok(Json.toJson(Newss.all))
            case "tests" => Ok(Json.toJson(Tests.all()))
            case _ => NotFound
          }
        }

    }
  
  
  def insertIntoTable(table: String) =
   SecuredJsonAction(AuthorizationService.HasAdminRole) {
   // JsonAction {
      implicit json =>
        withSession {
          table match {
            case "user" => insert(_.validate[User], Users)
            case "event" => insert(_.validate[Event], Events)
            case "talk" => insert(_.validate[Talk], Talks)
            case "speaker" => insert(_.validate[Speaker], Speakers)
            case "news" => insert(_.validate[News], Newss)
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




