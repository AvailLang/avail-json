/*
 * build.gradle.kts
 * Copyright © 1993-2021, The Avail Foundation, LLC.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of the contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toUpperCaseAsciiOnly

plugins {
    java
    kotlin("jvm") version "1.6.20"
    `maven-publish`
    publishing
    signing
}

val junitVersion = "5.8.2"
val jvmTarget = 11
val jvmTargetString = jvmTarget.toString()
val kotlinLanguage = "1.6"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(jvmTarget))
    }
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(
            JavaLanguageVersion.of(jvmTarget))
    }
}

group = "org.availlang"
version = "1.0.7-SNAPSHOT"
description = "A flexible JSON building and reading utility"

val isReleaseVersion =
    !version.toString().toUpperCaseAsciiOnly().endsWith("SNAPSHOT")

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}


///////////////////////////////////////////////////////////////////////////////
//                       Publish Utilities
///////////////////////////////////////////////////////////////////////////////
val ossrhUsername: String get() =
    System.getenv("OSSRH_USER") ?: ""
val ossrhPassword: String get() =
    System.getenv("OSSRH_PASSWORD") ?: ""

private val credentialsWarning =
    "Missing OSSRH credentials.  To publish, you'll need to create an OSSHR " +
        "JIRA account. Then ensure the user name, and password are available " +
        "as the environment variables: 'OSSRH_USER' and 'OSSRH_PASSWORD'"

/**
 * Check that the publish task has access to the necessary credentials.
 */
fun checkCredentials ()
{
    if (ossrhUsername.isEmpty() || ossrhPassword.isEmpty())
    {
        System.err.println(credentialsWarning)
    }
}

///////////////////////////////////////////////////////////////////////////////

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = jvmTargetString
        targetCompatibility = jvmTargetString
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = jvmTargetString
            freeCompilerArgs = listOf("-Xjvm-default=all-compatibility")
            languageVersion = kotlinLanguage
        }
    }
    withType<Test> {
        val toolChains =
            project.extensions.getByType(JavaToolchainService::class)
        javaLauncher.set(
            toolChains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(jvmTarget))
            })
        testLogging {
            events = setOf(org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED)
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }
    }

    val sourceJar by creating(Jar::class)
    {
        description = "Creates sources JAR."
        dependsOn(JavaPlugin.CLASSES_TASK_NAME)
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }

    val javadocJar by creating(Jar::class)
    {
        description = "Creates Javadoc JAR."
        dependsOn(JavaPlugin.CLASSES_TASK_NAME)
        archiveClassifier.set("javadoc")
        from(javadoc)
    }

    jar {
        manifest.attributes["Implementation-Version"] =
            project.version
        doFirst {
            delete(fileTree("$buildDir/libs").matching {
                include("**/*.jar")
                exclude("**/*-all.jar")
            })
        }
    }

    artifacts {
        add("archives", sourceJar)
        add("archives", javadocJar)
    }
    publish { checkCredentials() }
}

signing {
    // TODO make this work!
    // TODO https://docs.gradle.org/current/userguide/signing_plugin.html
    // TODO https://central.sonatype.org/publish/requirements/#sign-files-with-gpgpgp
    val signingKeyId: String? by project
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    sign(tasks["stuffZip"])
}

publishing {
    repositories {
        maven {
            url = if (isReleaseVersion)
            {
                // Release version
                uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            }
            else
            {
                // Snapshot
                uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            }
            credentials {
                username = System.getenv("OSSRH_USER")
                password = System.getenv("OSSRH_PASSWORD")
            }
        }
    }

    publications {

        create<MavenPublication>("avail-json") {
            pom {
                groupId = project.group.toString()
                name.set("Avail JSON")
                packaging = "jar"
                description.set("Read and write freeform JSON with precise error checking.")
                url.set("https://www.availlang.org/")
                licenses {
                    license {
                        name.set("BSD 3-Clause \"New\" or \"Revised\" License")
                        url.set("https://github.com/AvailLang/avail-json/blob/main/LICENSE")
                    }
                }
                scm {
                    connection.set("scm:git:git@github.com:AvailLang/avail-json.git")
                    developerConnection.set("scm:git:git@github.com:AvailLang/avail-json.git")
                    url.set("https://github.com/AvailLang/avail-json")
                }
                developers {
                    developer {
                        id.set("toddATAvail")
                        name.set("Todd Smith")
                    }
                    developer {
                        id.set("richATAvail")
                        name.set("Richard Arriaga")
                    }
                    developer {
                        id.set("leslieATAvail")
                        name.set("Leslie Schultz")
                    }
                    developer {
                        id.set("markATAvail")
                        name.set("Mark van Gulik")
                    }
                }
            }
            val sourceJar = tasks.getByName("sourceJar") as Jar
            val javadocJar = tasks.getByName("javadocJar") as Jar
            from(components["java"])
            artifact(sourceJar)
            artifact(javadocJar)
        }
    }
}
