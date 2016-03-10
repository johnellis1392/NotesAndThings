package com.celestia.notesandthings.actors

import akka.actor.Actor

import com.mongodb.{BasicDBObject, WriteResult}
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.{Imports, MongoDBList, MongoDBObject}
import com.mongodb.casbah.Imports._

import scala.util.Properties

import com.celestia.notesandthings.data._
import com.celestia.notesandthings.data.Note._


/**
  * Created by celestia on 3/5/16.
  */
class DBActor extends Actor {
  val interface = Properties.envOrElse("DB_PORT_27017_TCP_ADDR", "0.0.0.0")
  val port = Properties.envOrElse("DB_PORT_27017_TCP_PORT", "27017").toInt

  val conn:MongoClient = MongoClient(interface, port)
  val mongo:MongoDB = conn("notesandthings")
  val notes:MongoCollection = mongo("notes")


  override def receive = {
    case GetAll =>
      println(s"Received Get All")
      sender ! Success {
        notes.to[List] map { _.deserialize }
      }

    case Get(id) =>
      println(s"Received get for $id")
      implicit val db = conn
      sender ! {
        Note(_id = new ObjectId(id)).get
      }

    case Create(note: Note) =>

      println(s"Received create message: $note")
      notes.insert(note.serialize)
      println(s"Inserted note")
      sender ! Ok

    case Delete(id) =>
      sender ! Ok

    case Update(a) =>
      sender ! Ok

    case _ =>
      println(s"Received unmatched operation")
      sender ! InvalidOperation
  }
}

