/*
 * Copyright (c) 2024 Chris K Wensel <chris@wensel.net>. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.4.2"
}

version = "0.1"
group = "io.clusterless"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("info.picocli:picocli-codegen:4.7.6")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor:2.11.0")

    implementation("info.picocli:picocli:4.7.6")
    implementation("com.opencsv:opencsv:5.9")
    implementation("io.micronaut.picocli:micronaut-picocli:5.5.0")
    implementation("io.micronaut.serde:micronaut-serde-jackson:2.11.0")
    implementation("com.google.guava:guava:33.2.1-jre")
    implementation("org.barfuin.texttree:text-tree:2.1.2")
    implementation("org.slf4j:slf4j-api:2.0.13")

    runtimeOnly("ch.qos.logback:logback-classic:1.5.11")
}

application {
    mainClass = "io.clusterless.subpop.Main"
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<Test> {
    maxHeapSize = "2g" // Set the desired maximum heap size
}

micronaut {
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("io.clusterless.*")
    }
}

tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("dockerfileNative") {
    jdkVersion = "21"
}
