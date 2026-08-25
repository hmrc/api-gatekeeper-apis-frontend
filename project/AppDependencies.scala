import sbt.*

object AppDependencies {

  private val bootstrapVersion    = "10.8.0"

  private val commonDomainVersion = "1.4.0"
  private val apiDomainVersion    = "1.8.0"
  private val appDomainVersion    = "1.6.0"
  private val playfrontendVersion = "13.11.0"


  val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30"      % bootstrapVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30"      % playfrontendVersion,
    "uk.gov.hmrc"       %% "internal-auth-client-play-30"    % "4.4.0",
    "org.apache.commons" % "commons-csv"                     % "1.14.1",
    "uk.gov.hmrc"       %% "api-platform-common-domain"      % commonDomainVersion,
    "uk.gov.hmrc"       %% "api-platform-api-domain"         % apiDomainVersion,
    "uk.gov.hmrc"       %% "api-platform-application-domain" % appDomainVersion
  )

  val test = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30"                   % bootstrapVersion,
    "org.jsoup"    % "jsoup"                                    % "1.21.1",
    "uk.gov.hmrc" %% "api-platform-application-domain-fixtures" % appDomainVersion,
    "uk.gov.hmrc" %% "api-platform-common-domain-fixtures"      % commonDomainVersion
  ).map(_ % "test")
}
