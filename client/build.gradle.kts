plugins {
    id("java")
    id("application")
    id("com.gradleup.shadow") version "9.0.0-beta13" // Add this line for the Shadow plugin
}

group = "cool.circuit"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(project(":api")) // Ensure the API module is included
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("cool.circuit.Client")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "cool.circuit.Client"
        )
    }
    from(sourceSets.main.get().output)
}

tasks.shadowJar {
    configurations = project.configurations.compileClasspath.map { listOf(it) }
}