plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    `maven-publish`
    signing
}

group = "com.hooksniff"
version = "0.5.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    repositories {
        maven {
            name = "OSSRH"
            val releasesUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsUrl else releasesUrl
            credentials {
                username = System.getenv("OSSRH_USERNAME") ?: findProperty("ossrhUsername") as? String ?: ""
                password = System.getenv("OSSRH_PASSWORD") ?: findProperty("ossrhPassword") as? String ?: ""
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "com.hooksniff"
            artifactId = "hooksniff-kotlin"
            version = project.version.toString()
            pom {
                name.set("HookSniff Kotlin SDK")
                description.set("Official Kotlin SDK for HookSniff — the webhook infrastructure for developers")
                url.set("https://github.com/servetarslan02/hooksniff-kotlin")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("servetarslan02")
                        name.set("Servet Arslan")
                        email.set("servetarslan02@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/servetarslan02/hooksniff-kotlin.git")
                    developerConnection.set("scm:git:ssh://github.com/servetarslan02/hooksniff-kotlin.git")
                    url.set("https://github.com/servetarslan02/hooksniff-kotlin")
                }
            }
        }
    }
}

signing {
    val signingKey = findProperty("signingKey") as? String ?: System.getenv("GPG_SIGNING_KEY")?.trim()
    val signingPassword = findProperty("signingPassword") as? String ?: System.getenv("GPG_SIGNING_PASSWORD")?.trim() ?: ""
    if (!signingKey.isNullOrBlank() && signingKey.contains("BEGIN PGP")) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications["mavenJava"])
    }
}
