package models

import controllers.JSonFormats

import play.Play
import play.api.mvc.AnyContent
import play.api.libs.json._
import play.api.mvc.Request
import scala.slick.driver.BasicDriver.simple._


import Database.threadLocalSession
import java.sql.Timestamp
import java.util.Calendar



trait Cruded[A] {
  def insert(a: A) : Long
  def all : List[A]
}


object util {
  def now() = {
    new Timestamp(Calendar.getInstance().getTime().getTime())
  }

}

object Tests extends Tests {

}



object Answers extends Answers







object Eventpartners extends Eventpartners {
  
	def getById(id: Long) = {
		val q = for {
			p <- Eventpartners if p.id === id
		} yield (p)
		q.list match {
		case p :: _ => Some(p)
		case _ => None
		}
	}
}




object Participations extends Participations


object Polls extends Polls



object Tags extends Tags


object Talk_tags extends Talk_tags

object Yearpartners extends Yearpartners {
  def runnings = {
    val q = Query(Yearpartners)
    val now = util.now
    q.filter(_.startdate < now).filter(_.stopdate > now).list
  }
}



case class Answer(id: Option[Long],answer: String,votes: Option[Long],poll_id: Long)



class Answers extends Table[Answer]("answer"){
  def id = column[Long]("id", O.NotNull ,O.PrimaryKey, O.AutoInc)
  def answer = column[String]("answer")
  def votes = column[Long]("votes")
  def poll_id = column[Long]("poll_id")
  def * = id.? ~ answer ~ votes.? ~ poll_id <> (Answer, Answer.unapply _)

  def autoInc = answer ~ votes.? ~ poll_id returning id 
  def insert(o: Answer ) = autoInc.insert( o.answer, o.votes, o.poll_id)
  def all() = Query(Answers).list
}



case class Eventpartner(id: Option[Long],description: Option[String],logourl: Option[String],name: Option[String],url: Option[String])

class Eventpartners extends Table[Eventpartner]("eventpartner") with Cruded[Eventpartner]{
  def id = column[Long]("id", O.NotNull ,O.PrimaryKey, O.AutoInc)
  def description = column[String]("description")
  def logourl = column[String]("logourl")
  def name = column[String]("name")
  def url = column[String]("url")
  def * = id.? ~ description.? ~ logourl.? ~ name.? ~ url.? <> (Eventpartner, Eventpartner.unapply _)

  def autoInc = description.? ~ logourl.? ~ name.? ~ url.? returning id 
  def insert(o: Eventpartner ) = autoInc.insert( o.description, o.logourl, o.name, o.url)
  def all() = Query(Eventpartners).list
}

case class Participation(id: Option[Long],code: Option[String],status: Option[Int],event_id: Option[Long],user_id: Option[Long])

class Participations extends Table[Participation]("participation") with Cruded[Participation]{
  def id = column[Long]("id", O.NotNull ,O.PrimaryKey, O.AutoInc)
  def code = column[String]("code")
  def status = column[Int]("status")
  def event_id = column[Long]("event_id")
  def user_id = column[Long]("user_id")
  def * = id.? ~ code.? ~ status.? ~ event_id.? ~ user_id.? <> (Participation, Participation.unapply _)

  def autoInc = code.? ~ status.? ~ event_id.? ~ user_id.? returning id 
  def insert(o: Participation ) = autoInc.insert( o.code, o.status, o.event_id, o.user_id)
  def all() = Query(Participations).list
}

case class Poll(id: Option[Long],question: String,expirydate: Option[Timestamp],visible: Option[Boolean])

class Polls extends Table[Poll]("poll") with Cruded[Poll]{
  def id = column[Long]("id", O.NotNull ,O.PrimaryKey, O.AutoInc)
  def question = column[String]("question")
  def expirydate = column[Timestamp]("expirydate")
  def visible = column[Boolean]("visible")
  def * = id.? ~ question ~ expirydate.? ~ visible.? <> (Poll, Poll.unapply _)

  def autoInc = question ~ expirydate.? ~ visible.? returning id 
  def insert(o: Poll ) = autoInc.insert( o.question, o.expirydate, o.visible)
  def all() = Query(Polls).list
}
case class Tag(id: Option[Long],name: Option[String])

class Tags extends Table[Tag]("tag"){
  def id = column[Long]("id", O.NotNull ,O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def * = id.? ~ name.? <> (Tag, Tag.unapply _)

  def autoInc = name.? returning id 
  def insert(o: Tag ) = autoInc.insert( o.name)
  def all() = Query(Tags).list
}

case class Talk_tag(talk_id: Long,tags_id: Long)

class Talk_tags extends Table[Talk_tag]("talk_tag"){
  def talk_id = column[Long]("talk_id", O.NotNull ,O.PrimaryKey, O.AutoInc)
  def tags_id = column[Long]("tags_id", O.NotNull ,O.PrimaryKey, O.AutoInc)
  def * = talk_id ~ tags_id <> (Talk_tag, Talk_tag.unapply _)



  def all() = Query(Talk_tags).list
}
case class Test(id: Option[Long],name: String,nickname: Option[String])

class Tests extends Table[Test]("test"){
  def id = column[Long]("id", O.NotNull ,O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def nickname = column[String]("nickname")
  def * = id.? ~ name ~ nickname.? <> (Test, Test.unapply _)

  def autoInc = name ~ nickname.? returning id 
  def insert(o: Test ) = autoInc.insert( o.name, o.nickname)
  def all() = Query(Tests).list
}
case class Yearpartner(id: Option[Long],description: Option[String],logourl: Option[String],name: Option[String],startdate: Option[Timestamp],stopdate: Option[Timestamp],url: Option[String])

class Yearpartners extends Table[Yearpartner]("yearpartner"){
  def id = column[Long]("id", O.NotNull ,O.PrimaryKey, O.AutoInc)
  def description = column[String]("description")
  def logourl = column[String]("logourl")
  def name = column[String]("name")
  def startdate = column[Timestamp]("startdate")
  def stopdate = column[Timestamp]("stopdate")
  def url = column[String]("url")
  def * = id.? ~ description.? ~ logourl.? ~ name.? ~ startdate.? ~ stopdate.? ~ url.? <> (Yearpartner, Yearpartner.unapply _)

  def autoInc = description.? ~ logourl.? ~ name.? ~ startdate.? ~ stopdate.? ~ url.? returning id 
  def insert(o: Yearpartner ) = autoInc.insert( o.description, o.logourl, o.name, o.startdate, o.stopdate, o.url)
  def all() = Query(Yearpartners).list
}


// def speaker = foreignKey("speaker_id", speaker_id, Speakers)(_.id)


//  def all = {
//    val q = for {
//      c <- Speakers
//    } yield (c)
//
//    q.list
//  }

