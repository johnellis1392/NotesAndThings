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

import com.celestia.notesandthings.data.Note._
import com.celestia.notesandthings.data.Note.NoteJsonFormat._

/**
  * Created by celestia on 3/14/16.
  */
case class User(_id: ObjectId, username: String, notes: List[Note])


object User extends DefaultJsonProtocol
    with SprayJsonSupport {
  implicit class UserService(user:User) {

  }



  implicit class UserMongoSerializer(user:User) {
    def serialize = MongoDBObject(
      "_id" -> user._id,
      "username" -> user.username,
      "notes" -> user.notes.map { _.serialize }
    )
  }

  implicit class UserMongoDeserializer(obj: DBObject) {
    def deserialize = User(
      obj.getAs[ObjectId]("_id").getOrElse(new ObjectId),
      obj.getAs[String]("username").getOrElse(""),
      obj.getAs[List[Note]]("notes").getOrElse(Nil)
    )
  }


  implicit object UserJsonFormat extends RootJsonFormat[User] {
    override def read(jsValue:JsValue) = User(
      fromField[Option[String]](jsValue, "_id").map { new ObjectId(_) } getOrElse new ObjectId,
      fromField[String](jsValue, "username"),
      fromField[List[Note]](jsValue, "notes")
    )

    override def write(user:User) = JsObject(
      "_id" -> JsString(user._id.toString),
      "username" -> JsString(user.username),
      "notes" -> JsArray { user.notes.to[Vector].map { NoteJsonFormat.write(_) } }
    )
  }
}

