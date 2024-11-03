import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jreleaser.model.Active
import org.jreleaser.model.Distribution
import org.jreleaser.model.Stereotype
import java.io.FileInputStream
import java.util.*

/*
 * Copyright (c) 2024 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

plugins {
    id("com.gradleup.shadow") version "8.3.5"
    id("io.micronaut.application") version "4.4.3"
    id("org.jreleaser") version "1.15.0"
}

val versionProperties = Properties().apply {
    load(FileInputStream(File(rootProject.rootDir, "version.properties")))
}

val buildRelease = false
val buildNumber = System.getenv("GITHUB_RUN_NUMBER") ?: "dev"
val wipReleases = "wip-${buildNumber}"

version = if (buildRelease)
    "${versionProperties["release.major"]}-${versionProperties["release.minor"]}"
else "${versionProperties["release.major"]}-${wipReleases}"

group = "io.clusterless"

repositories {
    mavenCentral()

    maven {
        url = uri("https://maven.pkg.github.com/cwensel/*")
        name = "github"
        credentials(PasswordCredentials::class) {
            username = (project.findProperty("githubUsername") ?: System.getenv("USERNAME")) as? String
            password = (project.findProperty("githubPassword") ?: System.getenv("GITHUB_TOKEN")) as? String
        }
        content {
            includeVersionByRegex("net.wensel", "cascading-.*", ".*-wip-.*")
        }
    }
}

dependencies {
    annotationProcessor("info.picocli:picocli-codegen:4.7.6")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor:2.11.1")

    implementation("org.jetbrains:annotations:24.1.0")
    implementation("info.picocli:picocli:4.7.6")
    implementation("com.opencsv:opencsv:5.9")
    implementation("io.micronaut.picocli:micronaut-picocli:5.5.0")
    implementation("io.micronaut.serde:micronaut-serde-jackson:2.11.1")
    implementation("com.google.guava:guava:33.2.1-jre")
    implementation("org.barfuin.texttree:text-tree:2.1.2")

    // logging
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("ch.qos.logback:logback-classic:1.5.11")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

application {
    applicationName = "subpop"
    mainClass = "io.clusterless.subpop.Main"
}

distributions {
    main {
        distributionBaseName.set("subpop")
    }
}

tasks.withType<ShadowJar>{
    archiveBaseName.set("subpop")
}

tasks.named<Zip>("shadowDistZip") {
    archiveBaseName.set("subpop")
}

tasks.named<Tar>("shadowDistTar") {
    archiveBaseName.set("subpop")
}



tasks.withType<Test> {
    useJUnitPlatform()
    maxHeapSize = "5g" // Set the desired maximum heap size
}

tasks.named<ProcessResources>("processResources") {
    doFirst {
        file(rootProject.layout.buildDirectory.file("resources/main/version.properties"))
            .writeText("release.full=${version}")
    }
}

micronaut {
    version.set("4.6.0")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("io.clusterless.*")
    }
}

tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("dockerfileNative") {
    jdkVersion = "21"
}

jreleaser {
    dryrun.set(false)

    project {
        description.set("SubPop is a command line utility for finding the differences between one or more tabular datasets.")
        authors.add("Chris K Wensel")
        copyright.set("Chris K Wensel")
        license.set("MPL-2.0")
        stereotype.set(Stereotype.CLI)
        links {
            homepage.set("https://github.com/ClusterlessHQ")
        }
        inceptionYear.set("2024")
        gitRootSearch.set(true)
    }

    signing {
        armored.set(true)
        active.set(Active.ALWAYS)
        verify.set(false)
    }

    release {
        github {
            overwrite.set(true)
            sign.set(false)
            repoOwner.set("ClusterlessHQ")
            name.set("subpop")
            username.set("cwensel")
            branch.set("wip-1.0")
            changelog.enabled.set(false)
            milestone.close.set(false)
        }
    }

    distributions {
        create("subpop") {
            distributionType.set(Distribution.DistributionType.JAVA_BINARY)
            executable {
                name.set("subpop")
            }
            artifact {
                path.set(file("build/distributions/{{distributionName}}-{{projectVersion}}.zip"))
            }
        }
    }

    packagers {
        brew {
            active.set(Active.ALWAYS)
            repository.active.set(Active.ALWAYS)
        }

        docker {
            active.set(Active.ALWAYS)

            repository {
                repoOwner.set("ClusterlessHQ")
                name.set("subpop-docker")
            }

            registries {
                create("DEFAULT") {
                    externalLogin.set(true)
                    repositoryName.set("clusterless")
                }
            }

            buildx {
                enabled.set(false)
                platform("linux/amd64")
                platform("linux/arm64")
            }

            imageName("{{owner}}/{{distributionName}}:{{projectVersion}}")

            if (buildRelease) {
                imageName("{{owner}}/{{distributionName}}:{{projectVersionMajor}}")
                imageName("{{owner}}/{{distributionName}}:{{projectVersionMajor}}.{{projectVersionMinor}}")
                imageName("{{owner}}/{{distributionName}}:latest")
            } else {
                imageName("{{owner}}/{{distributionName}}:latest-wip")
            }
        }
    }
}

tasks.named("distZip"){
    enabled = false
}

tasks.named("distTar"){
    enabled = false
}

tasks.named("jreleaserPackage") {
    dependsOn("shadowDistZip")
    dependsOn("shadowDistTar")
}

tasks.register("release") {
    dependsOn("jreleaserRelease")
    dependsOn("jreleaserPackage")
    dependsOn("jreleaserPublish")
}
