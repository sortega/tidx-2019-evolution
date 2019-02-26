package com.tidx

import scala.collection.JavaConverters._

import io.confluent.kafka.serializers.KafkaAvroDeserializer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.ByteArrayDeserializer

object Recommender extends App {
  private val n            = 10
  private val topic        = "product-visit"
  private var userProfiles = Map.empty[String, UserProfile].withDefaultValue(UserProfile.Empty)

  withKafkaConsumer { consumer =>
    println(s"Computing last $n products seen from the topic $topic to make recommendations")
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
      "group.id"             -> "recommender",
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
    val userId      = visit.getUserId.toString
    val userProfile = userProfiles(userId).update(visit.getProductId.toString, n)
    userProfiles += (userId -> userProfile)
    println(s"User $userId updated profile: $userProfile")
  }
}
