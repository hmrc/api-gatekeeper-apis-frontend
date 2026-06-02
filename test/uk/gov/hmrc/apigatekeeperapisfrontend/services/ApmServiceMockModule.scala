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

import org.mockito.ArgumentMatchers.{any as `*`, eq as eqTo}
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar

import uk.gov.hmrc.apigatekeeperapisfrontend.models.EnvironmentDefinitions
import uk.gov.hmrc.apigatekeeperapisfrontend.utils.ApiDataTestData
import uk.gov.hmrc.apiplatform.modules.apis.domain.models.*

trait ApmServiceMockModule extends MockitoSugar {

  trait BaseApmServiceMock extends ApiDataTestData {
    def aMock: ApmService

    object FetchAllApis {

      def returnsData() = {
        when(aMock.fetchAllApis()(using *)).thenReturn(Future.successful(EnvironmentDefinitions(List(defaultApiDefinition), List(defaultApiDefinition))))
      }
    }

    object FetchApi {

      def returnsSingleApi(serviceName: ServiceName) = {
        when(aMock.fetchApi(eqTo(serviceName))(using *)).thenReturn(Future.successful(Some(Locator.Production(defaultApiDefinition))))
      }

      def returnsNoSingleApi(serviceName: ServiceName) = {
        when(aMock.fetchApi(eqTo(serviceName))(using *)).thenReturn(Future.successful(None))
      }
    }

    object FetchApiEvents {

      def returnsEvent(serviceName: ServiceName, includeNoChange: Boolean = true) = {
        when(aMock.fetchApiEvents(eqTo(serviceName), eqTo(includeNoChange))(using *)).thenReturn(Future.successful(List(defaultEvent)))
      }

      def returnsNoEvents(serviceName: ServiceName) = {
        when(aMock.fetchApiEvents(eqTo(serviceName), eqTo(true))(using *)).thenReturn(Future.successful(List.empty))
      }
    }

  }

  object ApmServiceMock extends BaseApmServiceMock {

    val aMock = mock[ApmService]
  }
}
