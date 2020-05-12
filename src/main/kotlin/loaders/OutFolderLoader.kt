package loaders

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import org.apache.log4j.Level
import java.io.File

class OutFolderLoader(private val newOutFolder: File) : ILoader {
    private val LOG = Logger.getInstance("jps_caches.OutFolderLoader")
        .also { it.setLevel(Level.WARN) }

    override fun copy(project: Project) {
        val time = System.currentTimeMillis()
        val oldOutFolder = File(project.basePath!! + "/out")
        LOG.warn(oldOutFolder.deleteRecursively().toString())

        FileUtil.moveDirWithContent(
            newOutFolder,
            oldOutFolder
        ).also { if(!it) LOG.error("Can't move out folder") }
        LOG.warn((System.currentTimeMillis()-time).toString())
    }

    override fun move() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun rollback() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}