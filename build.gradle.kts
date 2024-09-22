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

//    testImplementation("io.hosuaby:inject-resources-core:1.0.0")
//    testImplementation("io.hosuaby:inject-resources-junit-jupiter:1.0.0")

    runtimeOnly("ch.qos.logback:logback-classic:1.5.8")
}


application {
    mainClass = "io.clusterless.subpop.Main"
}
java {
    sourceCompatibility = JavaVersion.toVersion("17")
    targetCompatibility = JavaVersion.toVersion("17")
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


