import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    application
    idea
    java
    war
}

repositories {
    mavenCentral()
    mavenLocal()
    jcenter()
}

tasks {
    withType<KotlinCompile> {
        sourceCompatibility = JavaVersion.VERSION_1_8.name
        targetCompatibility = JavaVersion.VERSION_1_8.name
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.apiVersion = "1.3"
        kotlinOptions.languageVersion = "1.3"
        kotlinOptions.javaParameters = true
        kotlinOptions.suppressWarnings = true
    }

    withType<Test> {
        useJUnitPlatform {
            includeEngines = setOf("spek2")
        }
    }

    withType<CreateStartScripts> {
        classpath?.plus(files("./../config"))
        doLast {
            unixScript.writeText(unixScript.readText()
                    .replace("\$APP_HOME/lib/config", "\$APP_HOME/config"))
            windowsScript.writeText(windowsScript.readText()
                    .replace("%APP_HOME%\\lib\\config", "%APP_HOME%\\config"))
        }
    }

    withType<War> {
        dependsOn(":stratix-demo-ui:buildAngular")
        from("../stratix-demo-ui/build/webapp") { include("**/*") }
        from("src/main/webapp") { include("**/*") }
    }

    withType<Tar> {
        dependsOn(":stratix-demo-ui:buildAngular")
        compression = Compression.GZIP
    }

    withType<Zip> {
        enabled = false
    }

    // Warning: This definition has to be after the Zip otherwise Zip will disable this as well
    withType<Jar> {
        enabled = true
    }

    withType<JavaExec> {
        dependsOn(":stratix-demo-ui:buildAngular")
    }
}

dependencies {
    implementation("io.tekniq:tekniq-sparklin:${properties["tekniqVersion"]}")
    implementation("javax.mail:mail:1.4.7")
    implementation("javax.mail", "mail", "1.4.7")
    implementation("org.litote.kmongo:kmongo:${properties["kmongoVersion"]}")
    runtime("ch.qos.logback:logback-classic:1.2.3")
    providedRuntime("javax.servlet:javax.servlet-api:3.1.0")

    testImplementation(kotlin("test"))
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:${properties["spekVersion"]}")
    testImplementation("org.spekframework.spek2:spek-runner-junit5:${properties["spekVersion"]}")
}

idea {
    module {
        inheritOutputDirs = true
        outputDir = file("build/classes/kotlin/main")
        testOutputDir = file("build/classes/kotlin/test")
    }
}

application {
    mainClassName = "com.stratix.demo.WebAppKt"
    applicationDefaultJvmArgs = listOf("-Xms128m", "-Xmx256m")
    applicationDistribution.also { copy ->
        copy.from("../stratix-demo-ui/build/webapp") { into("webapp") }
        copy.from("src/main/resources/config.properties") {
            into("config")
        }
    }
}

