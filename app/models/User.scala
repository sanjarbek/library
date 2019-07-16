package models

case class User(id: Long, username: String, password: String)

case class UserData(id: Long, username: String)