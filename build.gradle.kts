val prometheusVersion = "0.8.1"
val ktorVersion = "1.3.2"
val logstashVersion = 5.2
val logbackVersion = "1.2.3"
val kotlinVersion = "1.3.50"
val jacksonVersion = "2.9.9"
val jacksonKotlinModuleVersion = "2.9.10"
val spekVersion = "2.0.6"
val mockKVersion = "1.9.3"
val assertJVersion = "3.12.2"
val junitVersion = "5.4.1"
val kluentVersion = "1.56"
val tokensupportVersion = "1.3.0"
val kotlinxCoroutinesVersion = "1.3.3"
val kotlinxHtmlVersion = "0.6.12"
val jjwtVersion = "0.11.0"
val bcproVersion = "1.64"
val dittnavLibVersion = "0.3.0"


plugins {
    val kotlinVersion = "1.3.70"

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
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    compile("no.nav.security:token-validation-ktor:$tokensupportVersion")
    compile("io.prometheus:simpleclient_common:$prometheusVersion")
    compile("io.prometheus:simpleclient_hotspot:$prometheusVersion")
    compile("io.prometheus:simpleclient_logback:$prometheusVersion")
    compile("io.ktor:ktor-server-netty:$ktorVersion")
    compile("io.ktor:ktor-client-apache:$ktorVersion")
    compile("io.ktor:ktor-client-json:$ktorVersion")
    compile("io.ktor:ktor-client-serialization-jvm:$ktorVersion")
    compile("io.ktor:ktor-jackson:$ktorVersion")
    compile("io.ktor:ktor-client-jackson:$ktorVersion")
    compile("io.ktor:ktor-html-builder:$ktorVersion")
    compile("io.ktor:ktor-client-logging:$ktorVersion")
    compile("io.ktor:ktor-client-logging-jvm:$ktorVersion")
    compile("ch.qos.logback:logback-classic:$logbackVersion")
    compile("net.logstash.logback:logstash-logback-encoder:$logstashVersion")
    compile("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonKotlinModuleVersion")
    compile("org.jetbrains.kotlinx:kotlinx-html-jvm:${kotlinxHtmlVersion}")
    compile("com.github.navikt.dittnav-common-lib:dittnav-common-logging:$dittnavLibVersion")
    compile("com.github.navikt.dittnav-common-lib:dittnav-common-utils:$dittnavLibVersion")
    compile("com.github.navikt.dittnav-common-lib:dittnav-common-metrics:$dittnavLibVersion")
    testCompile("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testCompile(kotlin("test-junit5"))
    testCompile("io.ktor:ktor-client-mock:$ktorVersion")
    testCompile("io.ktor:ktor-client-mock-jvm:$ktorVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    testImplementation("org.amshove.kluent:kluent:$kluentVersion")
    testImplementation("io.mockk:mockk:$mockKVersion")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
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

        main = application.mainClassName
        classpath = sourceSets["main"].runtimeClasspath
    }
}