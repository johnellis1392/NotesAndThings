package com.celestia.notesandthings

import com.celestia.notesandthings.actors.{ServerActor, DBActor}
import com.celestia.notesandthings.data.{Response, DBCommands}

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

import scala.util.Properties

//import com.mongodb.casbah._
import com.mongodb.casbah.Imports._
//import com.mongodb.casbah.query.Imports._



/**
  * Created by celestia on 3/1/16.
  */
object Main extends App {
  override def main(args:Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val timeout = Timeout(5 seconds)
    implicit def executionContext = system.dispatcher

    val interface = "0.0.0.0"
    val port = Properties.envOrElse("PORT", "8080").toInt
    val serverActor = system.actorOf(Props[ServerActor])

    IO(Http) ask Http.Bind(serverActor, interface=interface, port=port)
  }
}

