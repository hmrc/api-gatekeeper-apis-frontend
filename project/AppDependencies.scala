import sbt.*

object AppDependencies {

  private val bootstrapVersion = "9.11.0"
  val apiDomainVersion         = "0.19.1"
  val commonDomainVersion      = "0.18.0"
  val appDomainVersion         = "0.75.0"

  val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30"      % bootstrapVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30"      % "11.12.0",
    "uk.gov.hmrc"       %% "internal-auth-client-play-30"    % "3.1.0",
    "org.apache.commons" % "commons-csv"                     % "1.10.0",
    "uk.gov.hmrc"       %% "api-platform-api-domain"         % apiDomainVersion,
    "uk.gov.hmrc"       %% "api-platform-application-domain" % appDomainVersion
  )

  val test = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30"                   % bootstrapVersion,
    "org.jsoup"    % "jsoup"                                    % "1.17.2",
    "uk.gov.hmrc" %% "api-platform-application-domain-fixtures" % appDomainVersion
  ).map(_ % "test")
}
