package com.tidx

import scala.concurrent.duration._
import scala.util.Random
import scalaz.Scalaz._

import com.tidx.commons.CliApp
import com.tidx.commons.KafkaUtils.withKafkaProducer
import org.apache.kafka.clients.producer._

object ProductVisitGenerator extends CliApp {

  override def run(): Unit = {
    val interval = 3.seconds
    val topic    = "product-visit"
    val key      = Array.empty[Byte]

    withKafkaProducer[ProductVisit] { producer =>
      println(s"Producing a random product visit every $interval")
      while (!shouldStop) {
        val value = randomProductVisit()
        println(s"Emitting $value")
        producer.send(new ProducerRecord(topic, key, value))
        Thread.sleep(interval.toMillis)
      }
    }
  }

  private def randomProductVisit(): ProductVisit =
    ProductVisit.newBuilder
      .setProductId(s"product-${Random.nextInt(100)}")
      .setUserId(s"user-${Random.nextInt(10)}")
      .setTimestamp(System.currentTimeMillis())
      .setReferralToken(Random.nextBoolean().option(s"token-${Random.nextInt(1000)}").orNull)
//      .setLocation(
//        LatLon.newBuilder
//          .setLat(Random.nextFloat)
//          .setLon(Random.nextFloat)
//          .build
//      )
      .build()
}
