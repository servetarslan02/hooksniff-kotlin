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
    jvmToolchain(21)
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    repositories {
        maven {
            name = "OSSRH"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("OSSRH_USERNAME") ?: "spwZqL"
                password = System.getenv("OSSRH_PASSWORD") ?: "ledXS3IjH8MmDQX312BWiB2DsRxQM5CiY"
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
                description.set("Official Kotlin SDK for HookSniff webhook platform")
                url.set("https://github.com/servetarslan02/hooksniff-kotlin")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
