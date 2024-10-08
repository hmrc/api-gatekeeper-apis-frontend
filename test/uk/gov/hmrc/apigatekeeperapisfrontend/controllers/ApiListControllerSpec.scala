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

package uk.gov.hmrc.apigatekeeperapisfrontend.controllers

import scala.concurrent.ExecutionContext.Implicits.global

import org.mockito.{ArgumentMatchersSugar, MockitoSugar}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.MessagesControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers._

import uk.gov.hmrc.apigatekeeperapisfrontend.services.ApmServiceMockModule
import uk.gov.hmrc.apigatekeeperapisfrontend.utils.AsyncHmrcSpec
import uk.gov.hmrc.apigatekeeperapisfrontend.views.html.ApiListPage
import uk.gov.hmrc.apiplatform.modules.gkauth.domain.models.GatekeeperRoles
import uk.gov.hmrc.apiplatform.modules.gkauth.services.{LdapAuthorisationServiceMockModule, StrideAuthorisationServiceMockModule}

class ApiListControllerSpec extends AsyncHmrcSpec with GuiceOneAppPerSuite {

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm"     -> false,
        "metrics.enabled" -> false
      )
      .build()

  trait Setup extends MockitoSugar with ArgumentMatchersSugar with StrideAuthorisationServiceMockModule with LdapAuthorisationServiceMockModule with ApmServiceMockModule {
    val page = app.injector.instanceOf[ApiListPage]
    val mcc  = app.injector.instanceOf[MessagesControllerComponents]

    val controller = new ApiListController(mcc, StrideAuthorisationServiceMock.aMock, LdapAuthorisationServiceMock.aMock, page, ApmServiceMock.aMock)
    ApmServiceMock.FetchAllApis.returnsData()
  }

  "GET /" should {
    val fakeRequest = FakeRequest("GET", "/")

    "return 200 with stride auth" in new Setup {
      StrideAuthorisationServiceMock.Auth.succeeds(GatekeeperRoles.USER)

      val result = controller.page(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return 200 with ldap auth" in new Setup {
      StrideAuthorisationServiceMock.Auth.sessionRecordNotFound()
      LdapAuthorisationServiceMock.Auth.succeeds
      val result = controller.page(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in new Setup {
      StrideAuthorisationServiceMock.Auth.succeeds(GatekeeperRoles.USER)

      val result = controller.page(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }

    "return 403 for InsufficientEnrolments" in new Setup {
      StrideAuthorisationServiceMock.Auth.hasInsufficientEnrolments()
      LdapAuthorisationServiceMock.Auth.notAuthorised
      val result = controller.page(fakeRequest)
      status(result) shouldBe Status.FORBIDDEN
    }

    "return 303 for SessionRecordNotFound" in new Setup {
      StrideAuthorisationServiceMock.Auth.sessionRecordNotFound()
      LdapAuthorisationServiceMock.Auth.notAuthorised
      val result = controller.page(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
    }
  }

  "GET /csv" should {
    val fakeRequest = FakeRequest("GET", "/csv")

    "return 200 with stride auth" in new Setup {
      StrideAuthorisationServiceMock.Auth.succeeds(GatekeeperRoles.USER)

      val result = controller.csv(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return 200 with ldap auth" in new Setup {
      StrideAuthorisationServiceMock.Auth.sessionRecordNotFound()
      LdapAuthorisationServiceMock.Auth.succeeds
      val result = controller.csv(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return CSV" in new Setup {
      StrideAuthorisationServiceMock.Auth.succeeds(GatekeeperRoles.USER)

      val result = controller.csv(fakeRequest)
      contentType(result) shouldBe Some("text/csv")
      contentAsString(result) shouldBe
        """name,serviceName,context,version,source,status,access,isTrial,environment,lastPublishedAt
          |Hello World,helloworld,test/hello,1.0,OAS,STABLE,Public,false,Sandbox,2022-10-12T19:00:00Z
          |Hello World,helloworld,test/hello,1.0,OAS,STABLE,Public,false,Production,2022-10-12T19:00:00Z
          |""".stripMargin
    }

    "return 403 for InsufficientEnrolments" in new Setup {
      StrideAuthorisationServiceMock.Auth.hasInsufficientEnrolments()
      LdapAuthorisationServiceMock.Auth.notAuthorised
      val result = controller.csv(fakeRequest)
      status(result) shouldBe Status.FORBIDDEN
    }

    "return 303 for SessionRecordNotFound" in new Setup {
      StrideAuthorisationServiceMock.Auth.sessionRecordNotFound()
      LdapAuthorisationServiceMock.Auth.notAuthorised
      val result = controller.csv(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
    }
  }
}
