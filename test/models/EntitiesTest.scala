package models

import scala.slick.session.Database
import scala.slick.driver.BasicDriver.Implicit._
import Database.threadLocalSession

//import scala.slick.driver.PostgresDriver.simple._




object EntitiesTest extends App {
  Database.forURL("jdbc:postgresql:jug", "test", "test", driver = "org.postgresql.Driver") withSession {
   
//    val p = Events.getOpened
//    
//    println(p)
    
    val t1 = new Test(Some(5), "Olivier", Some("zozo"));
//    Tests.insert(t1);   

//    Tests.autoInc.insert("Olivier", Some("zozo"));
//    Tests.autoInc.insert("NoNick", None);

   val q = for { c <- Tests if c.id===5 } yield c

   val i = q.take(1);
   
   val z = i.list.head;
   val z2 = Test(z.id, z.name, None)
   println(z)
   
   Tests.where(_.id===z.id.get).update(z2)
   
   val email = "olivier.nouguier@gmail.com"
   println(email + ": " + Speakers.isMember(email))
   

    
    
  }

 
}