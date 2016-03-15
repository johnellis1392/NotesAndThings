package com.celestia.notesandthings

import com.mongodb.casbah.Imports._
import org.bson.types.ObjectId
import spray.json.{JsString, JsValue, RootJsonFormat}

/**
  * Created by celestia on 3/14/16.
  */
package object util {

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
