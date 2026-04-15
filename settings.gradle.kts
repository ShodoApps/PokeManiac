pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PokeManiac"
include(":app")
include(":feature:searchfriend")
include(":feature:myfriends")
include(":feature:dashboard")
include(":feature:posttransaction")
include(":feature:myprofile")
include(":shared:domain")
include(":shared:presentation")
include(":shared:api")
include(":shared:data")
include(":shared:di")
include(":coreui")
include(":shared:database")
include(":shared:tracking")
include(":feature:billing")
