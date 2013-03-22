package controllers

import play.api.mvc.Controller
import play.api.mvc.WebSocket
import play.api.libs.json.JsValue
import akka.util.Timeout
import scala.concurrent.duration._
import play.api.libs.concurrent.Akka
import akka.actor.Actor
import play.api.Play.current
import akka.actor.Props
import scala.concurrent.Future
import play.api.libs.iteratee.Iteratee
import play.api.libs.iteratee.Enumerator
import akka.actor._
import scala.concurrent.duration._
import play.api._
import play.api.libs.json._
import play.api.libs.iteratee._
import play.api.libs.concurrent._
import akka.util.Timeout
import akka.pattern.ask
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import java.util.Date

object WebSockets extends Controller {

  def connect(username: String) = WebSocket.async[JsValue] {
    request => ConnectionActor.connect(username)
  }

}

object ConnectionActor {

  Duration

  implicit val timeout = Timeout(1 second)

  
  
  lazy val default = {
    val connectionActor = Akka.system.actorOf(Props[ConnectionActor])
    
    val cancellable =
    Akka.system.scheduler.schedule(0 milliseconds,
    5000 milliseconds,
    connectionActor,
    Tick)
    
    connectionActor
  }

  def connect(username: String): Future[(Iteratee[JsValue, _], Enumerator[JsValue])] = {
    (default ? Join(username)).map {
      case Connected(enumerator) =>
        val iteratee = Iteratee.foreach[JsValue] { event =>
          default ! Message(username, (event \ "text").as[String])
        }.mapDone { _ =>
          default ! Quit(username)
        }
        (iteratee, enumerator)

      case CannotConnect(error) =>

        // Connection error

        // A finished Iteratee sending EOF
        val iteratee = Done[JsValue, Unit]((), Input.EOF)

        // Send an error and close the socket
        val enumerator = Enumerator[JsValue](JsObject(Seq("error" -> JsString(error)))).andThen(Enumerator.enumInput(Input.EOF))

        (iteratee, enumerator)
    }
  }

}

class ConnectionActor extends Actor {
  var members = Set.empty[String]
  val (chatEnumerator, chatChannel) = Concurrent.broadcast[JsValue]

  def receive = {
    case Join(username) => {
      
       members = members + username
        sender ! Connected(chatEnumerator)
        self ! NotifyJoin(username)
    }

    case NotifyJoin(username) => {
      notifyAll("join", username, "has entered the room")
    }

    case Message(username, text) => {
      notifyAll("msg", username, text)
    }

    case Quit(username) => {
      members = members - username
      notifyAll("quit", username, "has left the room")
    }
    
    case Tick => notifyAll("msg", "tick", "It is " + new Date)

  }

  def notifyAll(kind: String, user: String, text: String) {
    val msg = JsObject(
      Seq(
        "kind" -> JsString(kind),
        "user" -> JsString(user),
        "message" -> JsString(text),
        "members" -> JsArray(
          members.toList.map(JsString))))
    chatChannel.push(msg)
  }

}

case class Join(username: String)
case class Quit(username: String)
case class Message(username: String, text: String)
case class NotifyJoin(username: String)

case class Connected(enumerator: Enumerator[JsValue])
case class CannotConnect(msg: String)
case object Tick

