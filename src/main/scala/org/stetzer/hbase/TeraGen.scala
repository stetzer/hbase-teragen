package org.stetzer.hbase

import java.util.concurrent.{ArrayBlockingQueue, ThreadPoolExecutor, TimeUnit}

import org.apache.commons.lang.{RandomStringUtils => R}

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{Put, HTable}
import org.apache.hadoop.hbase.util.Bytes.{toBytes => B}


object TeraGen {
  def main(args: Array[String]) = {
    if(args.length < 3) {
      System.err.println("Usage: TeraGen <number of records> <table name> <number of concurrent Puts>")
      System.exit(1)
    }

    val totalRecords = args(0).toInt
    val tableName = args(1)
    val concurrentPuts = args(2).toInt

    val queue = new ArrayBlockingQueue[Runnable](10000)
    val exec = new ThreadPoolExecutor(concurrentPuts, concurrentPuts, 30, TimeUnit.SECONDS, queue)

    val config = HBaseConfiguration.create()

    val startTime = System.currentTimeMillis()
    for(i <- 1 to totalRecords) {
      exec.submit(new RunnablePut(config, tableName))
    }

    exec.shutdown()
    exec.awaitTermination(5, TimeUnit.MINUTES)

    println(s"Total records put: $totalRecords in ${System.currentTimeMillis() - startTime} ms")
  }
}

class RunnablePut(conf:Configuration, tableName:String) extends Runnable {
  def run() {
    val p = new Put(B(R.randomAlphanumeric(10)))
    p.add(B("colfam"), B("col"), B(R.randomAlphanumeric(250)))

    new HTable(conf, tableName).put(p)
  }
}
