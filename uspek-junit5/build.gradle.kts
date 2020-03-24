plugins {
    `maven-publish`
    kotlin("jvm")
}

group = "com.github.langara.uspek"
version = "0.0.6"

dependencies {
    implementation(Deps.kotlinStdlib8)
    api(project(":uspek"))
    api(Deps.junit5)
    runtimeOnly(Deps.junit5engine)
}

// Create sources Jar from main kotlin sources
val sourcesJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles sources JAR"
    classifier = "sources"
    from(sourceSets.getByName("main").allSource)
}

publishing {
    publications {
        create("default", MavenPublication::class.java) {
            from(components.getByName("java"))
            artifact(sourcesJar)
        }
    }
    repositories {
        maven {
            url = uri("$buildDir/repository")
        }
    }
}
