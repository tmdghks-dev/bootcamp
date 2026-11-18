plugins {
    java
    application
}

group = "com.back"
version = "1.0-SNAPSHOT"

repositories { mavenCentral() }

dependencies {
    runtimeOnly("com.mysql:mysql-connector-j:9.5.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.20.1")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.assertj:assertj-core:3.27.6")
}

application {
    mainClass = "com.back.Main"
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

tasks.test { useJUnitPlatform() }
