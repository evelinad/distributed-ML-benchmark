package MLbenchmark.utils

import org.apache.spark.rdd.RDD
import breeze.linalg.{SparseVector, Vector}
import java.io._


// Labeled point with sparse features for classification or regression tasks
case class LabeledPoint(val label: Double, val features: SparseVector[Double])


/** Algorithm Params
   * @param loss - the loss function l_i (assumed equal for all i)
   * @param n - number of data points
   * @param wInit - initial weight vector
   * @param numRounds - number of outer iterations (T in the paper)
   * @param localIters - number of inner localSDCA iterations (H in the paper)
   * @param lambda - the regularization parameter
   * @param beta - scaling parameter for CoCoA
   * @param gamma - aggregation parameter for CoCoA+ (gamma=1 for adding, gamma=1/K for averaging) 
   */
case class Params(
    loss: (LabeledPoint, Vector[Double]) => Double, 
    n: Int,
    wInit: Vector[Double], 
    numRounds: Int, 
    localIters: Int, 
    lambda: Double, 
    beta: Double,
    gamma: Double)


/** Debug Params
   * @param testData
   * @param debugIter
   * @param seed
   * @param chkptIter checkpointing the resulting RDDs from time to time, to ensure persistence and shorter dependencies
   */
case class DebugParams(
    testData: RDD[LabeledPoint],
    debugIter: Int,
    seed: Int,
    chkptIter: Int)

class DebugParamsML(trainData: RDD[LabeledPoint], testData: RDD[LabeledPoint])
{
    def testError(weights:Vector[Double], iterNum: Int, name: String,time:Long) = {
      val MSE_error = OptUtils.computeMSE(testData, weights)
      val classify_error = OptUtils.computeClassificationError(testData, weights)
      var pw = new PrintWriter(new BufferedWriter(new FileWriter("output/MSE_" +name+".txt", true)))
      pw.println(MSE_error)
      pw.flush()
      pw.close

      pw = new PrintWriter(new BufferedWriter(new FileWriter("output/Classify_" +name+".txt", true)))
      pw.println(classify_error)
      pw.flush()
      pw.close

      pw = new PrintWriter(new BufferedWriter(new FileWriter("output/Iter_" +name+".txt", true)))
      pw.println(iterNum)
      pw.flush()
      pw.close

      pw = new PrintWriter(new BufferedWriter(new FileWriter("output/time_" +name+".txt", true)))
      pw.println(time)
      pw.flush()
      pw.close

      println("Classsification error: " + OptUtils.computeClassificationError(testData, weights))
      println("MSE: " + OptUtils.computeMSE(testData, weights))
      println("iterations: " + iterNum)
      println("time: " + time + "ms")

    }
}

object TestError
{
  def testError(weights:Vector[Double], iterNum: Int, name: String, testData: RDD[LabeledPoint], time:Long) = {
    val MSE_error = OptUtils.computeMSE(testData, weights)
    val classify_error = OptUtils.computeClassificationError(testData, weights)
    var pw = new PrintWriter(new BufferedWriter(new FileWriter("output/MSE_" +name+".txt", true)))
    pw.println(MSE_error)
    pw.flush()
    pw.close

    pw = new PrintWriter(new BufferedWriter(new FileWriter("output/Classify_" +name+".txt", true)))
    pw.println(classify_error)
    pw.flush()
    pw.close

    pw = new PrintWriter(new BufferedWriter(new FileWriter("output/Iter_" +name+".txt", true)))
    pw.println(iterNum)
    pw.flush()
    pw.close

    pw = new PrintWriter(new BufferedWriter(new FileWriter("output/time_" +name+".txt", true)))
    pw.println(time)
    pw.flush()
    pw.close

    println("Classsification error: " + OptUtils.computeClassificationError(testData, weights))
    println("MSE: " + OptUtils.computeMSE(testData, weights))
    println("iterations: " + iterNum)
    println("time: " + time + "ms")
  }
}