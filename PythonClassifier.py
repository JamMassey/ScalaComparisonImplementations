# Databricks notebook source
sparkDF = (spark.read.format("csv")
  .option("header", "true")
  .option("inferSchema", "true")
  .csv("/FileStore/tables/heart_disease-d1003.csv"))

display(sparkDF)
sparkDF.printSchema

# COMMAND ----------

from pyspark.ml.feature import VectorAssembler


featureCols = ["age", "sex", "cp", "trestbps", "chol", "fbs", "restecg", "thalach", "exang", "oldpeak","slope","ca","thal"]
assembler = VectorAssembler(inputCols=featureCols, outputCol="features")
featureDF = assembler.transform(sparkDF)

display(featureDF)

# COMMAND ----------

from pyspark.ml.classification import LogisticRegression


sets = sparkDF.randomSplit([0.7, 0.3], 50)
trainingDF = sets[0]
testDF = sets[1]

lr = LogisticRegression(maxIter=10, regParam=0.0, labelCol="target", predictionCol="predictions")


# COMMAND ----------

from pyspark.ml import Pipeline

pipeline = Pipeline(stages=[assembler, lr])
model = pipeline.fit(trainingDF)

predictions = model.transform(testDF)

# COMMAND ----------

from pyspark.sql.functions import col
predictionAndTargets = predictions.select("target", "predictions")

right = predictionAndTargets.filter(col("target") == col("predictions")).count()
wrong = predictionAndTargets.filter(col("target") != col("predictions")).count()
print("Right Count: " + str(right))
print("Wrong Count: " + str(wrong))
print("True Positives: " + str(predictionAndTargets.filter(col("predictions") == 1.0).filter(col("target") == col("predictions")).count()))
print("True Negatives: " + str(predictionAndTargets.filter(col("predictions") == 0.0).filter(col("target") == col("predictions")).count()))
print("False Positive: " + str(predictionAndTargets.filter(col("predictions") == 1.0).filter(col("target") != col("predictions")).count()))
print("False Negative: " + str(predictionAndTargets.filter(col("predictions") == 0.0).filter(col("target") != col("predictions")).count()))
total = predictionAndTargets.count()
print("Percentage Right: " + str(wrong/total*100))
print("Percentage Right: " + str(right/total*100))

# COMMAND ----------

from pyspark.sql.functions import col
from pyspark.ml.feature import VectorAssembler
from pyspark.ml.classification import LogisticRegression
from pyspark.ml import Pipeline
from pyspark.sql.functions import col
sparkDF = (spark.read.format("csv").option("header", "true").option("inferSchema", "true").csv("/FileStore/tables/heart_disease-d1003.csv"))
featureCols = ["age", "sex", "cp", "trestbps", "chol", "fbs", "restecg", "thalach", "exang", "oldpeak","slope","ca","thal"]
assembler = VectorAssembler(inputCols=featureCols, outputCol="features")
featureDF = assembler.transform(sparkDF)
sets = sparkDF.randomSplit([0.7, 0.3], 50)
trainingDF = sets[0]
testDF = sets[1]
lr = LogisticRegression(maxIter=10, regParam=0.0, labelCol="target", predictionCol="predictions")
pipeline = Pipeline(stages=[assembler, lr])
model = pipeline.fit(trainingDF)
predictions = model.transform(testDF)
predictionAndTargets = predictions.select("target", "predictions")
right = predictionAndTargets.filter(col("target") == col("predictions")).count()
wrong = predictionAndTargets.filter(col("target") != col("predictions")).count()
print("Right Count: " + str(right))
print("Wrong Count: " + str(wrong))
print("True Positives: " + str(predictionAndTargets.filter(col("predictions") == 1.0).filter(col("target") == col("predictions")).count()))
print("True Negatives: " + str(predictionAndTargets.filter(col("predictions") == 0.0).filter(col("target") == col("predictions")).count()))
print("False Positive: " + str(predictionAndTargets.filter(col("predictions") == 1.0).filter(col("target") != col("predictions")).count()))
print("False Negative: " + str(predictionAndTargets.filter(col("predictions") == 0.0).filter(col("target") != col("predictions")).count()))
total = predictionAndTargets.count()
print("Percentage Right: " + str(wrong/total*100))
print("Percentage Right: " + str(right/total*100))
