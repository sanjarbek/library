package controllers

import javax.inject._
import models._
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class BookController @Inject()(bookRepo: BookRepository,
                               userAction: UserAction,
                               cc: ControllerComponents
                              )(implicit ec: ExecutionContext)
  extends AbstractController(cc) with play.api.i18n.I18nSupport {

  val createBookForm: Form[CreateBookForm] = Form {
    mapping(
      "isn" -> nonEmptyText(minLength = 1, maxLength = 50),
      "title" -> nonEmptyText(minLength = 1, maxLength = 100),
      "author" -> nonEmptyText(minLength = 1, maxLength = 100)
    )(CreateBookForm.apply)(CreateBookForm.unapply)
  }

  val updateBookForm: Form[UpdateBookForm] = Form {
    mapping(
      "isn" -> nonEmptyText(minLength = 1, maxLength = 50),
      "title" -> nonEmptyText(minLength = 1, maxLength = 100),
      "author" -> nonEmptyText(minLength = 1, maxLength = 100)
    )(UpdateBookForm.apply)(UpdateBookForm.unapply)
  }

  def addBookPage = userAction.andThen(Permission.PermissionCheckAction).async { implicit request =>
    Future.successful(Ok(views.html.book.create(createBookForm)))
  }

  def addBook = userAction.andThen(Permission.PermissionCheckAction).async { implicit request =>
    createBookForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.book.create(errorForm)))
      },
      book => {
        val userId = request.userData.get.id
        bookRepo.create(userId, book.isn, book.title, book.author).map { _ =>
          Redirect(routes.BookController.getBooks()).flashing("success" -> "book.created")
        }
      }
    )
  }

  def updateBookPage(id: Long) = userAction.andThen(Permission.PermissionCheckAction).async { implicit request =>
    bookRepo.findById(id, request.userData.get.id).map{ maybeBook =>
      maybeBook match {
        case Some(book) => {
          val tmp = updateBookForm.fill(UpdateBookForm(book.isn, book.title, book.author))
          Ok(views.html.book.update(tmp, id))
        }
        case None => Redirect(routes.BookController.getBooks()).flashing("error" -> "book.not_found")
      }
    }
  }

  def updateBook(id: Long) = userAction.andThen(Permission.PermissionCheckAction).async { implicit request =>
    updateBookForm.bindFromRequest.fold(
      errorForm => {
        Logger.logger.warn("I am here")
        Logger.logger.warn(errorForm.toString)
//        Future.successful(Ok(views.html.book.update(errorForm, id)).flashing("error" -> "book.not_found"))
        Future.successful(Ok(views.html.book.update(errorForm, id)))
      },
      book => {
        bookRepo.update(id, book.isn, book.title, book.author, request.userData.get.id).map { _ =>
          Redirect(routes.BookController.getBooks()).flashing("success" -> "book.updated")
        }
      }
    )
  }

  def deleteBook(id: Long) = userAction.andThen(Permission.PermissionCheckAction).async { implicit request =>
    bookRepo.delete(id, request.userData.get.id).map { result =>
      if (result==0) Redirect(routes.BookController.getBooks()).flashing("error"-> "book.not_found")
      else Redirect(routes.BookController.getBooks()).flashing("success" -> "book.deleted")
    }
  }

  def getBooks = userAction.andThen(Permission.PermissionCheckAction).async { implicit request =>
    bookRepo.findByUserId(request.userData.get.id).map { books =>
      Ok(views.html.book.index(books))
    }
  }
}

case class CreateBookForm(isn: String, title: String, author: String)
case class UpdateBookForm(isn: String, title: String, author: String)
