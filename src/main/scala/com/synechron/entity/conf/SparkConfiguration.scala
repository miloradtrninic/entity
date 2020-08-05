package com.synechron.entity.conf

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object SparkConfiguration {
  def sparkSessionConf(): SparkSession = {
    val conf: SparkConf = new SparkConf()
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

    SparkSession.builder().appName("Entity").config(conf).getOrCreate()
  }
}
