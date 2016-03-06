package com.celestia.notesandthings.actors

import akka.actor.{Actor, ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.celestia.notesandthings.data.Note
import com.celestia.notesandthings.{Create, Get}
import spray.json.DefaultJsonProtocol
import spray.routing.{Directives, HttpService}

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
