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

package uk.gov.hmrc.apigatekeeperapisfrontend.services

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

import uk.gov.hmrc.http.HeaderCarrier

import uk.gov.hmrc.apigatekeeperapisfrontend.connectors.ApmConnector
import uk.gov.hmrc.apigatekeeperapisfrontend.models.{DisplayApiEvent, EnvironmentDefinitions}
import uk.gov.hmrc.apiplatform.modules.apis.domain.models._
import uk.gov.hmrc.apiplatform.modules.common.domain.models.Environment

class ApmService @Inject() (apmConnector: ApmConnector)(implicit ec: ExecutionContext) {

  def fetchAllApis()(implicit hc: HeaderCarrier): Future[EnvironmentDefinitions] = {
    for {
      sandboxApis <- apmConnector.fetchAllApis(Environment.SANDBOX)
      prodApis    <- apmConnector.fetchAllApis(Environment.PRODUCTION)
    } yield EnvironmentDefinitions(sandboxApis, prodApis)
  }

  def fetchApi(serviceName: ServiceName)(implicit hc: HeaderCarrier): Future[Option[Locator[ApiDefinition]]] = {
    apmConnector.fetchApi(serviceName)
  }

  def fetchApiEvents(serviceName: ServiceName, includeNoChange: Boolean = true)(implicit hc: HeaderCarrier): Future[List[DisplayApiEvent]] = {
    apmConnector.fetchApiEvents(serviceName, includeNoChange)
  }
}
