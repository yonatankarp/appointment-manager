plugins {
    alias(libs.plugins.kotlin.jvm)
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.bundles.unit.test)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}


kotlin {
    javaToolchains {
        version = JavaLanguageVersion.of(21)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
