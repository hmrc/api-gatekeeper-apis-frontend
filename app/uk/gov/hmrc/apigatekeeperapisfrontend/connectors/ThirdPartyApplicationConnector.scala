/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.apigatekeeperapisfrontend.connectors

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

import com.google.common.base.Charsets
import org.apache.pekko.stream.Materializer
import org.apache.pekko.stream.scaladsl.{JsonFraming, Sink, Source}
import org.apache.pekko.util.ByteString

import play.api.http.HeaderNames
import play.api.libs.json.{Json, Reads}
import uk.gov.hmrc.http.client.{HttpClientV2, readSource}
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.ApplicationWithCollaborators
import uk.gov.hmrc.apiplatform.modules.applications.core.interface.models.QueriedApplication
import uk.gov.hmrc.apiplatform.modules.applications.query.domain.models.ApplicationQuery
import uk.gov.hmrc.apiplatform.modules.applications.query.domain.models.Param.*
import uk.gov.hmrc.apiplatform.modules.applications.query.domain.services.QueryParamsToQueryStringMap
import uk.gov.hmrc.apiplatform.modules.common.domain.models.ApiContext

@Singleton
class ThirdPartyApplicationConnector @Inject() (http: HttpClientV2, config: ThirdPartyApplicationConnector.Config)(implicit ec: ExecutionContext, mat: Materializer) {

  def fetchAllApplications(apiContext: ApiContext)(implicit hc: HeaderCarrier): Future[List[ApplicationWithCollaborators]] = {
    val applicationQry = ApplicationQuery.GeneralOpenEndedApplicationQuery(List(ApiContextQP(apiContext), ExcludeDeletedQP))
    val params         = QueryParamsToQueryStringMap.toHttpQueryString(applicationQry)

    rawQueryStream[QueriedApplication, ApplicationWithCollaborators](params)(_.asAppWithCollaborators)
  }

  private def rawQueryStream[S, T](qryStringMap: Map[String, String])(fn: S => T)(implicit hc: HeaderCarrier, rds: Reads[S]): Future[List[T]] = {
    http
      .get(url"${config.serviceBaseUrl}/query?$qryStringMap")
      .setHeader(HeaderNames.ACCEPT -> "application/stream+json")
      .stream[Source[ByteString, ?]]
      .map { response =>
        response.via(JsonFraming.objectScanner(maximumObjectLength = Int.MaxValue))
      }
      .map {
        _.map { bytestring =>
          Json.fromJson[S](Json.parse(bytestring.decodeString(Charsets.UTF_8)))
            .asOpt
        }
      }
      .map(_.collect {
        case Some(s) => s
      })
      .map(_.map(fn))
      .flatMap(_.runWith(Sink.seq))
      .map(_.toList)
  }
}

object ThirdPartyApplicationConnector {

  case class Config(
      serviceBaseUrl: String
    )
}
