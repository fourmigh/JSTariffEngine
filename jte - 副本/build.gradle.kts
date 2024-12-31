plugins {
    kotlin("jvm")
}

group = "org.caojun.library.jte"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    api(kotlin("stdlib"))
    api(libs.google.gson)
}

//tasks.test {
//    useJUnitPlatform()
//}