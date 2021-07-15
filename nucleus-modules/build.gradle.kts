plugins {
    `java-library`
    eclipse
}

group = "io.github.nucleuspowered"

repositories {
    mavenCentral()
    maven("https://repo-new.spongepowered.org/repository/maven-public")
    // maven("https://jitpack.io")
}

sourceSets {
    main {
        java {
            srcDir("src/main/java")
        }
        resources {
            srcDir("src/main/resources")
            exclude("assets/nucleus/suggestions/**")
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    api(project(":nucleus-api"))
    implementation(project(":nucleus-core"))

    testImplementation("org.mockito:mockito-all:1.10.19")
    testImplementation("org.powermock:powermock-module-junit4:1.6.4")
    testImplementation("org.powermock:powermock-api-mockito:1.6.4")
    testImplementation("org.hamcrest:hamcrest-junit:2.0.0.0")
    testImplementation("junit", "junit", "4.12")
}
