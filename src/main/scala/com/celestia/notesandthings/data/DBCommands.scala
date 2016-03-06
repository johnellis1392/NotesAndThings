package com.celestia.notesandthings.data

/**
  * Created by celestia on 3/5/16.
  */
sealed trait DBCommands
case class Create[A](a: A) extends DBCommands
case class Delete(id: Int) extends DBCommands
case class Get(id: Int) extends DBCommands
case class Update[A](a: A) extends DBCommands

