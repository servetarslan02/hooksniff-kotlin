plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    `maven-publish`
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
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/servetarslan02/hooksniff-kotlin")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: "servetarslan02"
                password = System.getenv("GITHUB_TOKEN") ?: ""
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
