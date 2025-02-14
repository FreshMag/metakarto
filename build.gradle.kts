/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.antlr.kotlin)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotest.multiplatform)
    alias(libs.plugins.kotlin.qa)
    alias(libs.plugins.npm.publish)
    alias(libs.plugins.multiJvmTesting)
    alias(libs.plugins.taskTree)
    alias(libs.plugins.mavenPublish)
}

group = "placeholder-group"

repositories {
    google()
    mavenCentral()
}

multiJvm {
    jvmVersionForCompilation.set(21)
}

kotlin {
    jvmToolchain(21)

    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget = JvmTarget.JVM_1_8
                }
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {}
        }

        commonTest.dependencies {
            implementation(libs.bundles.kotlin.testing.common)
            implementation(libs.bundles.kotest.common)
        }

        jvmTest.dependencies {
            implementation(libs.kotest.runner.junit5)
        }
    }

    js(IR) {
        moduleName = "placeholder-module_name"
        browser()
        nodejs()
        binaries.library()
    }

    applyDefaultHierarchyTemplate()

    targets.all {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    allWarningsAsErrors = true
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }
            }
        }
    }
}

ktlint {
    filter {
        exclude("**/generated/**")
    }
}

publishing {
    repositories {
        maven {
            name = "githubPackages"
            url = uri("https://maven.pkg.github.com/placeholder-author/placeholder-repo")
            credentials(PasswordCredentials::class)
        }
    }
}

mavenPublishing {
    pom {
        name.set("placeholder-project_name")
        description.set("placeholder-description")
        inceptionYear.set("placeholder-year")
        url.set("https://github.com/placeholder-author/placeholder-repo")

        licenses {
            license {
                name.set("placeholder-license")
                url.set("placeholder-license_url")
            }
        }

        // Specify developer information
        developers {
            developer {
                id.set("placeholder-author")
                name.set("placeholder-author_name")
                email.set("<<DEVELOPER_EMAIL>>")
            }
        }

        // Specify SCM information
        scm {
            url.set("https://github.com/<<AUTHOR>>/<<REPO>>")
        }
    }
    // Enable GPG signing for all publications
    signAllPublications()

    if (System.getenv("CI") == "true") {
        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = false)
    }
}

detekt {
    config.from(".detekt.yml")
    buildUponDefaultConfig = true
    parallel = true
}

npmPublish {
    packages {
        named("js") {
            packageName = "<<PACKAGE_NAME>>"
        }
    }

    registries {
        register("npmjs") {
            uri.set("https://registry.npmjs.org")
            if (System.getenv("CI") == "true") {
                authToken.set(System.getenv("NPM_TOKEN"))
            } else {
                val npmToken: String? by project
                authToken.set(npmToken)
                dry.set(npmToken.isNullOrBlank())
            }
        }
    }
}
