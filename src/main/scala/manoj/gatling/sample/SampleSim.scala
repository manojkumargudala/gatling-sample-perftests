package manoj.gatling.sample

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.core.feeder.RecordSeqFeederBuilder;
import io.gatling.core.structure.ChainBuilder

class SampleSim extends SampleGatlingConfig {
  //  val namespaces: Map[String, String] = Map(("", "http://www.w3.org/1999/xlink"));
  val namespaces: List[(String, String)] = List(("xlink", "http://www.w3.org/1999/xlink"));
  //val namespavelist: List = new ArrayList("");
  val homwReqstBldr: HttpRequestBuilder = http("GET HOME PAGE").get("/sqlrest/").check(xpath("//@xlink:href", namespaces).findAll.saveAs("difflists"));
  // val homwReqstBldr: HttpRequestBuilder = http("GET HOME PAGE").get("/sqlrest/").check(xpath("//@xlink:href", namespaces).saveAs("difflist"));
  val queryParamFeeder: RecordSeqFeederBuilder[String] = csv("abcd.csv").random
  //  val firstResultQuery: HttpRequestBuilder = http("LOAD_SEARCH_RESULT").get("${refurls}")
  //feed(queryParamFeeder)
  var detailUrls: String = "detailUrls"
  var detailUrl: String = "detailUrl"
  var detailedChain: ChainBuilder = exec(getHttpRequestBlr(getGatlingVariable("difflist"), detailUrls))
    .foreach(getGatlingVariable(detailUrls), detailUrl)(exec(http(getGatlingVariable(detailUrl)).get(getGatlingVariable(detailUrl))))
  val scn: ScenarioBuilder = scenario("BasicSimulation").feed(queryParamFeeder)
    .exec(homwReqstBldr)
    .foreach("${difflists}", "difflist")(detailedChain)
  // .exec(get)
  // .foreach("${refurls}", "refurl")(exec(http("${refurl}").get("${refurl}")))
  def getHttpRequestBlr(myUrl: String, storeLst: String): HttpRequestBuilder = {
    println("URL is " + myUrl)
    http(myUrl)
      .get(myUrl).check(xpath("//@xlink:href", namespaces).findAll.saveAs(storeLst))

  }
  def getGatlingVariable(variable: String): String = {
    "${" + variable + "}"
  }
  setUp(
    scn.inject(atOnceUsers(10))).protocols(httpConf)

}