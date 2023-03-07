import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import akka.http.scaladsl.unmarshalling.Unmarshal
import java.net.URLEncoder
import scala.io.StdIn.readLine



  object ApiUser extends App {
    implicit val as = ActorSystem()
    implicit val ec = as.dispatcher

    implicit class MapToJson[V](params: Map[String, V]) {
      def toUrlParams: String = params.map { case (k, v) => s"$k=$v" }.mkString("&")
    }
    print("Enter any number: ")
    val numparam = readLine()

    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = s"http://numbersapi.com/$numparam",
      headers = Seq(
        RawHeader("Content-Type", "application/x-www-form-urlencoded"),
        RawHeader("Accept", "application/json")
      ),
      //entity = HttpEntity(ContentTypes.`application/json`, requestBody)
    )
    val performRequestFut = for {
      response <- Http().singleRequest(request)
      status = response.status
      body <- Unmarshal(response.entity).to[String]
      _ = response.entity.discardBytes()
    } yield println(s"status: $status, body: $body")
    performRequestFut.andThen(_ => as.terminate()) // we do not need Await.result because of as running
  }
