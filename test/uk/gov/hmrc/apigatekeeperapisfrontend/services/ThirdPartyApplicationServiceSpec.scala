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

import scala.concurrent.ExecutionContext.Implicits.global

import uk.gov.hmrc.http.HeaderCarrier

import uk.gov.hmrc.apigatekeeperapisfrontend.connectors.TpaConnectorMockModule
import uk.gov.hmrc.apigatekeeperapisfrontend.utils.{ApiDataTestData, AsyncHmrcSpec}

class ThirdPartyApplicationServiceSpec extends AsyncHmrcSpec {

  trait Setup extends TpaConnectorMockModule with ApiDataTestData {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    val service                    = new ThirdPartyApplicationService(TpaConnectorMock.aMock)
  }

  "Fetch all Applications" should {
    "pass back the list it gets" in new Setup {
      TpaConnectorMock.FetchAllApplications.returnsData()
      val result = await(service.fetchAllApplications(defaultContext))
      result shouldBe List(standardApp)
    }
  }
}
