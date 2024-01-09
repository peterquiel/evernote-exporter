import org.gradle.jvm.tasks.Jar

plugins {
    id("application")
    kotlin("jvm") version ("1.7.20")
    id("org.graalvm.buildtools.native") version "0.9.28"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

group = "com.stm.evernote.exporter"
version = "1.0.0"

dependencies {
    implementation("com.evernote:evernote-api:1.25.1")
    implementation("commons-io:commons-io:2.11.0")
    implementation("commons-codec:commons-codec:1.15")
    implementation("info.picocli:picocli:4.6.3")
    annotationProcessor("info.picocli:picocli-codegen:4.6.3")

    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("ch.qos.logback:logback-classic:1.2.11")

    testImplementation("junit:junit:4.13.2")

    implementation("com.google.guava:guava:30.1.1-jre")
}

tasks.register("fatjar", Jar::class) {
    group = "Build"
    description = "Build a fat jar with all dependencies"
    manifest {
        attributes["Main-Class"] = "com.stm.evenote.exporter.EvernoteExporter"
    }
    archiveBaseName.set("evernote-exporter")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get())
}

application {
    mainClass.set("com.stm.evenote.exporter.EvernoteExporter")
    applicationName = "EvernoteExporter"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("evernote-exporter")
            verbose.set(true)
            buildArgs.addAll("--enable-url-protocols=https")
        }
    }
}
