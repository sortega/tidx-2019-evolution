package com.tidx

import scala.collection.JavaConverters._

import com.tidx.commons.KafkaUtils.withKafkaConsumer
import com.tidx.commons.CliApp

object Recommender extends CliApp {
  def run(): Unit = {
    val n            = 10
    val topic        = "product-visit"
    var userProfiles = Map.empty[String, UserProfile].withDefaultValue(UserProfile.Empty)

    withKafkaConsumer[ProductVisit](appName = "recommender") { consumer =>
      println(s"Computing last $n products seen from the topic $topic to make recommendations")
      consumer.subscribe(List(topic).asJava)
      while (!synchronized(shouldStop)) {
        val records = consumer.poll(java.time.Duration.ofSeconds(1))
        records.iterator().asScala.foreach(record => updateStats(record.value()))
        consumer.commitSync()
      }
    }

    def updateStats(visit: ProductVisit): Unit = {
      val userId      = visit.getUserId.toString
      val userProfile = userProfiles(userId).update(visit.getProductId.toString, n)
      userProfiles += (userId -> userProfile)
      println(s"User $userId updated profile: $userProfile")
    }
  }
}
