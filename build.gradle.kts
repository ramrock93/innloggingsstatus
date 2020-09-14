val jacksonKotlinModuleVersion = "2.9.9"
val dittnavLibVersion = "0.3.0"


plugins {
    val kotlinVersion = "1.3.71"

    kotlin("jvm").version(kotlinVersion)
    kotlin("plugin.allopen").version(kotlinVersion)

    application
}

repositories {
    jcenter()
    maven { url = uri("https://jitpack.io") }
    mavenCentral()
}

dependencies {
    implementation(Kotlinx.coroutines)
    implementation(NAV.tokenValidatorKtor)
    implementation(Prometheus.common)
    implementation(Prometheus.hotspot)
    implementation(Prometheus.logback)
    implementation(Ktor.serverNetty)
    implementation(Ktor.clientApache)
    implementation(Ktor.clientJson)
    implementation(Ktor.clientSerializationJvm)
    implementation(Ktor.jackson)
    implementation(Ktor.clientJackson)
    implementation(Ktor.htmlBuilder)
    implementation(Ktor.clientLogging)
    implementation(Ktor.clientLoggingJvm)
    implementation(Logback.classic)
    implementation(Logstash.logbackEncoder)
    implementation(Jackson.dataTypeJsr310)
    implementation(Jackson.moduleKotlin)
    implementation(Kotlinx.htmlJvm)
    implementation("com.github.navikt.dittnav-common-lib:dittnav-common-logging:$dittnavLibVersion")
    implementation("com.github.navikt.dittnav-common-lib:dittnav-common-utils:$dittnavLibVersion")
    implementation("com.github.navikt.dittnav-common-lib:dittnav-common-metrics:$dittnavLibVersion")
    testImplementation(Junit.api)
    testImplementation(Junit.engine)
    testImplementation(Ktor.clientMock)
    testImplementation(Ktor.clientMockJvm)
    testImplementation(Kluent.kluent)
    testImplementation(Mockk.mockk)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "13"
}

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    withType<Jar> {
        manifest {
            attributes["Main-Class"] = application.mainClassName
        }
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    }

    withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    register("runServer", JavaExec::class) {
        environment("OIDC_ISSUER", "http://localhost:9000")
        environment("OIDC_DISCOVERY_URL", "http://localhost:9000/.well-known/openid-configuration")
        environment("OIDC_ACCEPTED_AUDIENCE", "stubOidcClient")
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
        environment("SENSU_PORT", "")
        environment("STS_CACHE_ENABLED", "false")
        environment("STS_CACHE_EXPIRY_MARGIN_MINUTES", "5")
        environment("CORS_ALLOWED_ORIGINS", "localhost:9002")

        main = application.mainClassName
        classpath = sourceSets["main"].runtimeClasspath
    }
}