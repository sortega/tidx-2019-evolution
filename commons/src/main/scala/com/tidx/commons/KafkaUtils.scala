package com.tidx.commons

import scala.collection.JavaConverters._

import io.confluent.kafka.serializers.{KafkaAvroDeserializer, KafkaAvroSerializer}
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer}

object KafkaUtils {
  private val commonConfig = Map[String, AnyRef](
    "bootstrap.servers"   -> "127.0.0.1:9092",
    "schema.registry.url" -> "http://127.0.0.1:8081"
  )

  def withKafkaProducer[R <: SpecificRecord](block: KafkaProducer[Array[Byte], R] => Unit): Unit = {
    val kafkaConfig = commonConfig ++ Map(
      "acks"             -> "all",
      "retries"          -> "10",
      "key.serializer"   -> classOf[ByteArraySerializer],
      "value.serializer" -> classOf[KafkaAvroSerializer]
    )
    val producer = new KafkaProducer[Array[Byte], R](kafkaConfig.asJava)
    try block(producer)
    finally {
      producer.close()
    }
  }

  def withKafkaConsumer[R <: SpecificRecord](
      appName: String
  )(
      block: KafkaConsumer[Array[Byte], R] => Unit
  ): Unit = {
    val kafkaConfig = commonConfig ++ Map(
      "group.id"             -> appName,
      "auto.commit.enable"   -> "false",
      "auto.offset.reset"    -> "earliest",
      "specific.avro.reader" -> "true",
      "key.deserializer"     -> classOf[ByteArrayDeserializer],
      "value.deserializer"   -> classOf[KafkaAvroDeserializer]
    )
    val consumer = new KafkaConsumer[Array[Byte], R](kafkaConfig.asJava)
    try block(consumer)
    finally {
      consumer.close()
    }
  }
}
