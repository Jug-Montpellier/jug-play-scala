package models
        
import scala.slick.driver.BasicDriver.simple._
import Database.threadLocalSession

case class Talk(id: Option[Long],orderinevent: Int,teaser: Option[String],datetime: Option[String],title: Option[String],event_id: Option[Long],speaker_id: Option[Long])

object Talks extends Talks

//GENERATED - Talk
//case class Talk(id: Option[Long],orderinevent: Int,teaser: Option[String],datetime: Option[String],title: Option[String],event_id: Option[Long],speaker_id: Option[Long])

class Talks extends Table[Talk]("talk") with Cruded[Talk] {
    def id = column[Long]("id", O.NotNull ,O.PrimaryKey, O.AutoInc)
    def orderinevent = column[Int]("orderinevent")
    def teaser = column[String]("teaser")
    def datetime = column[String]("datetime")
    def title = column[String]("title")
    def event_id = column[Long]("event_id")
    def speaker_id = column[Long]("speaker_id")

    def event = foreignKey("fk27a8ccbafea2d6", event_id, Events)(_.id)
    def speaker = foreignKey("fk27a8ccf3eb05b6", speaker_id, Speakers)(_.id)

    def * = id.? ~ orderinevent ~ teaser.? ~ datetime.? ~ title.? ~ event_id.? ~ speaker_id.? <> (Talk, Talk.unapply _)
    def autoInc = orderinevent ~ teaser.? ~ datetime.? ~ title.? ~ event_id.? ~ speaker_id.? returning id
    def insert(o: Talk) = autoInc.insert( o.orderinevent, o.teaser, o.datetime, o.title, o.event_id, o.speaker_id)
    def all() = Query(Talks).list
}

// END - Talk
