import java.io.File
import java.io.FileInputStream
import org.gradle.internal.impldep.org.apache.commons.io.FileUtils
import org.gradle.internal.impldep.org.apache.commons.codec.digest.DigestUtils

import java.io.InputStream
import java.lang.Exception
import java.nio.charset.Charset

interface MD5WorkParameters : WorkParameters {
    val sourceFile: RegularFileProperty
    val mD5File: RegularFileProperty
}

abstract class GenerateMD5 : WorkAction<MD5WorkParameters> {
    override fun execute() {
        try {
            val sourceFile: File = parameters.sourceFile.asFile.get()
            val md5File: File = parameters.mD5File.asFile.get()
            val stream: InputStream = FileInputStream(sourceFile)
            println("Generating MD5 for " + sourceFile.name + "...")
            // Artificially make this task slower.
            Thread.sleep(3000)
            FileUtils.writeStringToFile(
                md5File, DigestUtils.md5Hex(stream),
                Charset.defaultCharset()
            )
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}

abstract class CreateMD5 : SourceTask() {
    @get:InputFiles
    abstract val codecClasspath: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val destinationDirectory: DirectoryProperty

    @get:javax.inject.Inject
    abstract val workerExecutor: WorkerExecutor

    @TaskAction
    fun createHashes() {
        val workQueue = workerExecutor.classLoaderIsolation {
            this.classpath.from(codecClasspath)
        }
        for (mSourceFile in source.files) {
            val md5File = destinationDirectory.file(mSourceFile.nameWithoutExtension + ".md5")
            workQueue.submit(GenerateMD5::class.java) {
                sourceFile.set(mSourceFile)
                mD5File.set(md5File)
            }
        }
    }
}

tasks.register<CreateMD5>("md5") {
    destinationDirectory.set(project.layout.projectDirectory.dir("md5"))
    source(project.layout.projectDirectory.file("src"))
}