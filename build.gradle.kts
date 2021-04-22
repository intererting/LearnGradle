apply(from = "./learn.gradle.kts")
apply(from = "./work.gradle.kts")
apply(from = "./plugins.gradle.kts")
//class RepoPlugin : Plugin<Gradle> {
//    override fun apply(target: Gradle) {
//        target.allprojects {
//            repositories {
//            }
//        }
//    }
//}
//
//apply<RepoPlugin>()

plugins {
    kotlin("jvm") version "1.4.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("commons-io:commons-io:2.5")
    implementation("commons-codec:commons-codec:1.9")
}
