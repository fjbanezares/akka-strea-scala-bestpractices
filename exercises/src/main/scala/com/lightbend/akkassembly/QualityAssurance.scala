package com.lightbend.akkassembly

import akka.NotUsed
import akka.stream.{ActorAttributes, Supervision}
import akka.stream.scaladsl.Flow

class QualityAssurance {

  val strategy: Supervision.Decider = {
    case _: QualityAssurance.CarFailedInspection => Supervision.Resume
    case _ => Supervision.Stop
  }
  val inspect: Flow[UnfinishedCar, Car, NotUsed] = {
    Flow[UnfinishedCar].collect {
      case UnfinishedCar(Some(color), Some(engine), wheels, upgrade) if wheels.size == 4 =>
        Car(SerialNumber(), color, engine, wheels, upgrade)
      case carFailed => throw QualityAssurance.CarFailedInspection(carFailed)
    }.withAttributes(ActorAttributes.supervisionStrategy(strategy))

    //Add an appropriate supervisor strategy to the inspect flow so that it can continue to process messages when a failure occurs.
  }
}

object QualityAssurance{
  case class CarFailedInspection(carFailed: UnfinishedCar) extends IllegalStateException(s"Car Not finished $carFailed")
}