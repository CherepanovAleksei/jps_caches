package loaders

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import org.apache.log4j.Level
import java.io.File

class BuildSrcFolderLoader(private val buildSrcFolder: File) : ILoader {
    private val LOG = Logger.getInstance("jps_caches.BuildSrcFolderLoader")
        .also { it.setLevel(Level.WARN) }

    override fun copy(project: Project) {
        val oldBuildSrcFolder = File(project.basePath!! + "/buildSrc")
        LOG.warn(oldBuildSrcFolder.deleteRecursively().toString())

        FileUtil.moveDirWithContent(
            buildSrcFolder,
            oldBuildSrcFolder
        ).also { if(!it) LOG.error("Can't move buildSrc folder") }
    }

    override fun move() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun rollback() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}