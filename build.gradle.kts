// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val kotlinVersion by extra("1.4.31")
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-alpha13")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

@Suppress("JcenterRepositoryObsolete") allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter() // some libs haven't migrated yet, so we left it here for now
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
