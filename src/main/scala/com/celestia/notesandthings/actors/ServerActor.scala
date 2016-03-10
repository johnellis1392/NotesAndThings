package com.celestia.notesandthings.actors


import scala.concurrent.duration._

import akka.actor.{Actor, ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import akka.pattern.ask
import akka.http._

import spray.json.DefaultJsonProtocol
import spray.json.DefaultJsonProtocol._
import spray.httpx.SprayJsonSupport
import spray.httpx.SprayJsonSupport._
import spray.httpx.marshalling._
import spray.httpx.unmarshalling._
import spray.http._
import spray.routing.{Directives, HttpService}
import spray.http._
import MediaTypes._

import com.celestia.notesandthings.data._


/**
  * Created by celestia on 3/5/16.
  */
class ServerActor extends Actor
    with Directives
    with DefaultJsonProtocol
    with HttpService {

  implicit val system = ActorSystem()
  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(5 seconds)
  implicit val materializer = ActorMaterializer()

  override def actorRefFactory = context
  lazy val db = system.actorOf(Props[DBActor])


  override def receive = runRoute(routes)
  def routes = {
    logRequestResponse("akka-routes") {
      pathPrefix("notes") {
        pathEnd {
          post {
            entity(as[Note]) { note =>
              println(s"Received request: $note")
              createNote(note)
            }
          } ~ get {
            listNotes
          }
        } ~ path(Segment) { id =>
          get {
            getNote(id)
          }
        }
      }
    }
  }


  /**
    * Query the given actor to create a new note
    *
    * @param note
    * @return
    */
  def createNote[A](note: Note) = {
    complete {
      (db ? Create(note)).map {
        case Ok =>
//          marshal(s"Success")
          s"Success"
        case _ =>
//          marshal(s"Something went wrong")
          s"Something went wrong"
      }
    }
  }


  def listNotes = {
    complete {
      (db ? GetAll).map {
        case Success(l: List[Note]) =>
          l.toStream
//          marshal(l.toStream)
//        case _ =>
//          marshal(s"An error occurred")
//          s"An error occurred"
      }
    }
  }


  def getNote(id: String) = {
    rejectEmptyResponse {
      complete {
        (db ? Get(id)).mapTo[Option[Note]]
//        (db ? Get(id)).map {
//          case Some(n: Note) =>
//            //          marshal(n)
//            n
//          case None =>
//
//          //          marshal(s"Not Found")
//          //          s"Not Found"
//        }
      }
    }
  }
}
