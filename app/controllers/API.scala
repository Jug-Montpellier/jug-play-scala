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

object API extends Controller with DBSession with JSonFormats {

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

  //  def withJSon[A](req: Request[AnyContent])(f: JsValue => A): Option[A] = {
  //    req.body.asJson.map {json =>
  //      f(json)
  //    }.getOrElse {
  //        BadRequest("Expecting Json data")
  //      }
  //    
  //
  //  }

  def withJSon(f: JsValue => JsResult[SimpleResult[String]])(implicit req: Request[AnyContent]): SimpleResult[String] = {
    req.body.asJson.map { json =>
      f(json).recoverTotal {
        e =>
          {
            println(e)
            BadRequest("Dettected error: " + JsError.toFlatJson(e))
          }
      }

    }.getOrElse {
      BadRequest("Expecting Json data")
    }

  }

  def testsInsert = Action {
    implicit req =>
      withJSon { json =>
        json.validate[Test].map {

          case test: Test => {
            withSession {
              Tests.autoInc.insert(test.name, test.nickname)
            }
            Ok(test.name)
          }
        }
      }
  }

  def testsInsertRaw = Action {
    req =>
      req.body.asJson.map { json =>
        json.validate[Test].map {

          case test: Test => {
            withSession {
              Tests.autoInc.insert(test.name, test.nickname)
            }
            Ok(test.name)
          }
        }.recoverTotal {
          e =>
            {
              println(e)
              BadRequest("Dettected error: " + JsError.toFlatJson(e))
            }
        }
      }.getOrElse {
        BadRequest("Expecting Json data")
      }

  }
}
