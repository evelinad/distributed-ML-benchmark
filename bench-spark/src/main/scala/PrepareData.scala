import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.{SparkConf, SparkContext}
import utils.Utils

import scala.util.Try
import scalax.file.Path

/**
  * Created by amirreza on 07/05/16.
  */

class PrepArgParser(arguments: Seq[String]) extends org.rogach.scallop.ScallopConf(arguments) {
  val dataset = opt[String](required = true, short = 'd',
    descr = "absolute address of the libsvm dataset. This must be provided.")
  val partitions = opt[Int](required = false, default = Some(4), short = 'p', validate = (0 <),
    descr = "Number of spark partitions to be used. Optional.")
  val dir = opt[String](required = true, default = Some("../results/"), short = 'w', descr = "working directory where results " +
    "are stored. Default is \"../results\". ")
  val method = opt[String](required = true, short = 'm',
    descr = "Method can be either \"Regression\" or \"Classification\". This must be provided")
  verify()
}

object PrepareData {
  def main(args: Array[String]) {
    //Spark conf
    val conf = new SparkConf().setAppName("Distributed Machine Learning").setMaster("local[*]")
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    //Turn off logs
    val rootLogger = Logger.getRootLogger()
    rootLogger.setLevel(Level.ERROR)
    //Parse arguments
    val parser = new PrepArgParser(args)
    val dataset = parser.dataset()
    var workingDir = parser.dir()
    val numPartitions = parser.partitions()
    val method = parser.method()

    //Load data
    val (train, test) = method match {
      case "Classification" => Utils.loadAbsolutLibSVMBinaryClassification(dataset, numPartitions, sc)
      case "Regression" => Utils.loadAbsolutLibSVMRegression(dataset, numPartitions, sc)
      case _ => throw new IllegalArgumentException("The method " + method + " is not supported.")
    }

    // append "/" to workingDir if necessary
    workingDir = workingDir + ( if (workingDir.takeRight(1) != "/") "/" else "" )
    val trainPath: Path = Path.fromString(workingDir + "train")
    Try(trainPath.deleteRecursively(continueOnFailure = false))
    val testPath: Path = Path.fromString(workingDir + "test")
    Try(testPath.deleteRecursively(continueOnFailure = false))
    MLUtils.saveAsLibSVMFile(train, workingDir + "train")
    MLUtils.saveAsLibSVMFile(test, workingDir + "test")
  }
}
