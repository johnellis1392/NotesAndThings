package com.celestia.notesandthings.actors

import akka.actor.Actor
import com.celestia.notesandthings.{Create, Delete, Get, Update}
import com.mongodb.casbah.Imports._

import scala.util.Properties

/**
  * Created by celestia on 3/5/16.
  */
class DBActor extends Actor {
  val interface = Properties.envOrElse("DB_PORT_27017_TCP_ADDR", "0.0.0.0")
  val port = Properties.envOrElse("DB_PORT_27017_TCP_PORT", "27017").toInt

  val conn = MongoConnection(interface, port) // Mongo Connection
  val mongo = conn("notesandthings") // NotesAndThings Database
  val notes = mongo("notes") // Notes collection

  implicit class NoteMongoSerializer(note: Note) {
    def serialize =
      MongoDBObject(
        "title" -> note.title,
        "content" -> note.content
      )
  }

  override def receive = {
    case Create(note: Note) =>
      notes.insert(note.serialize)
      sender ! Ok
    case Delete(id) =>
      sender ! Ok
    case Get(id) =>
      sender ! Ok
    case Update(a) =>
      sender ! Ok
    case _ =>
      sender ! InvalidOperation
  }
}
