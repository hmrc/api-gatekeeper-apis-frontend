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

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}

import uk.gov.hmrc.apigatekeeperapisfrontend.controllers.actions.GatekeeperRoleActions
import uk.gov.hmrc.apigatekeeperapisfrontend.services.ApmService
import uk.gov.hmrc.apigatekeeperapisfrontend.views.html._
import uk.gov.hmrc.apiplatform.modules.apis.domain.models._
import uk.gov.hmrc.apiplatform.modules.gkauth.controllers.GatekeeperBaseController
import uk.gov.hmrc.apiplatform.modules.gkauth.services.{LdapAuthorisationService, StrideAuthorisationService}

@Singleton
class ApiDetailsController @Inject() (
    mcc: MessagesControllerComponents,
    strideAuthorisationService: StrideAuthorisationService,
    val ldapAuthorisationService: LdapAuthorisationService,
    apiDetailsPage: ApiDetailsPage,
    apiEventsPage: ApiEventsPage,
    errorTemplate: ErrorTemplate,
    apmService: ApmService
  )(implicit override val ec: ExecutionContext
  ) extends GatekeeperBaseController(strideAuthorisationService, mcc) with GatekeeperRoleActions {

  def page(serviceName: ServiceName): Action[AnyContent] = loggedInOnly() { implicit request =>
    apmService
      .fetchApi(serviceName)
      .map {
        case Some(defs) => Ok(apiDetailsPage(defs))
        case None       => NotFound(errorTemplate("API Not Found", "API Not Found", s"The api ${serviceName} hasn't been found in either environment"))
      }
  }

  def events(serviceName: ServiceName): Action[AnyContent] = loggedInOnly() { implicit request =>
    apmService
      .fetchApi(serviceName)
      .flatMap {
        case Some(api) =>
          val definition = api.production.orElse(api.sandbox).get
          apmService.fetchApiEvents(serviceName).map(events => Ok(apiEventsPage(definition.name, definition.serviceName, events)))

        case None => Future.successful(NotFound(errorTemplate("API Not Found", "API Not Found", s"The api ${serviceName} hasn't been found")))
      }
  }
}
