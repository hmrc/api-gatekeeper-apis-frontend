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

package uk.gov.hmrc.apigatekeeperapisfrontend.config

import com.google.inject.AbstractModule

import uk.gov.hmrc.apigatekeeperapisfrontend.connectors.{ApmConnector, ThirdPartyApplicationConnector}
import uk.gov.hmrc.apigatekeeperapisfrontend.controllers.HandleForbiddenWithView
import uk.gov.hmrc.apiplatform.modules.gkauth.controllers.actions.ForbiddenHandler

class Module extends AbstractModule {

  override def configure(): Unit = {

    bind(classOf[ForbiddenHandler]).to(classOf[HandleForbiddenWithView])
    bind(classOf[GatekeeperConfig]).toProvider(classOf[GatekeeperConfigProvider])
    bind(classOf[ApmConnector.Config]).toProvider(classOf[LiveApmConnectorConfigProvider])
    bind(classOf[ThirdPartyApplicationConnector.Config]).toProvider(classOf[TPAConnectorConfigProvider])
    bind(classOf[AppConfig]).asEagerSingleton()
  }
}
