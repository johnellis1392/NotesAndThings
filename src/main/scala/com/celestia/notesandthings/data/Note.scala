package com.celestia.notesandthings.data

import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.Imports.ObjectId
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.ValidBSONType.{BasicDBObject, ObjectId}
import com.mongodb.casbah.commons.{MongoDBList, Imports, MongoDBObject}
import com.mongodb.casbah.query.dsl.BSONType.BSONObjectId
import spray.http.{HttpEntity, MediaTypes}
import spray.httpx.SprayJsonSupport
import spray.httpx.SprayJsonSupport._
import spray.httpx.marshalling.{MarshallingContext, Marshaller}
import spray.httpx.unmarshalling.Unmarshaller
import spray.json._
import MediaTypes._


/**
  * Created by celestia on 3/5/16.
  */
case class Note(
   _id: ObjectId=new ObjectId,
   title: String="",
   content: String="",
   children: List[Note]=List()
)


/**
  * Implicit Json serialization
  */
object Note extends DefaultJsonProtocol
      with SprayJsonSupport {


  implicit object NoteJsonFormat extends RootJsonFormat[Note] {
    override def read(jsValue:JsValue):Note = Note(
      fromField[Option[String]](jsValue, "_id").map { new ObjectId(_) } getOrElse new ObjectId,
      fromField[String](jsValue, "title"),
      fromField[String](jsValue, "content"),
      fromField[List[JsValue]](jsValue, "children").map { read(_) }
    )

    override def write(note:Note):JsValue = JsObject(
      "_id" -> JsString(note._id.toString),
      "title" -> JsString(note.title),
      "content" -> JsString(note.content),
      "children" -> JsArray { note.children.to[Vector].map { write(_) } }
    )
  }


  /**
    * Translate a note to a mongo object
    *
    * @param note
    */
  implicit class NoteMongoSerializer(note: Note) {
    def serialize:Imports.DBObject =
      MongoDBObject(
        "_id" -> note._id,
        "title" -> note.title,
        "content" -> note.content,
        "children" -> MongoDBList(note.children.map { _.serialize })
      )
  }


  implicit class NoteMongoDeserializer(obj: DBObject) {
    def deserialize:Note =
      Note(
        obj.getAs[ObjectId]("_id").getOrElse(new ObjectId),
        obj.getAs[String]("title").getOrElse(""),
        obj.getAs[String]("content").getOrElse(""),
        obj.getAs[List[DBObject]]("children").getOrElse(Nil).map { _.deserialize }
      )
  }


  /**
    * Service for fetching and serializing data to Mongo
    *
    * @param note
    */
  implicit class NoteDBService(note: Note) {
    def list(implicit db:MongoClient):List[Note] = {
      val notes = db("notesandthings")("notes")
      notes.to[List] map { _.deserialize }
    }

    def get(implicit db:MongoClient):Option[Note] = {
      val notes = db("notesandthings")("notes")
      notes.findOne(MongoDBObject("_id" -> note._id))
        .map { _.deserialize }
    }

    def create(implicit db:MongoClient):Note = {
      val notes = db("notesandthings")("notes")
      notes.insert(note.serialize)
      note
    }

    def update(implicit db:MongoClient):Unit = {
      val notes = db("notesandthings")("notes")
      notes.update(MongoDBObject("_id" -> note._id), note.serialize)
    }

    def delete(implicit db:MongoClient):Option[Note] = {
      val notes = db("notesandthings")("notes")
      notes.findAndRemove(MongoDBObject("_id" -> note._id))
        .map { _.deserialize }
    }
  }


  /**
    * Serialize and deserialize ObjectId to javascript values
    */
  implicit object ObjectIdJsonSerializer extends RootJsonFormat[ObjectId] {
    override def write(o:ObjectId):JsValue = JsString(o.toString)
    override def read(s:JsValue):ObjectId = s match {
      case JsString(s) => new ObjectId(s)
      case _ => throw new Exception("Tried to serialize non-string object to ObjectId: " + s.toString)
    }
  }
}

