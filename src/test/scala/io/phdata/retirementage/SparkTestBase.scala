/*
 * Copyright 2018 phData Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.phdata.retirementage

import java.util.UUID

import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{Row, SparkSession}

trait SparkTestBase {
  val spark = SparkSession
    .builder()
    .appName("retirement-age")
    .master("local[3]")
    .config("spark.sql.parquet.compression.codec", "snappy")
    .config("spark.sql.parquet.binaryAsString", "true")
    .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    .enableHiveSupport()
    .getOrCreate()

  def writeTestTable(dataset: List[List[_]], schema: StructType): String = {
    val tableName = "test_" + UUID.randomUUID().toString.substring(0, 5)

    createDataFrame(dataset, schema).write.saveAsTable(s"default.$tableName")

    tableName
  }

  def createDataFrame(data: List[List[_]], schema: StructType) = {
    val rows = data.map(x => Row(x: _*))
    val rdd  = spark.sparkContext.makeRDD(rows)

    spark.createDataFrame(rdd, schema)
  }
}
