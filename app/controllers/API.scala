package controllers

import scala.slick.driver.BasicDriver.simple.Database.threadLocalSession
import models.Events
import models.Speakers
import models.Test
import models.Tests
import models.Yearpartners
import play.api.libs.json.JsError
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.libs.json.JsValue
import play.api.mvc.SimpleResult
import play.api.libs.json.JsResult
import java.sql.SQLException
import play.api.libs.json.JsString


object API extends Controller
  with DBSession
  with JsonAction {

  def speakers() = Action {
    withSession {
      Ok(Json.toJson(Speakers.all()))
    }
  }

  def events() = Action {
    withSession {
      Ok(Json.toJson(Events.all))
    }
  }

  def yearPartners = Action {
    withSession {
      Ok(Json.toJson(Yearpartners.all))
    }
  }

  def tests = Action {
    withSession {
      Ok(Json.toJson(Tests.all))
    }
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

}
