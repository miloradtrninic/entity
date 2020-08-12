package com.synechron.entity.entityresolution
import com.synechron.entity.conf.SparkConfiguration
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.ml.classification.NaiveBayes
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{HashingTF, IndexToString, StopWordsRemover, StringIndexer, StringIndexerModel, Tokenizer}
import org.apache.spark.sql.functions.{col, concat, lit, when}

object ResolutionMain {
  val spark: SparkSession = SparkConfiguration.sparkSessionConf()
  def main(args: Array[String]): Unit = {

    val trainData = readDataFromCSV("hdfs://namenode:9000/model/news/data/naive_bayes/news_crawler_df_train.csv")
    val testData = readDataFromCSV("hdfs://namenode:9000/model/news/data/naive_bayes/news_crawler_df_test.csv")

    val model = createModel(trainData)

    val transformed = model.transform(testData)
    transformed.show()

    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("label")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")
    val accuracy = evaluator.evaluate(transformed)
    println()
    println(s"Test set accuracy = $accuracy")
    println()
  }
  def readDataFromCSV(path: String): DataFrame = {
    val SPACE = " "
    val csvDF = spark.read.option("header", "true")
      .option("delimiter", "\t")
      .csv(path)
    csvDF.withColumn("title_body", concat(col("title"), lit(SPACE), col("body")))
      .drop("body", "title")
  }
  def createModel(trainData: DataFrame): PipelineModel = {
    val nb = new NaiveBayes()
      .setFeaturesCol("features")
    val labelIndexer = new StringIndexer()
      .setInputCol("currency")
      .setOutputCol("label")
      .fit(trainData)
      .setHandleInvalid("keep")
    val predictionToLabel = new IndexToString()
      .setInputCol("prediction")
      .setOutputCol("currency_prediction")
      .setLabels(labelIndexer.labels)

    val pipeline: Pipeline = new Pipeline()
      .setStages(Array(labelIndexer, sentenceToWords, removeStopWords, defineFeatures, nb, predictionToLabel))
    pipeline.fit(trainData)
  }
  def sentenceToWords: Tokenizer = {
    new Tokenizer().setInputCol("title_body").setOutputCol("words")
  }
  def removeStopWords: StopWordsRemover = {
    new StopWordsRemover().setInputCol("words").setOutputCol("removed_stop")
  }
  def defineFeatures: HashingTF = {
    new HashingTF().setInputCol("removed_stop").setOutputCol("features")
  }
}
