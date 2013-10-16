package org.stetzer.hbase

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{Put, HTable}
import org.apache.hadoop.hbase.util.Bytes.{toBytes => B}

import org.apache.commons.lang.{RandomStringUtils => R}

object TeraGen {
  def main(args: Array[String]) = {
    if(args.length < 1) {
      System.err.println("Usage: TeraGen <number of records> <table name>")
      System.exit(1)
    }

    val totalRecords = args(0).toInt
    val tableName = args(1)

    val config = HBaseConfiguration.create()
    val table = new HTable(config, tableName)

    val startTime = System.currentTimeMillis()
    for(i <- 1 to totalRecords) {
      val p = new Put(B(R.randomAlphanumeric(10)))
      p.add(B("colfam"), B("col"), B(R.randomAlphanumeric(250)))

      table.put(p)
    }

    println(s"Total records put: $totalRecords in ${System.currentTimeMillis() - startTime} ms")
  }
}
