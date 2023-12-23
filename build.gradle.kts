plugins {
    kotlin("jvm") version "1.9.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.bcel:bcel:6.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.7.0")
    implementation("co.streamx.fluent:ex-tree:2.8.0")
    implementation("com.github.jasync-sql:jasync-postgresql:2.2.0")
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java)
    .all {
        compilerOptions {
            freeCompilerArgs.add("-Xno-call-assertions")
            freeCompilerArgs.add("-Xno-receiver-assertions")
            freeCompilerArgs.add("-Xno-param-assertions")
            freeCompilerArgs.add("-Xsam-conversions=class")
        }
    }