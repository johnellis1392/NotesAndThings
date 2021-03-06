package com.celestia.notesandthings.data

/**
  * Created by celestia on 3/5/16.
  */
sealed trait Response
case object Ok extends Response
case class Success[A](a: A) extends Response
sealed trait ErrorResponse extends Response
case object InvalidOperation extends ErrorResponse
case object InvalidParameter extends ErrorResponse
case object Exception extends ErrorResponse

