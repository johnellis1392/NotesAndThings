package com.celestia.notesandthings.data

/**
  * Created by celestia on 3/5/16.
  */
sealed trait DBCommands
case object GetAll extends DBCommands
case class Get(id: String) extends DBCommands
case class Create(a: Note) extends DBCommands
case class Delete(id: String) extends DBCommands
case class Update(a: Note) extends DBCommands

