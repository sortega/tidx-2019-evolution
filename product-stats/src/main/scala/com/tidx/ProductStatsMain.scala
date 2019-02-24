package com.tidx

import scala.collection.JavaConverters._

import io.confluent.kafka.serializers.KafkaAvroDeserializer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.ByteArrayDeserializer

object ProductStatsMain extends App {
  private val topic = "product-visit"
  private var stats = Map.empty[String, Int].withDefaultValue(0)

  withKafkaConsumer { consumer =>
    println(s"Computing product stats from the topic $topic")
    consumer.subscribe(List(topic).asJava)
    while (!shouldStop()) {
      val records = consumer.poll(java.time.Duration.ofSeconds(1))
      records.iterator().asScala.foreach(record => updateStats(record.value()))
      consumer.commitSync()
    }
  }

  private def withKafkaConsumer(block: KafkaConsumer[Array[Byte], ProductVisit] => Unit): Unit = {
    val kafkaConfig = Map[String, AnyRef](
      "bootstrap.servers"    -> "127.0.0.1:9092",
      "schema.registry.url"  -> "http://127.0.0.1:8081",
      "group.id"             -> "product-stats",
      "auto.commit.enable"   -> "false",
      "auto.offset.reset"    -> "earliest",
      "specific.avro.reader" -> "true",
      "key.deserializer"     -> classOf[ByteArrayDeserializer],
      "value.deserializer"   -> classOf[KafkaAvroDeserializer]
    )
    val consumer = new KafkaConsumer[Array[Byte], ProductVisit](kafkaConfig.asJava)
    try block(consumer)
    finally {
      consumer.close()
    }
  }

  // Stop producing events when the standard output is closed
  private def shouldStop(): Boolean = System.in.read(Array.empty) == -1

  private def updateStats(visit: ProductVisit): Unit = {
    val id     = visit.getProductId.toString
    val visits = stats(id) + 1
    stats += (id -> visits)
    println(s"Product $id has now $visits visits")
  }
}
