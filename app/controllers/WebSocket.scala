package controllers

import java.util.Date

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.concurrent.duration.DurationInt

import akka.actor.Actor
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.pattern.ask
import akka.util.Timeout
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.iteratee.Concurrent
import play.api.libs.iteratee.Done
import play.api.libs.iteratee.Enumerator
import play.api.libs.iteratee.Input
import play.api.libs.iteratee.Iteratee
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import play.api.mvc.Controller
import play.api.mvc.WebSocket

object WebSockets extends Controller {

  def connect(username: String) = WebSocket.async[JsValue] {
    request => ConnectionActor.connect(username)
  }

}

object ConnectionActor {

  implicit val timeout = Timeout(1 second)

  //  def test = {
  //    default ? Join("zozo")
  //  }

  def connect(username: String): Future[(Iteratee[JsValue, _], Enumerator[JsValue])] = {
    lazy val default = {
      val connectionActor = Akka.system.actorOf(Props[ConnectionActor])

      println("Build lazy actor")

      val cancellable =
        Akka.system.scheduler.schedule(0 milliseconds,
          1000 milliseconds,
          connectionActor,
          Tick)

      connectionActor
    }
    (default ? Join(username)).map {
      case Connected(enumerator) => {
        println("connected")
        val iteratee = Iteratee.foreach[JsValue] { event =>
          println("received " + event \ "text")
          default ! Message(username, (event \ "text").as[String])
        }.mapDone { _ =>
          println("Done")
          default ! Quit(username)
        }
        (iteratee, enumerator)

      }
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
      //        self ! NotifyJoin(username)
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
      Akka.system.stop(self)
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

  override def postStop() = {
    println("Bye")
    super.postStop();
  }

}

case class Join(username: String)
case class Quit(username: String)
case class Message(username: String, text: String)
case class NotifyJoin(username: String)

case class Connected(enumerator: Enumerator[JsValue])
case class CannotConnect(msg: String)
case object Tick

