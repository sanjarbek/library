package controllers

import com.google.inject.Inject
import models.UserData
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class UserRequest[A](val userData: Option[UserData], request: Request[A]) extends WrappedRequest[A](request)

class UserAction @Inject()(val parser: BodyParsers.Default)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[UserRequest, AnyContent]
    with ActionTransformer[Request, UserRequest] {
  def transform[A](request: Request[A]) = Future.successful {
    val maybeUserData = (for {
      userId <- request.session.get("userId")
      username <- request.session.get("username")
    } yield (userId, username)) match {
      case Some((userId, username)) => Some(UserData(userId.toLong, username))
      case None => None
    }
    new UserRequest(maybeUserData, request)
  }
}

object Permission {
  def PermissionCheckAction(implicit ec: ExecutionContext) = new ActionFilter[UserRequest] {
    def executionContext = ec

    def filter[A](input: UserRequest[A]) = Future.successful {
      input.userData match {
        case Some(userData) => None
        case None => Some(Results.Redirect(routes.AppController.loginPage()))
      }
    }
  }
}
