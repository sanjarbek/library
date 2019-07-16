package controllers

import play.api.{Logger, Logging}
import javax.inject._
import models.UserRepository
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

class AppController @Inject()(userRepo: UserRepository,
                              userAction: UserAction,
                              cc: MessagesControllerComponents
                             )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  val loginForm: Form[LoginForm] = Form {
    mapping(
      "username" -> nonEmptyText(maxLength = 50),
      "password" -> nonEmptyText(maxLength = 100)
    )(LoginForm.apply)(LoginForm.unapply)
  }

  def loginPage = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }

  def logout = userAction.andThen(Permission.PermissionCheckAction).async { implicit request =>
    Future.successful(Redirect(routes.AppController.loginPage()).withNewSession)
  }

  def login = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      errorForm => {
        Logger.logger.debug(errorForm.toString)
        Future.successful(Ok(views.html.login(errorForm)))
      },
      loginFormData => {
        userRepo.findByUsernamePassword(loginFormData.username, loginFormData.password).map { maybeUser =>
          maybeUser match {
            case Some(user) => {
              Redirect(routes.BookController.getBooks())
                .withSession(request.session + ("userId"->user.id.toString) + ("username" -> user.username) )
            }
            case None => Ok(views.html.login(loginForm))
          }
        }
      }
    )
  }

}

case class LoginForm(username: String, password: String)
