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

import scala.concurrent.Future

import org.mockito.{ArgumentMatchersSugar, MockitoSugar}

import uk.gov.hmrc.apigatekeeperapisfrontend.models.EnvironmentDefinitions
import uk.gov.hmrc.apigatekeeperapisfrontend.utils.ApiDataTestData
import uk.gov.hmrc.apiplatform.modules.apis.domain.models._

trait ApmServiceMockModule extends MockitoSugar with ArgumentMatchersSugar {

  trait BaseApmServiceMock extends ApiDataTestData {
    def aMock: ApmService

    def returnsData() = {
      when(aMock.fetchAllApis()(*)).thenReturn(Future.successful(EnvironmentDefinitions(List(defaultApiDefinition), List(defaultApiDefinition))))
    }

    def returnsSingleApi(serviceName: ServiceName) = {
      when(aMock.fetchApi(eqTo(serviceName))(*)).thenReturn(Future.successful(Some(Locator.Production(defaultApiDefinition))))
    }

    def returnsNoSingleApi(serviceName: ServiceName) = {
      when(aMock.fetchApi(eqTo(serviceName))(*)).thenReturn(Future.successful(None))
    }
  }

  object ApmServiceMock extends BaseApmServiceMock {

    val aMock = mock[ApmService]
  }
}
