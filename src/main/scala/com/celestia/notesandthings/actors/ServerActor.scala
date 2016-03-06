package com.celestia.notesandthings.actors


import scala.concurrent.duration._
//import scala.util.{Try, Success, Failure}

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

import com.celestia.notesandthings.data.{Success, Get, Note, GetAll}


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
//      path("notes") {
//        get {
//          complete {
//            (db ? GetAll)
//                .map {
//                  case l@(h :: t) =>
//                    s"$l"
//                  case _ =>
//                    s"Something went wrong"
//                }
//          }
//        }
//      } ~
//      path("notes" / Segment) { id =>
//        import Note._
//        get {
//          complete {
//            (db ? Get(id)).map {
//              case n@Note(_, _, _) =>
//                marshal(n)
//              case _ =>
//                marshal(s"Something went wrong")
//            }
//          }
//        }
//      } ~
      pathPrefix("notes") {
        pathEnd {
          get {
            complete {
              (db ? GetAll).map {
                case Success(l:List[Note]) =>
                  marshal(l.toStream)
                case _ =>
                  marshal(s"An error occurred")
              }
            }
          }
        } ~
        path(Segment) { id =>
          import Note._
          get {
            complete {
              (db ? Get(id)).map {
                case Success(n:Note) =>
                  marshal(n)
                case _ =>
                  marshal(s"An error occurred")
              }
            }
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
          s"Posted Something"
        }
      }
    }
  }

  override def receive = runRoute(routes)
}
