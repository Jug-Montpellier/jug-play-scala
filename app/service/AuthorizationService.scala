package service

import securesocial.core.Authorization
import securesocial.core.Identity
import controllers.DBSession
import models.Speakers

object AuthorizationService {

  case object HasAdminRole extends Authorization with DBSession {
    def isAuthorized(user: Identity) = {
      withSession {
        user.email.map { email => Speakers.isMember(email) }.getOrElse(false) 
      }
    }
  }

}