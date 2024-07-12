import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.io.FileInputStream
import java.io.IOException
import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.dagger.hilt.android) apply false
}

val key: String = getLocalProperty("MAPKIT_API_KEY").toString()

fun getLocalProperty(propertyName: String): String? {
    return try {
        val properties = Properties()
        properties.load(FileInputStream(File(rootDir, "local.properties")))
        properties.getProperty(propertyName)
    } catch (e: IOException) {
        null
    }

}




