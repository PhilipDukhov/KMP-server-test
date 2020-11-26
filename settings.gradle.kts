include(":server")
rootProject.name = "My Application"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        jcenter()
        mavenCentral()
    }
    resolutionStrategy {
        eachPlugin {
            val pluginId = requested.id.id
            val gradlePluginVersion: String by settings
            when {
                pluginId == "com.android" || pluginId == "kotlin-android-extensions" ->
                useModule("com.android.tools.build:gradle:${gradlePluginVersion}")
                else -> {}
            }
        }
    }
}
