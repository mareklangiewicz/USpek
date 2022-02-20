import pl.mareklangiewicz.defaults.*

buildscript {

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    dependencies {
        classpath(Deps.kotlinGradlePlugin)
    }
}

plugins {
    id("io.github.gradle-nexus.publish-plugin") version Vers.nexusPublishGradlePlugin
}

defaultGroupAndVer(Deps.uspek)

ext["signing.keyId"] = System.getenv("MYKOTLIBS_SIGNING_KEYID")
ext["signing.password"] = System.getenv("MYKOTLIBS_SIGNING_PASS")
ext["signing.key"] = System.getenv("MYKOTLIBS_SIGNING_KEY")
ext["ossrhUsername"] = System.getenv("MYKOTLIBS_OSSRH_USERNAME")
ext["ossrhPassword"] = System.getenv("MYKOTLIBS_OSSRH_PASSWORD")
ext["sonatypeStagingProfileId"] = System.getenv("MYKOTLIBS_STAGING_PROFILEID")

nexusPublishing {
    repositories {
        sonatype {  //only for users registered in Sonatype after 24 Feb 2021
            stagingProfileId put rootExt("sonatypeStagingProfileId")
            username put rootExt("ossrhUsername")
            password put rootExt("ossrhPassword")
            nexusUrl put uri("https://s01.oss.sonatype.org/service/local/")
            snapshotRepositoryUrl put uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        }
    }
}



allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

