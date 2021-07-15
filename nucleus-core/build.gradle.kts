plugins {
    java
    `java-library`
    eclipse
    id("de.undercouch.download")
}

group = "io.github.nucleuspowered"

repositories {
    mavenCentral()
    maven("https://repo-new.spongepowered.org/repository/maven-public")
}

sourceSets {
    main
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val spongeApiVersion: String by rootProject
val vavrVersion: String by rootProject

dependencies {
    api(project(":nucleus-api"))

    val dep = "org.spongepowered:spongeapi:$spongeApiVersion"
    annotationProcessor(dep)
    api(dep)

    api("io.vavr:vavr:$vavrVersion")
    implementation("org.spongepowered:timings:1.0-SNAPSHOT")

    testImplementation("org.mockito:mockito-all:1.10.19")
    testImplementation("org.powermock:powermock-module-junit4:1.6.4")
    testImplementation("org.powermock:powermock-api-mockito:1.6.4")
    testImplementation("org.hamcrest:hamcrest-junit:2.0.0.0")
    testImplementation("junit", "junit", "4.12")
}

val downloadCompat by tasks.registering(de.undercouch.gradle.tasks.download.Download::class) {
    src("https://v2.nucleuspowered.org/data/nca.json")
    dest(File(buildDir, "resources/main/assets/nucleus/compat.json"))
    onlyIfModified(true)
}
