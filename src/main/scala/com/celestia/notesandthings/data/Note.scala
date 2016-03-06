package com.celestia.notesandthings.data

import spray.http.{HttpEntity, MediaTypes}
import spray.httpx.SprayJsonSupport
import spray.httpx.SprayJsonSupport._
import spray.httpx.marshalling.{MarshallingContext, Marshaller}
import spray.json.{JsonFormat, RootJsonFormat, DefaultJsonProtocol}
import MediaTypes._


/**
  * Created by celestia on 3/5/16.
  */
case class Note(
  title: String="",
  content: String="",
  children: List[Note]=List()
)

/**
  * Implicit Json serialization
  */
object Note
    extends DefaultJsonProtocol
      with SprayJsonSupport {
  implicit val NoteFormat: JsonFormat[Note] = lazyFormat(jsonFormat3(Note.apply))

  implicit val NoteMarshaller =
    Marshaller.of[Note](`application/json`) { (value, contentType, context) =>
      val json = NoteFormat.write(value).toString
      context.marshalTo(HttpEntity(contentType, json))
    }
}

