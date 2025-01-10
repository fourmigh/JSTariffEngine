plugins {
    kotlin("jvm") version "2.0.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "org.caojun.library.jte"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.google.code.gson:gson:2.10.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

tasks.shadowJar {
    archiveClassifier.set("all")
    dependencies {
        include(dependency("com.google.code.gson:gson"))
    }
    from(sourceSets.main.get().output)
}