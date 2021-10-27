plugins {
    kotlin("jvm") version Kotlin.version
    kotlin("plugin.allopen") version Kotlin.version

    id(Shadow.pluginId) version Shadow.version

    application
}

repositories {
    jcenter()
    maven { url = uri("https://jitpack.io") }
    mavenCentral()
}

dependencies {
    implementation(DittNAV.Common.logging)
    implementation(DittNAV.Common.utils)
    implementation(DittNAV.Common.influx)
    implementation(DittNAV.Common.evictingCache)
    implementation("com.github.navikt.tms-ktor-token-support:token-support-idporten:2021.10.27-test-proxy")
    implementation(Jackson.dataTypeJsr310)
    implementation(Jackson.moduleKotlin)
    implementation(Kotlinx.coroutines)
    implementation(Kotlinx.htmlJvm)
    implementation(Ktor.clientApache)
    implementation(Ktor.clientJackson)
    implementation(Ktor.clientJson)
    implementation(Ktor.clientLogging)
    implementation(Ktor.clientLoggingJvm)
    implementation(Ktor.clientSerializationJvm)
    implementation(Ktor.htmlBuilder)
    implementation(Ktor.jackson)
    implementation(Ktor.serverNetty)
    implementation(Logback.classic)
    implementation(Logstash.logbackEncoder)
    implementation(NAV.tokenValidatorKtor)
    implementation(Prometheus.common)
    implementation(Prometheus.hotspot)
    implementation(Prometheus.logback)
    implementation(NAV.customKtorCorsFeature)
    testImplementation(Junit.api)
    testImplementation(Junit.engine)
    testImplementation(Kluent.kluent)
    testImplementation(Ktor.clientMock)
    testImplementation(Ktor.clientMockJvm)
    testImplementation(Mockk.mockk)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

tasks {

    withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    register("runServer", JavaExec::class) {
        environment("OIDC_ISSUER", "http://localhost:9000")
        environment("LOGINSERVICE_IDPORTEN_DISCOVERY_URL", "http://localhost:9000/.well-known/openid-configuration")
        environment("LOGINSERVICE_IDPORTEN_AUDIENCE", "stubOidcClient")
        environment("OPEN_AM_REST_SERVICE_URL", "http://localhost:8095/esso")
        environment("SECURITY_TOKEN_SERVICE_URL", "http://localhost:8095/security-token-service-token")
        environment("STS_API_GW_KEY", "stsKey")
        environment("PDL_API_URL", "http://localhost:8095/pdl-api")
        environment("PDL_API_GW_KEY", "pdlKey")
        environment("SERVICEUSER_USERNAME", "username")
        environment("SERVICEUSER_PASSWORD", "password")
        environment("NAIS_CLUSTER_NAME", "dev-sbs")
        environment("NAIS_NAMESPACE", "local")
        environment("SENSU_HOST", "stub")
        environment("SENSU_PORT", "0")
        environment("STS_CACHE_ENABLED", "false")
        environment("STS_CACHE_EXPIRY_MARGIN_MINUTES", "5")
        environment("CORS_ALLOWED_HOST", "*")
        environment("OIDC_CLAIM_CONTAINING_THE_IDENTITY", "pid")
        environment("IDPORTEN_WELL_KNOWN_URL", "http://localhost:9000/.well-known/openid-configuration")
        environment("IDPORTEN_CLIENT_ID", "fdsagdsagre2332t4g")
        environment(
            "IDPORTEN_CLIENT_JWK",
            """{
  "use": "sig",
  "kty": "RSA",
  "kid": "jXDxKRE6a4jogcc4HgkDq3uVgQ0",
  "alg": "RS256",
  "n": "xQ3chFsz...",
  "e": "AQAB",
  "d": "C0BVXQFQ...",
  "p": "9TGEF_Vk...",
  "q": "zb0yTkgqO...",
  "dp": "7YcKcCtJ...",
  "dq": "sXxLHp9A...",
  "qi": "QCW5VQjO..."
}"""
        )
        environment("IDPORTEN_REDIRECT_URI", "www.nav.no")

        main = application.mainClassName
        classpath = sourceSets["main"].runtimeClasspath
    }
}

apply(plugin = Shadow.pluginId)
