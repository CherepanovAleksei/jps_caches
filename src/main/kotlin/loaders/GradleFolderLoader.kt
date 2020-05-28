package loaders

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import org.apache.log4j.Level
import java.io.File

class GradleFolderLoader(private val gradleFolder: File) : ILoader {
    private val LOG = Logger.getInstance("jps_caches.GradleFolderLoader")
        .also { it.setLevel(Level.WARN) }

    override fun copy(project: Project) {
        val gradleFolder = File(project.basePath!! + "/.gradle")
        LOG.warn(gradleFolder.deleteRecursively().toString())
        FileUtil.moveDirWithContent(
            this.gradleFolder,
            gradleFolder
        ).also { if (!it) LOG.error("Can't move .gradle folder") }
    }

    override fun rollback() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}