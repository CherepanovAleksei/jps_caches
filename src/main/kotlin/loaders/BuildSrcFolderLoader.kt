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
        val buildSrcBuildClassesFolder = File(project.basePath!! + "/buildSrc/build/classes").also {
            if (!it.exists())
                throw Exception(
                    "buildSrc/build/classes/ does not exists," +
                            " please re-import Gradle project"
                )
        }

        val oldBuildSrcCachesFolder = File(buildSrcBuildClassesFolder.absolutePath + "/java")
        if (oldBuildSrcCachesFolder.exists()) {
            LOG.warn(oldBuildSrcCachesFolder.deleteRecursively().toString())
        }

        FileUtil.moveDirWithContent(
            buildSrcFolder,
            oldBuildSrcCachesFolder
        ).also { if (!it) LOG.error("Can't move buildSrc folder") }
    }

    override fun rollback() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}