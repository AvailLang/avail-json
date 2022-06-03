/*
 * build.gradle.kts
 * Copyright © 1993-2022, The Avail Foundation, LLC.
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
plugins {
    java
    kotlin("jvm") version "1.6.20"
    `maven-publish`
    publishing
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
version = "1.0.7"
description = "A flexible JSON building and reading utility"

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
val githubUsername: String get() =
    System.getenv("GITHUB_USER") ?: ""
val githubPassword: String get() =
    System.getenv("GITHUB_TOKEN") ?: ""


private val credentialsWarning =
    "Missing credentials.  To publish, you'll need to create a GitHub " +
        "token:\n" +
        (
            "https://help.github.com/en/actions/" +
                "configuring-and-managing-workflows/" +
                "authenticating-with-the-github_token") +
        "\n" +
        "Then set GITHUB_USER and GITHUB_TOKEN variables to hold your github " +
        "username and the (generated hexadecimal) token, respectively, in " +
        "~/.bash_profile or other appropriate login script." +
        "\n" +
        "Remember to restart IntelliJ after this change."

/**
 * Check that the publish task has access to the necessary credentials.
 */
fun checkCredentials ()
{
    if (githubUsername.isEmpty() || githubPassword.isEmpty())
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

    val sourceJar by creating(Jar::class) {
        description = "Creates sources JAR."
        dependsOn(JavaPlugin.CLASSES_TASK_NAME)
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
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

    artifacts { add("archives", sourceJar) }
    publish { checkCredentials() }
}

// TODO Update publish details when project is independent.
publishing {
    repositories {
        maven {
            name = "GitHub"
            url = uri("") // TODO
            credentials {
                username = githubUsername
                password = githubPassword
            }
        }
    }

    publications {
        create<MavenPublication>("avail-json") {
            val sourceJar = tasks.getByName("sourceJar") as Jar
            from(components["java"])
            artifact(sourceJar)
        }
    }
}
