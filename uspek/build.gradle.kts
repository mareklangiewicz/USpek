import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.kotlin

plugins {
//    `build-scan`
    `maven-publish`
    kotlin("jvm", "1.1.50")
}

group = "com.github.langara.uspek"
version = "0.0.1"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib", "1.1.50"))
    implementation("junit:junit:4.12")
    testImplementation("org.assertj:assertj-core:3.8.0")
}

//buildScan {
//    setLicenseAgreementUrl("https://gradle.com/terms-of-service")
//    setLicenseAgree("yes")
//
//    publishAlways()
//}

// Create sources Jar from main kotlin sources
val sourcesJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles sources JAR"
    classifier = "sources"
    from(java.sourceSets["main"].allSource)
}

publishing {
    publications {
        create("default", MavenPublication::class.java) {
            from(components["java"])
            artifact(sourcesJar)
        }
    }
    repositories {
        maven {
            url = uri("$buildDir/repository")
        }
    }
}
