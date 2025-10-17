pluginManagement {
    repositories {

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
                // 优先从环境变量读取（用于 JitPack），如果找不到，再从本地 gradle.properties 读取
                username = System.getenv("GITHUB_ACTOR") ?: providers.gradleProperty("gpr.user").orNull
                password = System.getenv("GITHUB_TOKEN") ?: providers.gradleProperty("gpr.key").orNull
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
