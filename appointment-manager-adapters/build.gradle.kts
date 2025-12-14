plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(project(":appointment-manager-application"))
    implementation(project(":appointment-manager-domain"))

    testImplementation(libs.bundles.unit.test)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(21)
}
