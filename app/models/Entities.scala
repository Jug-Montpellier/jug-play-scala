package models

import controllers.JSonFormats

import play.Play
import play.api.mvc.AnyContent
import play.api.libs.json._
import play.api.mvc.Request
import scala.slick.driver.BasicDriver.simple._
//import scala.slick.driver.PostgresDriver.simple._

import Database.threadLocalSession
import java.sql.Timestamp
import java.util.Calendar



trait Cruded[A] {
  def insert(a: A) : Long
}


object util {
  def now() = {
    new Timestamp(Calendar.getInstance().getTime().getTime())
  }

}

object Tests extends Tests {

}



object Users extends Users
object Answers extends Answers


case class Event(id: Option[Long], capacity: Int, date: Option[Timestamp], description: Option[String], location: Option[String], map: Option[String], open: Boolean, registrationurl: Option[String], report: Option[String], title: Option[String], partner_id: Option[Long]) {
  def attachments: Array[String] = {

    val eventFolder = Play.application().getFile(s"/public/event${id.get}")
    if (eventFolder != null) {
      val list = eventFolder.list()
      if (list != null)
        return list
    }
    Array()
  }
}

object Events extends Events {
  def last(n: Int) = Query(Events).sortBy(_.date.desc.nullsLast).take(n).list

  def pastAndUpComing = Query(Events).sortBy(_.date.desc.nullsLast).list.partition { e => e.date.get.before(util.now()) }

  override def all = Query(Events).sortBy(_.date).list

  def getById(id: Long) = {

    val q = for {
      e <- Events if e.id === id
      t <- Talks if t.event_id === e.id
      (t_t, tag) <- Talk_tags leftJoin Tags on (_.tags_id === _.id)
      s <- Speakers if s.id === t.speaker_id
    } yield (e, t, s, tag)

    vo(q)
  }

  def getOpened() = {

    val q = for {
      e <- Events if e.open
      t <- Talks if t.event_id === e.id
      (t_t, tag) <- Talk_tags leftJoin Tags on (_.tags_id === _.id)
      s <- Speakers if s.id === t.speaker_id
    } yield (e, t, s, tag)

    vo(q)
  }

  private def vo(q: Query[(models.Events.type, models.Talks.type, models.Speakers.type, models.Tags.type), (models.Event, models.Talk, models.Speaker, models.Tag)]): Option[EventViewObject] = {

    val list = q.list

    val event = list.groupBy(etst => etst._1).map {
      case (evt, etst) => (evt, etst.groupBy(etst2 => etst2._2))
    }

    val viewObject = event map {
      case (evt, etst) => EventViewObject(evt,
        etst.toList.map {
          case (talk, list) => TalkViewObject(talk,
            list.map(_._3).distinct,
            list.map(_._4).distinct)
        },
        evt.partner_id.flatMap(id => Eventpartners.getById(id)))
    }

    viewObject match {
      case h :: _ => Some(h)
      case _ => None
    }

  }

}


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


object Newss extends Newss


object Participations extends Participations

object Play_evolutionss extends Play_evolutionss


object Polls extends Polls

object Speakers extends Speakers {
  def isMember(email: String) = {
    val q = Query(Speakers).filter(s => s.email === email).filter(s => s.jugmember)
    Query(q.length).first > 0
  }

}


object Tags extends Tags

object Talks extends Talks

object Talk_tags extends Talk_tags

object Yearpartners extends Yearpartners {
  def runnings = {
    val q = Query(Yearpartners)
    val now = util.now
    q.filter(_.startdate < now).filter(_.stopdate > now).list
  }
}



case class User(id: Option[Long],email: Option[String])

class Users extends Table[User]("User") with Cruded[User]{
  def id = column[Long]("id", O.NotNull ,O.PrimaryKey, O.AutoInc)
  def email = column[String]("email")
  def * = id.? ~ email.? <> (User, User.unapply _)

  def autoInc = email.? returning id 
  def insert(o: User ) = autoInc.insert( o.email)
  def all() = Query(Users).list
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


class Events extends Table[Event]("event") with Cruded[Event]{
  def id = column[Long]("id", O.NotNull ,O.PrimaryKey, O.AutoInc)
  def capacity = column[Int]("capacity")
  def date = column[Timestamp]("date")
  def description = column[String]("description")
  def location = column[String]("location")
  def map = column[String]("map")
  def open = column[Boolean]("open")
  def registrationurl = column[String]("registrationurl")
  def report = column[String]("report")
  def title = column[String]("title")
  def partner_id = column[Long]("partner_id")
  def * = id.? ~ capacity ~ date.? ~ description.? ~ location.? ~ map.? ~ open ~ registrationurl.? ~ report.? ~ title.? ~ partner_id.? <> (Event, Event.unapply _)

  def autoInc = capacity ~ date.? ~ description.? ~ location.? ~ map.? ~ open ~ registrationurl.? ~ report.? ~ title.? ~ partner_id.? returning id 
  def insert(o: Event ) = autoInc.insert( o.capacity, o.date, o.description, o.location, o.map, o.open, o.registrationurl, o.report, o.title, o.partner_id)
  def all() = Query(Events).list
}
case class Eventpartner(id: Option[Long],description: Option[String],logourl: Option[String],name: Option[String],url: Option[String])

class Eventpartners extends Table[Eventpartner]("eventpartner"){
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
case class News(id: Option[Long],comments: Boolean,content: Option[String],date: Option[Timestamp],title: Option[String])

class Newss extends Table[News]("news"){
  def id = column[Long]("id", O.NotNull ,O.PrimaryKey, O.AutoInc)
  def comments = column[Boolean]("comments")
  def content = column[String]("content")
  def date = column[Timestamp]("date")
  def title = column[String]("title")
  def * = id.? ~ comments ~ content.? ~ date.? ~ title.? <> (News, News.unapply _)

  def autoInc = comments ~ content.? ~ date.? ~ title.? returning id 
  def insert(o: News ) = autoInc.insert( o.comments, o.content, o.date, o.title)
  def all() = Query(Newss).list
}
case class Participation(id: Option[Long],code: Option[String],status: Option[Int],event_id: Option[Long],user_id: Option[Long])

class Participations extends Table[Participation]("participation"){
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
case class Play_evolutions(id: Int,hash: String,applied_at: Timestamp,apply_script: Option[String],revert_script: Option[String],state: Option[String],last_problem: Option[String])

class Play_evolutionss extends Table[Play_evolutions]("play_evolutions"){
  def id = column[Int]("id", O.NotNull ,O.PrimaryKey, O.AutoInc)
  def hash = column[String]("hash")
  def applied_at = column[Timestamp]("applied_at")
  def apply_script = column[String]("apply_script")
  def revert_script = column[String]("revert_script")
  def state = column[String]("state")
  def last_problem = column[String]("last_problem")
  def * = id ~ hash ~ applied_at ~ apply_script.? ~ revert_script.? ~ state.? ~ last_problem.? <> (Play_evolutions, Play_evolutions.unapply _)

  def autoInc = hash ~ applied_at ~ apply_script.? ~ revert_script.? ~ state.? ~ last_problem.? returning id 
  def insert(o: Play_evolutions ) = autoInc.insert( o.hash, o.applied_at, o.apply_script, o.revert_script, o.state, o.last_problem)
  def all() = Query(Play_evolutionss).list
}
case class Poll(id: Option[Long],question: String,expirydate: Option[Timestamp],visible: Option[Boolean])

class Polls extends Table[Poll]("poll"){
  def id = column[Long]("id", O.NotNull ,O.PrimaryKey, O.AutoInc)
  def question = column[String]("question")
  def expirydate = column[Timestamp]("expirydate")
  def visible = column[Boolean]("visible")
  def * = id.? ~ question ~ expirydate.? ~ visible.? <> (Poll, Poll.unapply _)

  def autoInc = question ~ expirydate.? ~ visible.? returning id 
  def insert(o: Poll ) = autoInc.insert( o.question, o.expirydate, o.visible)
  def all() = Query(Polls).list
}
case class Speaker(id: Option[Long],activity: Option[String],compan: Option[String],description: Option[String],fullname: Option[String],jugmember: Option[Boolean],memberfct: Option[String],photourl: Option[String],url: Option[String],email: Option[String],personalurl: Option[String])

class Speakers extends Table[Speaker]("speaker"){
  def id = column[Long]("id", O.NotNull ,O.PrimaryKey, O.AutoInc)
  def activity = column[String]("activity")
  def compan = column[String]("compan")
  def description = column[String]("description")
  def fullname = column[String]("fullname")
  def jugmember = column[Boolean]("jugmember")
  def memberfct = column[String]("memberfct")
  def photourl = column[String]("photourl")
  def url = column[String]("url")
  def email = column[String]("email")
  def personalurl = column[String]("personalurl")
  def * = id.? ~ activity.? ~ compan.? ~ description.? ~ fullname.? ~ jugmember.? ~ memberfct.? ~ photourl.? ~ url.? ~ email.? ~ personalurl.? <> (Speaker, Speaker.unapply _)

  def autoInc = activity.? ~ compan.? ~ description.? ~ fullname.? ~ jugmember.? ~ memberfct.? ~ photourl.? ~ url.? ~ email.? ~ personalurl.? returning id 
  def insert(o: Speaker ) = autoInc.insert( o.activity, o.compan, o.description, o.fullname, o.jugmember, o.memberfct, o.photourl, o.url, o.email, o.personalurl)
  def all() = Query(Speakers).list
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
case class Talk(id: Option[Long],orderinevent: Int,teaser: Option[String],datetime: Option[String],title: Option[String],event_id: Option[Long],speaker_id: Option[Long])

class Talks extends Table[Talk]("talk"){
  def id = column[Long]("id", O.NotNull ,O.PrimaryKey, O.AutoInc)
  def orderinevent = column[Int]("orderinevent")
  def teaser = column[String]("teaser")
  def datetime = column[String]("datetime")
  def title = column[String]("title")
  def event_id = column[Long]("event_id")
  def speaker_id = column[Long]("speaker_id")
  def * = id.? ~ orderinevent ~ teaser.? ~ datetime.? ~ title.? ~ event_id.? ~ speaker_id.? <> (Talk, Talk.unapply _)

  def autoInc = orderinevent ~ teaser.? ~ datetime.? ~ title.? ~ event_id.? ~ speaker_id.? returning id 
  def insert(o: Talk ) = autoInc.insert( o.orderinevent, o.teaser, o.datetime, o.title, o.event_id, o.speaker_id)
  def all() = Query(Talks).list
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

