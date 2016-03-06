package com.celestia.notesandthings.actors

import akka.actor.Actor
import com.celestia.notesandthings.data._
import com.mongodb.WriteResult
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.{Imports, MongoDBList, MongoDBObject}

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


  /**
    * Translate a note to a mongo object
    *
    * @param note
    */
  implicit class NoteMongoSerializer(note: Note) {
    def serialize:Imports.DBObject =
      MongoDBObject(
        "title" -> note.title,
        "content" -> note.content,
        "children" -> MongoDBList(note.children.map { _.serialize })
      )
  }

  implicit class NoteMongoDeserializer(obj: MongoDBObject) {
    def deserialize:Note =
      Note(
        obj getAs[String] "title" getOrElse "",
        obj getAs[String] "content" getOrElse "",
        obj.to[List] map { _.asInstanceOf[MongoDBObject] deserialize }
      )
  }

  override def receive = {
    case GetAll =>
      sender ! Success {
        notes.to[List] map { _.asInstanceOf[MongoDBObject] deserialize }
      }

    case Get(id) =>
      sender ! Success {
        notes.findOneByID(id) map {
          _.asInstanceOf[MongoDBObject] deserialize
        } getOrElse Note()
      }

    case Create(note: Note) =>
      notes.insert(note serialize)
      sender ! Ok

    case Delete(id) =>
      sender ! Ok

    case Update(a) =>
      sender ! Ok

    case _ =>
      sender ! InvalidOperation
  }
}

