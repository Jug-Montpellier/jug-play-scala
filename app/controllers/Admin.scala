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

object Admin extends Controller
  with securesocial.core.SecureSocial
  with DBSession
  with JsonAction {

  implicit val memberFormat = Json.format[Member]
  implicit val entityFormat = Json.format[Entity]

  def index = SecuredAction(OnlyMe("olivier.nouguier@gmail.com")) {
    implicit request => controllers.Assets.at("/public/admin", "index.html")(request)

  }

  def testsInsert = JsonAction { json =>
    json.validate[Test].map {
      (test) =>
        withSession {
          Tests.autoInc.insert(test.name, test.nickname)
          Ok(Json.toJson(test))
        }
    }
  }

  def tables = SecuredAction(OnlyMe("olivier.nouguier@gmail.com")) {
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




