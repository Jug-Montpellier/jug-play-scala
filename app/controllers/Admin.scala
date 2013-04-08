package controllers

import play.api.mvc._
import securesocial.core.{ Identity, Authorization }
import play.Logger

object Admin extends Controller with securesocial.core.SecureSocial {

  def index = SecuredAction(OnlyMe("olivier.nouguier@gmail.com")) {
    implicit request => controllers.Assets.at("/public/admin", "index.html")(request)

  }

}

case class OnlyMe(me: String) extends Authorization {
  def isAuthorized(user: Identity) = {
    user.email.getOrElse("") == me
  }
}



