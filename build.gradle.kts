plugins {
    kotlin("jvm") version "1.8.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

val asmVersion = "9.4"

group = "wtf.zani"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.ow2.asm:asm-tree:$asmVersion")
    implementation("org.ow2.asm:asm-util:$asmVersion")
    implementation("org.ow2.asm:asm-commons:$asmVersion")
}

kotlin {
    jvmToolchain(11)
}

tasks.shadowJar {
    manifest.attributes(
        "Premain-Class" to "wtf.zani.antioptifine.AgentKt"
    )
}
