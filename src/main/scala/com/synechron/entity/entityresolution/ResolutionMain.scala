package com.synechron.entity.entityresolution
import com.johnsnowlabs.nlp.{DocumentAssembler, Finisher}
import com.johnsnowlabs.nlp.annotator.{Normalizer, Tokenizer}
import com.johnsnowlabs.nlp.pretrained.PretrainedPipeline
import com.synechron.entity.conf.SparkConfiguration.sparkSessionConf
import org.apache.spark.SparkConf
import org.apache.spark.ml.Pipeline
import org.apache.spark.sql.SparkSession

object ResolutionMain {
  val conf: SparkConf = new SparkConf()
    .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
  val spark: SparkSession = SparkSession.builder().appName("Entity").config(conf).getOrCreate()
  def main(args: Array[String]): Unit = {

    val articlesDF = spark.createDataFrame(Seq(
      ("Tensorflow Google", "Google has announced the release of a beta version of the popular TensorFlow machine learning library"),
      ("Paris metro", "The Paris metro will soon enter the 21st century, ditching single-use paper tickets for rechargeable electronic cards.")
    )).toDF("title", "body")
    val pipeline= PretrainedPipeline("recognize_entities_dl", lang = "en", diskLocation = Option("hdfs://namenode:9000/models/recognize_entities_dl/"))
    val bodyDF = articlesDF.select("body")
    val result = pipeline.transform(bodyDF)
    result.show()
//    val document = new DocumentAssembler()
//      .setInputCol("title")
//      .setOutputCol("document")
//
//    val token = new Tokenizer()
//      .setInputCols("document")
//      .setOutputCol("token")
//
//    val normalizer = new Normalizer()
//      .setInputCols("token")
//      .setOutputCol("normal")
//
//    val finisher = new Finisher()
//      .setInputCols("normal")
//
//    val pipeline = new Pipeline().setStages(Array(document, token, normalizer, finisher))
//
//    pipeline.fit(articlesDF).transform(articlesDF).show()
  }
}
