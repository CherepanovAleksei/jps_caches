package loaders

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import org.apache.log4j.Level
import java.io.File

class DistFolderLoader(private val distFolder: File) : ILoader {
    private val LOG = Logger.getInstance("#DistFolderLoader")
        .also { it.setLevel(Level.WARN) }

    override fun copy(project: Project) {
        val oldDistFolder = File(project.basePath!! + "/dist")
        LOG.warn(oldDistFolder.deleteRecursively().toString())

        FileUtil.moveDirWithContent(
            distFolder,
            oldDistFolder
        ).also { if (!it) LOG.error("Can't move dist folder") }
    }

    override fun move() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun rollback() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}