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

import uk.gov.hmrc.apigatekeeperapisfrontend.connectors.ApmConnectorMockModule
import uk.gov.hmrc.apigatekeeperapisfrontend.utils.{ApiDataTestData, AsyncHmrcSpec}
import uk.gov.hmrc.apiplatform.modules.common.domain.models.{ApiContext, Environment}
import uk.gov.hmrc.http.HeaderCarrier

class ApmServiceSpec extends AsyncHmrcSpec {

  trait Setup extends ApmConnectorMockModule with ApiDataTestData {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    val service                    = new ApmService(ApmConnectorMock.aMock)
  }
  "Fetch All Apis" should {
    "use sandbox if only sandbox returned" in new Setup {
      ApmConnectorMock.returnsData(Environment.SANDBOX)
      ApmConnectorMock.returnsNoData(Environment.PRODUCTION)
      val result = await(service.fetchAllApis())
      result.size shouldBe 1
      result.get(defaultContext) shouldBe anApiDataMap().get(defaultContext)
    }

    "use production if only production returned" in new Setup {
      ApmConnectorMock.returnsNoData(Environment.SANDBOX)
      ApmConnectorMock.returnsData(Environment.PRODUCTION)
      val result = await(service.fetchAllApis())
      result.size shouldBe 1
      result.get(defaultContext) shouldBe anApiDataMap().get(defaultContext)
    }

    "merge them when both are returned" in new Setup {
      ApmConnectorMock.returnsData(Environment.SANDBOX, anApiDataMap().concat(Map(ApiContext("sandbox") -> defaultData.copy(context = ApiContext("sandbox")))))
      ApmConnectorMock.returnsData(Environment.PRODUCTION, anApiDataMap().concat(Map(ApiContext("production") -> defaultData.copy(context = ApiContext("production")))))
      val result = await(service.fetchAllApis())
      result.size shouldBe 3
      result.get(defaultContext) shouldBe anApiDataMap().get(defaultContext)
    }
  }
}
