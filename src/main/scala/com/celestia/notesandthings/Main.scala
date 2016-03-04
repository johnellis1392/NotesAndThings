package com.celestia.notesandthings

import scala.concurrent.duration._

import akka.io.IO
import akka.pattern.ask
import akka.actor.{Props, ActorSystem, Actor}
import akka.util.Timeout
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.{Flow, Sink, Source}

import spray.json.DefaultJsonProtocol
import spray.routing._
import spray.can.Http
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

//import com.mongodb.casbah._
import com.mongodb.casbah.Imports._
//import com.mongodb.casbah.query.Imports._



sealed trait Response
case class Ok[A](a: A) extends Response
sealed trait ErrorResponse extends Response
case object InvalidOperation extends ErrorResponse
case object InvalidParameter extends ErrorResponse
case object Exception extends ErrorResponse

sealed trait DBCommands
case class Create[A](a: A) extends DBCommands
case class Delete(id: Int) extends DBCommands
case class Get(id: Int) extends DBCommands
case class Update[A](a: A) extends DBCommands

case class Note(title: String, content: String)


class DBActor extends Actor {
  val conn = MongoConnection("localhost", 27017) // Mongo Connection
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

class ServerActor extends Actor
    with Directives
    with DefaultJsonProtocol
    with HttpService {
  implicit val system = ActorSystem()
  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(5 seconds)
  implicit val materializer = ActorMaterializer()
  override def actorRefFactory = context
  private lazy val db = system.actorOf(Props[DBActor])

  def routes = {
    logRequestResponse("akka-routes") {
      path("notes" / Segment) { id =>
        get {
          complete {
            (db ? Get(id.toInt))
                .map {
                  case n @ Note(_, _) =>
//                    Ok(n)
                    s"Ok: $n"
                  case _ =>
//                    Exception
                  s"Something Went Wrong"
                }
          }
        }
      } ~
      path("something" / Segment) { id =>
        get {
          complete {
            s"Got message from id: $id"
          }
        }
      } ~
      path("something") {
        get {
          complete {
            s"Got Something!"
          }
        }
      } ~
      get {
        complete {
          s"Got a path on the base route!"
        }
      } ~
      post {
        complete {
          (db ? Create(Note("Something", "This is a note!")))
              .map {
                case Ok =>
                  s"Success!"
                case _ =>
                  s"Something went wrong."
              }
        }
      }
    }
  }

  override def receive = runRoute(routes)
}


/**
  * Created by celestia on 3/1/16.
  */
object Main extends App {
  override def main(args:Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val timeout = Timeout(5 seconds)
    implicit def executionContext = system.dispatcher

    val serverActor = system.actorOf(Props[ServerActor])
//    (serverActor ? None)
//        .foreach { _ => println("Something Happened!") }

    IO(Http) ! Http.Bind(serverActor, interface="127.0.0.1", port=3000)
  }
}

