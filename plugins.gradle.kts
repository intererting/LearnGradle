//class GreetingPlugin : Plugin<Project> {
//    override fun apply(project: Project) {
//        project.task("hello") {
//            doLast {
//                println("Hello from the GreetingPlugin")
//            }
//        }
//    }
//}
//
//// Apply the plugin
//apply<GreetingPlugin>()

//通过Extension动态配置参数
//abstract class GreetingPluginExtension {
//    abstract val message: Property<String>
//
//    init {
//        message.convention("Hello from GreetingPlugin")
//    }
//}
//
//class GreetingPlugin : Plugin<Project> {
//    override fun apply(project: Project) {
//        // Add the 'greeting' extension object
//        val extension = project.extensions.create<GreetingPluginExtension>("greeting")
//        // Add a task that uses configuration from the extension object
//        project.task("hello") {
//            doLast {
//                println(extension.message.get())
//            }
//        }
//    }
//}
//
//apply<GreetingPlugin>()
//
//// Configure the extension
//the<GreetingPluginExtension>().message.set("Hi from Gradle")

//另一种配置方式
//interface GreetingPluginExtension {
//    val message: Property<String>
//    val greeter: Property<String>
//}
//
//class GreetingPlugin : Plugin<Project> {
//    override fun apply(project: Project) {
//        val extension = project.extensions.create<GreetingPluginExtension>("greeting")
//        project.task("hello") {
//            doLast {
//                println("${extension.message.get()} from ${extension.greeter.get()}")
//            }
//        }
//    }
//}
//
//apply<GreetingPlugin>()
//
//// Configure the extension using a DSL block
//configure<GreetingPluginExtension> {
//    message.set("Hi")
//    greeter.set("Gradle")
//}

abstract class GreetingToFileTask : DefaultTask() {

    @get:OutputFile
    abstract val destination: RegularFileProperty

    @TaskAction
    fun greet() {
        val file = destination.get().asFile
        file.parentFile.mkdirs()
        file.writeText("Hello!")
    }
}

val greetingFile = objects.fileProperty()

tasks.register<GreetingToFileTask>("greet") {
    destination.set(greetingFile)
}

tasks.register("sayGreeting") {
    dependsOn("greet")
    doLast {
        val file = greetingFile.get().asFile
        println("${file.readText()} (file: ${file.name})")
    }
}

greetingFile.set(layout.buildDirectory.file("hello.txt"))