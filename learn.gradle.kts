//val cstmName: String by project
//
////通过-P添加运行时参数
////./gradlew -q -Pproperty="property" learn
////或者在gradle.properties中配置，这里可以直接获取
//tasks.register("learn") {
//    doLast {
//        println(cstmName)
//    }
//}

//配置java编译环境
//tasks.withType<JavaCompile>().configureEach {
//    options.compilerArgs = listOf("-Xdoclint:none", "-Xlint:none", "-nowarn")
//}

//./gradlew -q projects 获取所有projects
//
//tasks.register("getRepo") {
//    doFirst {
//        repositories.forEach {
//            println(it.name)
//        }
//    }
//}

//可以通过./gradlew -q直接执行
//defaultTasks("defaultTask")
//
//tasks.register("defaultTask") {
//
//    timeout.set(java.time.Duration.ofMillis(1000))
//
//    //可以添加执行条件，只有满足了条件才会执行
//    enabled = true
//    onlyIf { project.hasProperty("version") }
//    doFirst {
//        println("test")
//    }
//}

//增量编译
//abstract class IncrementalReverseTask : DefaultTask() {
//    @get:Incremental
//    @get:PathSensitive(PathSensitivity.NAME_ONLY)
//    @get:InputDirectory
//    abstract val inputDir: DirectoryProperty
//
//    @get:OutputDirectory
//    abstract val outputDir: DirectoryProperty
//
//    @get:Input
//    abstract val inputProperty: Property<String>
//
//    @TaskAction
//    fun execute(inputChanges: InputChanges) {
//        println(
//            if (inputChanges.isIncremental) "Executing incrementally"
//            else "Executing non-incrementally"
//        )
//
//        inputChanges.getFileChanges(inputDir).forEach { change ->
//            if (change.fileType == FileType.DIRECTORY) return@forEach
//
//            println("${change.changeType}: ${change.normalizedPath}")
//            val targetFile = outputDir.file(change.normalizedPath).get().asFile
//            if (change.changeType == ChangeType.REMOVED) {
//                targetFile.delete()
//            } else {
//                targetFile.writeText(change.file.readText().reversed())
//            }
//        }
//    }
//}
//
//tasks.register<IncrementalReverseTask>("greeting") {
//    inputDir.set(file("input"))
//    outputDir.set(file("output"))
//    //如果是修改了属性，那么会重新执行，ACTION将会被认为为ADD
//    inputProperty.set("change")
//    doLast {
//        println("finish")
//    }
//}

//Work API
// The parameters for a single unit of work
//interface ReverseParameters : WorkParameters {
//    val fileToReverse: RegularFileProperty
//    val destinationDir: DirectoryProperty
//}
//
//// The implementation of a single unit of work
//abstract class ReverseFile @javax.inject.Inject
//constructor(val fileSystemOperations: FileSystemOperations) :
//    WorkAction<ReverseParameters> {
//    override fun execute() {
//        fileSystemOperations.copy {
//            from(parameters.fileToReverse)
//            into(parameters.destinationDir)
//            filter { line: String -> line.reversed() }
//        }
//    }
//}
//
//// The WorkerExecutor will be injected by Gradle at runtime
//abstract class ReverseFiles @javax.inject.Inject constructor(private val workerExecutor: WorkerExecutor) :
//    SourceTask() {
//    @get:OutputDirectory
//    abstract val outputDir: DirectoryProperty
//
//    @TaskAction
//    fun reverseFiles() {
//        // Create a WorkQueue to submit work items
//        val workQueue = workerExecutor.noIsolation()
//
//        // Create and submit a unit of work for each file
//        source.forEach { file ->
//            workQueue.submit(ReverseFile::class) {
//                fileToReverse.set(file)
//                destinationDir.set(outputDir)
//            }
//        }
//    }
//}
//
//tasks.register<ReverseFiles>("greeting") {
//    outputDir.set(file("output"))
//}

//Provider
// A task that displays a greeting
//abstract class Greeting : DefaultTask() {
//    // Configurable by the user
//    @get:Input
//    abstract val greeting: Property<String>
//
//    // Read-only property calculated from the greeting
//    @Internal
//    val message: Provider<String> = greeting.map { "$it from Gradle" }
//
//    @TaskAction
//    fun printMessage() {
//        logger.quiet(message.get())
//    }
//}
//
//tasks.register<Greeting>("greeting") {
//    // Configure the greeting
//    greeting.set("Hi")
//
//}

// A project extension
//interface MessageExtension {
//    // A configurable greeting
//    val greeting: Property<String>
//}
//
//// A task that displays a greeting
//abstract class Greeting : DefaultTask() {
//    // Configurable by the user
//    @get:Input
//    abstract val greeting: Property<String>
//
//    // Read-only property calculated from the greeting
//    @Internal
//    val message: Provider<String> = greeting.map { "$it from Gradle" }
//
//    @TaskAction
//    fun printMessage() {
//        logger.quiet(message.get())
//    }
//}
//
//// Create the project extension
//val messages = project.extensions.create<MessageExtension>("messages")
//
//// Create the greeting task
//tasks.register<Greeting>("greeting") {
//    // Attach the greeting from the project extension
//    // Note that the values of the project extension have not been configured yet
//    greeting.set(messages.greeting)
//}
//
//messages.apply {
//    // Configure the greeting on the extension
//    // Note that there is no need to reconfigure the task's `greeting` property. This is automatically updated as the extension property changes
//    greeting.set("Hi")
//}

//File Api
// A task that generates a source file and writes the result to an output directory
//abstract class GenerateSource : DefaultTask() {
//    // The configuration file to use to generate the source file
//    @get:InputFile
//    abstract val configFile: RegularFileProperty
//
//    // The directory to write source files to
//    @get:OutputDirectory
//    abstract val outputDir: DirectoryProperty
//
//    @TaskAction
//    fun compile() {
//        val inFile = configFile.get().asFile
//        logger.quiet("configuration file = $inFile")
//        val dir = outputDir.get().asFile
//        logger.quiet("output dir = $dir")
//        val className = inFile.readText().trim()
//        val srcFile = File(dir, "${className}.java")
//        srcFile.writeText("public class ${className} { }")
//    }
//}
//
//// Create the source generation task
//tasks.register<GenerateSource>("generate") {
//    // Configure the locations, relative to the project and build directories
//    configFile.set(layout.projectDirectory.file("src/config.txt"))
//    outputDir.set(layout.buildDirectory.dir("build"))
//}
//
//// Change the build directory
//// Don't need to reconfigure the task properties. These are automatically updated as the build directory changes
//layout.buildDirectory.set(layout.projectDirectory.dir("output"))

//Input OutPut API
//连接两个Task
//abstract class Producer : DefaultTask() {
//    @get:OutputFile
//    abstract val outputFile: RegularFileProperty
//
//    @TaskAction
//    fun produce() {
//        val message = "Hello, World!"
//        val output = outputFile.get().asFile
//        output.writeText(message)
//        logger.quiet("Wrote '${message}' to ${output}")
//    }
//}
//
//abstract class Consumer : DefaultTask() {
//    @get:InputFile
//    abstract val inputFile: RegularFileProperty
//
//    @TaskAction
//    fun consume() {
//        val input = inputFile.get().asFile
//        val message = input.readText()
//        logger.quiet("Read '${message}' from ${input}")
//    }
//}
//
//val producer = tasks.register<Producer>("producer")
//val consumer = tasks.register<Consumer>("consumer")
//
//consumer {
//    // Connect the producer task output to the consumer task input
//    // Don't need to add a task dependency to the consumer task. This is automatically added
//如果是Property<String>，那么这里就是map
//    inputFile.set(producer.flatMap { it.outputFile })
//}
//
//producer {
//    // Set values for the producer lazily
//    // Don't need to update the consumer.inputFile property. This is automatically updated as producer.outputFile changes
//    outputFile.set(layout.buildDirectory.file("file.txt"))
//}
//
//// Change the build directory.
//// Don't need to update producer.outputFile and consumer.inputFile. These are automatically updated as the build directory changes
//layout.buildDirectory.set(layout.projectDirectory.dir("output"))

//ListProperty
//abstract class Producer : DefaultTask() {
//    @get:OutputFile
//    abstract val outputFile: RegularFileProperty
//
//    @TaskAction
//    fun produce() {
//        val message = "Hello, World!"
//        val output = outputFile.get().asFile
//        output.writeText(message)
//        logger.quiet("Wrote '${message}' to ${output}")
//    }
//}
//
//abstract class Consumer : DefaultTask() {
//    @get:InputFiles
//    abstract val inputFiles: ListProperty<RegularFile>
//
//    @TaskAction
//    fun consume() {
//        inputFiles.get().forEach { inputFile ->
//            val input = inputFile.asFile
//            val message = input.readText()
//            logger.quiet("Read '${message}' from ${input}")
//        }
//    }
//}
//
//val producerOne = tasks.register<Producer>("producerOne")
//val producerTwo = tasks.register<Producer>("producerTwo")
//tasks.register<Consumer>("consumer") {
//    // Connect the producer task outputs to the consumer task input
//    // Don't need to add task dependencies to the consumer task. These are automatically added
//    inputFiles.add(producerOne.get().outputFile)
//    inputFiles.add(producerTwo.get().outputFile)
//}
//
//// Set values for the producer tasks lazily
//// Don't need to update the consumer.inputFiles property. This is automatically updated as producer.outputFile changes
//producerOne { outputFile.set(layout.buildDirectory.file("one.txt")) }
//producerTwo { outputFile.set(layout.buildDirectory.file("two.txt")) }
//
//// Change the build directory.
//// Don't need to update the task properties. These are automatically updated as the build directory changes
//layout.buildDirectory.set(layout.projectDirectory.dir("output"))

//Map Property
//abstract class Generator : DefaultTask() {
//    @get:Input
//    abstract val properties: MapProperty<String, Int>
//
//    @TaskAction
//    fun generate() {
//        properties.get().forEach { entry ->
//            logger.quiet("${entry.key} = ${entry.value}")
//        }
//    }
//}
//
//// Some values to be configured later
//var b = 0
//var c = 0
//
//tasks.register<Generator>("generate") {
//    properties.put("a", 1)
//    // Values have not been configured yet
//    properties.put("b", providers.provider { b })
//    properties.putAll(providers.provider { mapOf("c" to c, "d" to c + 1) })
//}
//
//// Configure the values. There is no need to reconfigure the task
////新设置的value会覆盖之前的
//b = 2
//c = 3

//convention的使用
tasks.register<Copy>("convention") {
    doLast {
        val property = objects.property(String::class)

// Set a convention
        property.convention("convention 1")
        println("value = " + property.get())

// Can replace the convention
        property.convention("convention 2")
        println("value = " + property.get())

        property.set("value")

// Once a value is set, the convention is ignored
        property.convention("ignored convention")
        println("value = " + property.get())
    }
}


