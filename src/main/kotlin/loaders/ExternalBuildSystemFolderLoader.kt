package loaders

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.externalSystem.service.project.manage.ExternalProjectsDataStorage
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import org.apache.log4j.Level
import java.io.File

class ExternalBuildSystemFolderLoader(private val externalBuildSystemFolder: File) :
    ILoader {
    private val LOG = Logger.getInstance("jps_caches.ExternalBuildSystemFolderLoader")
        .also { it.setLevel(Level.WARN) }

    override fun copy(project: Project) {
        val ex = ExternalProjectsDataStorage.getProjectConfigurationDir(project).toFile()
        LOG.warn(ex.absolutePath)
        LOG.warn(ex.deleteRecursively().toString())
        FileUtil.moveDirWithContent(
            externalBuildSystemFolder,
            ex
        ).also { if(!it) LOG.error("Can't move external_build_system folder") }
    }

    override fun move() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun rollback() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}