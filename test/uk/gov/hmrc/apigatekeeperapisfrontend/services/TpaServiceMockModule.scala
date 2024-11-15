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

import uk.gov.hmrc.apigatekeeperapisfrontend.models.TempApplication
import uk.gov.hmrc.apigatekeeperapisfrontend.utils.ApiDataTestData
import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.RateLimitTier.{BRONZE, SILVER}
import uk.gov.hmrc.apiplatform.modules.common.domain.models.ApiContext

trait TpaServiceMockModule extends MockitoSugar with ArgumentMatchersSugar {

  trait BaseTpaServiceMock {
    def aMock: ThirdPartyApplicationService

    object FetchAllApplications extends ApiDataTestData {

      def returnsData() = {
        when(aMock.fetchAllApplications(*[ApiContext])(*)).thenReturn(Future.successful(List(TempApplication(BRONZE), TempApplication(SILVER))))
      }

    }
  }

  object TpaServiceMock extends BaseTpaServiceMock {

    val aMock = mock[ThirdPartyApplicationService]
  }
}
