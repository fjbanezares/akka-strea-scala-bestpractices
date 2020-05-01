package com.lightbend.akkassembly

import akka.NotUsed
import akka.stream.FlowShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, Source}

class UpgradeShop {

  val installUpgrades: Flow[UnfinishedCar, UnfinishedCar, NotUsed]= Flow.fromGraph(
    GraphDSL.create(){

      implicit builder: GraphDSL.Builder[NotUsed] =>

        import akka.stream.scaladsl.GraphDSL.Implicits._

        val splitThreePaths = builder.add(Broadcast[UnfinishedCar](3))
        val combineInOne = builder.add(Merge[UnfinishedCar](3))

        val flowDX: Flow[UnfinishedCar, UnfinishedCar, NotUsed] =
          Flow[UnfinishedCar].map(car => car.installUpgrade(Upgrade.DX))

        val flowSport: Flow[UnfinishedCar, UnfinishedCar, NotUsed] =
          Flow[UnfinishedCar].map(car => car.installUpgrade(Upgrade.Sport))

        val flowStanderd: Flow[UnfinishedCar, UnfinishedCar, NotUsed] = Flow[UnfinishedCar]

        splitThreePaths ~> flowDX ~> combineInOne
        splitThreePaths ~> flowSport ~> combineInOne
        splitThreePaths ~> flowStanderd ~> combineInOne

        FlowShape(splitThreePaths.in,combineInOne.out)
    }
  )






}
