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

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, stubFor, urlEqualTo}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import play.api.http.Status.OK
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{Json, OFormat}
import play.api.{Application, Configuration, Mode}
import uk.gov.hmrc.http.HeaderCarrier

import uk.gov.hmrc.apigatekeeperapisfrontend.models.DisplayApiEvent
import uk.gov.hmrc.apigatekeeperapisfrontend.utils.ApiDataTestData
import uk.gov.hmrc.apiplatform.modules.apis.domain.models._
import uk.gov.hmrc.apiplatform.modules.common.domain.models.Environment

class ApmConnectorISpec extends BaseConnectorIntegrationSpec with GuiceOneAppPerSuite with ApiDataTestData {

  private val stubConfig = Configuration(
    "microservice.services.api-platform-microservice.port" -> stubPort,
    "metrics.enabled"                                      -> "false"
  )

  override def fakeApplication(): Application = GuiceApplicationBuilder()
    .configure(stubConfig)
    .in(Mode.Test)
    .build()

  trait Setup {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val connector = app.injector.instanceOf[ApmConnector]
  }

  "fetch api list" should {
    val url = "/api-definitions/all?environment=SANDBOX"
    "fetch all apis" in new Setup {
      stubFor(WireMock.get(urlEqualTo(url))
        .willReturn(aResponse()
          .withStatus(OK)
          .withBody(Json.toJson(List(defaultApiDefinition).map(d => d.context -> d).toMap).toString())))

      val result = await(connector.fetchAllApis(Environment.SANDBOX))

      result.find(_.context == defaultContext).value shouldBe defaultApiDefinition
    }
  }

  "fetch api" should {
    val url = "/api-definitions/service-name/fish"
    "fetch all apis" in new Setup {
      implicit val format: OFormat[Locator[ApiDefinition]] = Locator.buildLocatorFormatter[ApiDefinition]
      stubFor(WireMock.get(urlEqualTo(url))
        .willReturn(aResponse()
          .withStatus(OK)
          .withBody(Json.toJson[Locator[ApiDefinition]](Locator.Production(defaultApiDefinition)).toString)))

      val result = await(connector.fetchApi(ServiceName("fish")))

      result.value shouldBe Locator.Production(defaultApiDefinition)
    }
  }

  "fetch api events" should {
    val url = "/api-definitions/service-name/fish/events"
    "fetch apis events" in new Setup {
      stubFor(WireMock.get(urlEqualTo(url))
        .willReturn(aResponse()
          .withStatus(OK)
          .withBody(Json.toJson[List[DisplayApiEvent]](List(defaultEvent)).toString())))

      val result = await(connector.fetchApiEvents(ServiceName("fish")))

      result shouldBe List(defaultEvent)
    }
  }
}
