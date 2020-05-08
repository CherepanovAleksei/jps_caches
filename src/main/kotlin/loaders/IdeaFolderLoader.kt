package loaders

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import org.apache.log4j.Level
import java.io.File

class IdeaFolderLoader(private val ideaFolder: File) : ILoader {
    private val LOG = Logger.getInstance("#IdeaFolderLoader")
        .also { it.setLevel(Level.WARN) }

    override fun copy(project: Project) {
        val oldIdeaFolder = File(project.basePath!! + "/.idea")
        LOG.warn(oldIdeaFolder.deleteRecursively().toString())

        FileUtil.moveDirWithContent(
            ideaFolder,
            oldIdeaFolder
        ).also { if(!it) LOG.error("Can't move .idea folder") }
    }

    override fun move() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun rollback() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}