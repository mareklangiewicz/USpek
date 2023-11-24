import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.*

plugins {
    plug(plugs.NexusPublish)
    plug(plugs.KotlinMulti) apply false
    plug(plugs.KotlinJvm) apply false
}

defaultBuildTemplateForRootProject(
    langaraLibDetails(
        name = "USpek",
        description = "Micro tool for testing with syntax similar to Spek, but shorter.",
        githubUrl = "https://github.com/langara/USpek",
        version = Ver(0, 0, 28),
        // https://repo1.maven.org/maven2/pl/mareklangiewicz/uspek/
        // https://github.com/langara/USpek/releases
    ),
    withSonatypeOssPublishing = true,
)

// region [Root Build Template]

/** Publishing to Sonatype OSSRH has to be explicitly allowed here, by setting withSonatypeOssPublishing to true. */
fun Project.defaultBuildTemplateForRootProject(
    libDetails: LibDetails? = null,
    withSonatypeOssPublishing: Boolean = false
) {
    check(libDetails != null || !withSonatypeOssPublishing)
    ext.addDefaultStuffFromSystemEnvs()
    libDetails?.let {
        rootExtLibDetails = it
        defaultGroupAndVerAndDescription(it)
        if (withSonatypeOssPublishing) defaultSonatypeOssNexusPublishing()
    }

    // kinda workaround for kinda issue with kotlin native
    // https://youtrack.jetbrains.com/issue/KT-48410/Sync-failed.-Could-not-determine-the-dependencies-of-task-commonizeNativeDistribution.#focus=Comments-27-5144160.0-0
    repositories { mavenCentral() }
}

/**
 * System.getenv() should contain six env variables with given prefix, like:
 * * MYKOTLIBS_signing_keyId
 * * MYKOTLIBS_signing_password
 * * MYKOTLIBS_signing_keyFile (or MYKOTLIBS_signing_key with whole signing key)
 * * MYKOTLIBS_ossrhUsername
 * * MYKOTLIBS_ossrhPassword
 * * MYKOTLIBS_sonatypeStagingProfileId
 * * First three of these used in fun pl.mareklangiewicz.defaults.defaultSigning
 * * See DepsKt/template-mpp/template-mpp-lib/build.gradle.kts
 */
fun ExtraPropertiesExtension.addDefaultStuffFromSystemEnvs(envKeyMatchPrefix: String = "MYKOTLIBS_") =
    addAllFromSystemEnvs(envKeyMatchPrefix)

fun Project.defaultSonatypeOssNexusPublishing(
    sonatypeStagingProfileId: String = rootExtString["sonatypeStagingProfileId"],
    ossrhUsername: String = rootExtString["ossrhUsername"],
    ossrhPassword: String = rootExtString["ossrhPassword"],
) {
    nexusPublishing {
        this.repositories {
            sonatype {  // only for users registered in Sonatype after 24 Feb 2021
                stagingProfileId put sonatypeStagingProfileId
                username put ossrhUsername
                password put ossrhPassword
                nexusUrl put repos.sonatypeOssNexus
                snapshotRepositoryUrl put repos.sonatypeOssSnapshots
            }
        }
    }
}

// endregion [Root Build Template]