package com.tidx

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.util.Random
import scalaz.Scalaz._

import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization.ByteArraySerializer

object ProductVisitGenerator extends App {
  val interval = 5.seconds
  val topic    = "product-visit"
  val key      = Array.empty[Byte]

  withKafkaProducer { producer =>
    println(s"Producing a random product visit every $interval")
    while (!shouldStop()) {
      val value = randomProductVisit()
      println(s"Emitting $value")
      producer.send(new ProducerRecord[Array[Byte], ProductVisit](topic, key, value))
      Thread.sleep(interval.toMillis)
    }
  }

  private def withKafkaProducer(block: KafkaProducer[Array[Byte], ProductVisit] => Unit): Unit = {
    val kafkaConfig = Map[String, AnyRef](
      "bootstrap.servers"   -> "127.0.0.1:9092",
      "schema.registry.url" -> "http://127.0.0.1:8081",
      "acks"                -> "all",
      "retries"             -> "10",
      "key.serializer"      -> classOf[ByteArraySerializer],
      "value.serializer"    -> classOf[KafkaAvroSerializer]
    )
    val producer = new KafkaProducer[Array[Byte], ProductVisit](kafkaConfig.asJava)
    try block(producer)
    finally {
      producer.close()
    }
  }

  // Stop producing events when the standard output is closed
  private def shouldStop(): Boolean = System.in.read(Array.empty) == -1

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
//          .build)
      .build()
}
