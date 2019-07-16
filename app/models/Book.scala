package models

import play.api.libs.json._

case class Book(id: Long, userId: Long, isn: String, title: String, author: String)

