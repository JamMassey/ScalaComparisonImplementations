// Databricks notebook source
//Read in csv, defining it schema
val sparkDF = spark.read.format("csv")
  .option("header", "true")
  .option("inferSchema", "true")
  .csv("/FileStore/tables/heart_disease-d1003.csv")

//Schema information
display(sparkDF)
sparkDF.printSchema

// COMMAND ----------

import org.apache.spark.ml.feature.VectorAssembler

//Create feature vector
val featureCols = Array("age", "sex", "cp", "trestbps", "chol", "fbs", "restecg", "thalach", "exang", "oldpeak","slope","ca","thal")
val assembler = new VectorAssembler().setInputCols(featureCols).setOutputCol("features")
val featureDF = assembler.transform(sparkDF)
//Confirming feature vector
featureDF.show

// COMMAND ----------

import org.apache.spark.ml.classification.LogisticRegression
//Split into train and test set
val Array(trainingDF, testDF) = sparkDF.randomSplit(Array[Double](0.7, 0.3), 50)
//Initialize new Logistic Regression Model
val lr = new LogisticRegression()
  .setLabelCol("target")
  .setPredictionCol("predictions")
  .setMaxIter(10)
  .setRegParam(0.0)

// COMMAND ----------

import org.apache.spark.ml.Pipeline

//Initialise our pipeline 
val pipeline = new Pipeline().setStages(Array(assembler, lr))

//Fit our training data, create a new variable to access our model
val model = pipeline.fit(trainingDF)

//Make predictions
val predictions = model.transform(testDF)

// COMMAND ----------

//Isolate fields for evaluation
val predictionAndTargets = predictions.select($"target", $"predictions").as[(Double, Double)]

//Evaluation metrics
val right = predictionAndTargets.filter($"target" === $"predictions").count().toDouble
val wrong = predictionAndTargets.filter(!($"target" === $"predictions")).count().toDouble
println("Right Count: " + right)
println("Wrong Count: " + wrong)
println("True Positives: " + predictionAndTargets.filter($"predictions" === 1.0).filter($"target" === $"predictions").count())
println("True Negatives: " + predictionAndTargets.filter($"predictions" === 0.0).filter(($"target" === $"predictions")).count())
println("False Positive: " + predictionAndTargets.filter($"predictions" === 1.0).filter(!($"target" === $"predictions")).count())
println("False Negative: " + predictionAndTargets.filter($"predictions" === 0.0).filter(!($"target" === $"predictions")).count())
val total = predictionAndTargets.count().toDouble
println("Percentage Right: " + wrong/total*100)
println("Percentage Right: " + right/total*100)

// COMMAND ----------

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.VectorAssembler

val sparkDF = spark.read.format("csv")
  .option("header", "true")
  .option("inferSchema", "true")
  .csv("/FileStore/tables/heart_disease-d1003.csv")
val featureCols = Array("age", "sex", "cp", "trestbps", "chol", "fbs", "restecg", "thalach", "exang", "oldpeak","slope","ca","thal")
val assembler = new VectorAssembler().setInputCols(featureCols).setOutputCol("features")
val featureDF = assembler.transform(sparkDF)
val Array(trainingDF, testDF) = sparkDF.randomSplit(Array[Double](0.7, 0.3), 50)

//Initialize new Logistic Regression Model
val lr = new LogisticRegression()
  .setLabelCol("target")
  .setPredictionCol("predictions")
  .setMaxIter(10)
  .setRegParam(0.0)

//Initialise our pipeline 
val pipeline = new Pipeline().setStages(Array(assembler, lr))

//Fit our training data, create a new variable to access our model
val model = pipeline.fit(trainingDF)

//Make predictions
val predictions = model.transform(testDF)

val predictionAndTargets = predictions.select($"target", $"predictions").as[(Double, Double)]

//Evaluation metrics
val right = predictionAndTargets.filter($"target" === $"predictions").count().toDouble
val wrong = predictionAndTargets.filter(!($"target" === $"predictions")).count().toDouble
println("Right Count: " + right)
println("Wrong Count: " + wrong)
println("True Positives: " + predictionAndTargets.filter($"predictions" === 1.0).filter($"target" === $"predictions").count())
println("True Negatives: " + predictionAndTargets.filter($"predictions" === 0.0).filter(($"target" === $"predictions")).count())
println("False Positive: " + predictionAndTargets.filter($"predictions" === 1.0).filter(!($"target" === $"predictions")).count())
println("False Negative: " + predictionAndTargets.filter($"predictions" === 0.0).filter(!($"target" === $"predictions")).count())
val total = predictionAndTargets.count().toDouble
println("Percentage Right: " + wrong/total*100)
println("Percentage Right: " + right/total*100)
