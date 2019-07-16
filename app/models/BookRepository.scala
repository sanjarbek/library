package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for books.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class BookRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class BookTable(tag: Tag) extends Table[Book](tag, "books") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def userId = column[Long]("user_id")

    def isn = column[String]("isn")

    def title = column[String]("title")

    def author = column[String]("author")

    def * = (id, userId, isn, title, author) <> ((Book.apply _).tupled, Book.unapply)
  }

  private val books = TableQuery[BookTable]

  def create(userId: Long, isn: String, title: String, author: String): Future[Book] = db.run {
    (books.map(p => (p.userId, p.isn, p.title, p.author))
      returning books.map(_.id)
      into ((tuple, id) => Book(id, tuple._1, tuple._2, tuple._3, tuple._4))
    ) += (userId, isn, title, author)
  }

  def update(id: Long, isn: String, title: String, author: String, userId: Long): Future[Int] = {
    val query = books.filter(item => item.id===id)
      .map(item => (item.isn, item.title, item.author))
      .update((isn, title, author))
    db.run(query)
  }

  def delete(id: Long, userId: Long): Future[Int] = db.run {
    books.filter(item => item.id===id && item.userId===userId).delete
  }

  def all(): Future[Seq[Book]] = db.run {
    books.result
  }

  def findById(id: Long, userId: Long): Future[Option[Book]] = db.run {
    books.filter(_.id===id).result.headOption
  }

  def findByUserId(userId: Long): Future[Seq[Book]] = db.run {
    books.filter(_.userId===userId).result
  }
}
