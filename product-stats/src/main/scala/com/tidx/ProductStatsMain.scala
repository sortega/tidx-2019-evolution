package com.tidx

import scala.collection.JavaConverters._

import com.tidx.commons.CliApp
import com.tidx.commons.KafkaUtils.withKafkaConsumer

object ProductStatsMain extends CliApp {

  def run(): Unit = {
    var stats = Map.empty[String, Int].withDefaultValue(0)
    val topic = "product-visit"

    withKafkaConsumer[ProductVisit](appName = "product-stats") { consumer =>
      println(s"Computing product stats from the topic $topic")
      consumer.subscribe(List(topic).asJava)
      while (!shouldStop) {
        val records = consumer.poll(java.time.Duration.ofSeconds(1))
        records.iterator().asScala.foreach(record => updateStats(record.value()))
        consumer.commitSync()
      }
    }

    def updateStats(visit: ProductVisit): Unit = {
      val id     = visit.getProductId.toString
      val visits = stats(id) + 1
      stats += (id -> visits)
      println(s"Product $id has now $visits visits")
    }
  }
}
