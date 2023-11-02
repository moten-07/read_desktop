import groovy.json.JsonGenerator.Options
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
	kotlin("jvm")
	id("org.jetbrains.compose")
}

group = "com.quin"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
	maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	google()
}

dependencies {
	// Note, if you develop a library, you should use compose.desktop.common.
	// compose.desktop.currentOs should be used in launcher-sourceSet
	// (in a separate module for demo project and in testMain).
	// With compose.desktop.common you will also lose @Preview functionality
	implementation(compose.desktop.currentOs)
	implementation("org.usb4java:usb4java-javax:1.3.0")
}

compose.desktop {
	application {
		mainClass = "MainKt"

		nativeDistributions {
			targetFormats(
				TargetFormat.Exe,
				TargetFormat.Msi,
			)
			packageName = "read_desktop"
			packageVersion = "1.0.0"
		}
	}
}