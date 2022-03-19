import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.utils.*

buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins { id("io.github.gradle-nexus.publish-plugin") version vers.nexusPublishGradlePlugin }

defaultGroupAndVer(deps.uspek)

ext.addAllFromSystemEnvs("MYKOTLIBS_")

nexusPublishing {
    repositories {
        sonatype {  //only for users registered in Sonatype after 24 Feb 2021
            stagingProfileId put rootExt("sonatypeStagingProfileId")
            username put rootExt("ossrhUsername")
            password put rootExt("ossrhPassword")
            nexusUrl put uri(repos.sonatypeOssNexus)
            snapshotRepositoryUrl put uri(repos.sonatypeOssSnapshots)
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
