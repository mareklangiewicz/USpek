buildscript {

    repositories {
        mavenCentral()
    }

    dependencies {
        classpath(Deps.kotlinGradlePlugin)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

