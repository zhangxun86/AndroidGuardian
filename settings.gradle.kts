pluginManagement {
    repositories {
        maven {
            name = "GitHubPackages-zhangxun86"
            url = uri("https://maven.pkg.github.com/zhangxun86/my-public-aar-libs")
            // 加上凭证，以私有方式访问 (Kotlin DSL 语法)
            credentials {
                username = providers.gradleProperty("gpr.user").orNull
                password = providers.gradleProperty("gpr.key").orNull
            }
        }

        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }

        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()

        maven { url = uri("https://developer.huawei.com/repo") }
        maven { url = uri("https://artifact.bytedance.com/repository/pangle") }
        maven { url = uri("https://repo1.maven.org/maven2/")}
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://mvn.sigmob.com/repository/maven-public/") }




    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {

        maven {
            name = "GitHubPackages-zhangxun86"
            url = uri("https://maven.pkg.github.com/zhangxun86/my-public-aar-libs")
            // 加上凭证，以私有方式访问 (Kotlin DSL 语法)
            credentials {
                username = providers.gradleProperty("gpr.user").orNull
                password = providers.gradleProperty("gpr.key").orNull
            }
        }

        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }


        google()
        mavenCentral()

        maven { url = uri("https://developer.huawei.com/repo") }
        maven { url = uri("https://artifact.bytedance.com/repository/pangle") }
        maven { url = uri("https://repo1.maven.org/maven2/")}
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://mvn.sigmob.com/repository/maven-public/") }

    }
}

rootProject.name = "AndroidGuardian"
include(":app")
include(":guardian")
