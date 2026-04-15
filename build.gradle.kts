plugins {
    id("java-library")
    id("groovy")
    id("idea")
    id("jacoco")
    id("maven-publish")

    id("io.freefair.lombok") version("9.2.0")
    id("com.github.hierynomus.license")  version("0.16.1")
}
repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://repository.hosting.ebf.de/nexus/repository/releases/") }
}

group = "com.ebf"
version = "3.0.1"

java {
    withSourcesJar()
    withJavadocJar()

    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

idea {
    module {
        outputDir = file("build/classes/main")
        testOutputDir = file("build/classes/test")
    }
}

dependencies {
    compileOnly(libs.spring.starter.jpa)
    compileOnly(libs.spring.starter.security)

    annotationProcessor(libs.spring.autoconfigure.processor)
    annotationProcessor(libs.spring.configuration.processor)

    testImplementation(libs.bundles.test)
    testRuntimeOnly(libs.test.junit.launcher)
}

license {
    header = file("header.txt")
    exclude("LICENSE.txt")
}

tasks.jar {
    manifest {
        attributes["Implementation-Title"] = "Spring Granular Permissions"
        attributes["Implementation-Version"] = archiveVersion
    }
}

tasks.test {
    useJUnitPlatform()

    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        csv.required = false
        xml.required = true
        html.required = true
    }

    finalizedBy(tasks.jacocoTestCoverageVerification)
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = BigDecimal.valueOf(0.80) // at least 80% coverage is required
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name = "Spring Granular Permissions"
                description = "Granular permission library for spring boot"
                url = "https://github.com/ebf/spring-granular-permissions"
                packaging = "jar"

                organization {
                    name = "EBF GmbH"
                    url = "https://ebf.com"
                }

                developers {
                    developer {
                        id = "EBF"
                        name = "EBF"
                        email = "dev@ebf.com"
                        organization = "EBF GmbH"
                        organizationUrl = "https://ebf.com"
                    }

                    developers {
                        developer {
                            id = "iznenad"
                            name = "Nenad Nikolic"
                            email = "nenad.nikolic@ebf.de"
                            organization = "EBF GmbH"
                            organizationUrl = "https://ebf.com"
                        }
                    }
                }

                issueManagement {
                    system = "Github"
                    url = "https://github.com/ebf/spring-granular-permissions/issues"
                }

                scm {
                    developerConnection = "scm:git:ssh://git@github.com/ebf/spring-granular-permissions.git"
                    connection = "scm:git:git://github.com/ebf/spring-granular-permissions.git"
                    url = "https://github.com/ebf/spring-granular-permissions"
                }

                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
            }
        }
    }

    repositories {
        maven {
            name = "ebf-releases-deployment"
            url = uri("https://repository.hosting.ebf.de/nexus/content/repositories/releases/")

            if (project.hasProperty("nexus_user") && project.hasProperty("nexus_pass")) {
                credentials {
                    username = project.property("nexus_user") as String
                    password = project.property("nexus_pass") as String
                }
            }
        }
    }
}
