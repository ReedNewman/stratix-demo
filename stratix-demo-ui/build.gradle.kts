apply {
  plugin("base")
}

tasks {
  withType<Delete> {
    delete(buildDir)
  }

  val installAngular by creating {
    description = "Install Angular Dependencies"
    group = BasePlugin.BUILD_GROUP
    doFirst {
      exec { commandLine("npm", "install") }
    }
  }

  val buildAngular by creating {
    dependsOn(installAngular)
    description = "Build Angular Application"
    group = BasePlugin.BUILD_GROUP
    doFirst {
      exec { commandLine("ng", "build", "--prod") }
    }
  }

  withType<ProcessResources> {
    dependsOn(buildAngular)
  }
}

