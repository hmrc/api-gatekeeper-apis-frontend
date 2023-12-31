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

import play.api.libs.json.OFormat
import uk.gov.hmrc.apiplatform.modules.apis.domain.models._
import uk.gov.hmrc.apiplatform.modules.common.domain.models._
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

@Singleton
class ApmConnector @Inject() (http: HttpClient, config: ApmConnector.Config)(implicit ec: ExecutionContext) {

  def fetchAllApis(env: Environment)(implicit hc: HeaderCarrier): Future[List[ApiDefinition]] = {
    http.GET[MappedApiDefinitions](s"${config.serviceBaseUrl}/api-definitions/all?environment=$env")
      .map(_.wrapped.values.toList)
  }

  def fetchApi(serviceName: ServiceName)(implicit hc: HeaderCarrier): Future[Option[Locator[ApiDefinition]]] = {
    implicit val formatter: OFormat[Locator[ApiDefinition]] = Locator.buildLocatorFormatter[ApiDefinition]

    http.GET[Option[Locator[ApiDefinition]]](s"${config.serviceBaseUrl}/api-definitions/service-name/$serviceName")
  }
}

object ApmConnector {

  case class Config(
      serviceBaseUrl: String
    )
}
