plugins {
    java
}

group = "org.example"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.github.bonigarcia:webdrivermanager:5.6.3")
    testImplementation("org.seleniumhq.selenium:selenium-java:4.31.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("ch.qos.logback:logback-classic:1.4.11")
    testImplementation("io.cucumber:cucumber-java:7.18.0")
    testImplementation("io.cucumber:cucumber-junit:7.18.0")
    testImplementation("io.cucumber:cucumber-core:7.18.0")
}

tasks.test {
    useJUnit()
    systemProperty("file.encoding", "UTF-8")
}
tasks.compileTestJava {
    options.encoding = "UTF-8"
}