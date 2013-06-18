package models
        
import scala.slick.driver.BasicDriver.simple._
import Database.threadLocalSession

import play.Play

import java.sql.Timestamp
import java.util.Calendar

case class Event(id: Option[Long],capacity: Int,date: Option[Timestamp],description: Option[String],location: Option[String],map: Option[String],open: Boolean,registrationurl: Option[String],report: Option[String],title: Option[String],partner_id: Option[Long]) {
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

//GENERATED - Event
//case class Event(id: Option[Long],capacity: Int,date: Option[Timestamp],description: Option[String],location: Option[String],map: Option[String],open: Boolean,registrationurl: Option[String],report: Option[String],title: Option[String],partner_id: Option[Long])

class Events extends Table[Event]("event") with Cruded[Event] {
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

    def fk403827aa33e91e4 = foreignKey("fk403827aa33e91e4", partner_id, Eventpartners)(_.id)

    def * = id.? ~ capacity ~ date.? ~ description.? ~ location.? ~ map.? ~ open ~ registrationurl.? ~ report.? ~ title.? ~ partner_id.? <> (Event, Event.unapply _)
    def autoInc = capacity ~ date.? ~ description.? ~ location.? ~ map.? ~ open ~ registrationurl.? ~ report.? ~ title.? ~ partner_id.? returning id
    def insert(o: Event) = autoInc.insert( o.capacity, o.date, o.description, o.location, o.map, o.open, o.registrationurl, o.report, o.title, o.partner_id)
    def all() = Query(Events).list
}

// END - Event
