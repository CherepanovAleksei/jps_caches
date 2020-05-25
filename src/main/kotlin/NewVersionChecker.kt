import com.intellij.compiler.server.BuildManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ex.ProjectManagerEx
import loaders.LoaderManager
import org.apache.log4j.Level
import storage.LocalStorage
import java.io.File
import java.util.*

class NewVersionChecker(var project: Project, private val updatePeriod: Long = 300000) {
    private val LOG = Logger.getInstance("jps_caches.NewVersionChecker").also { it.setLevel(Level.WARN) }

    private val balloonNotification = BalloonNotification(::startRebase, ::startCheckout, project)
    private val persistentStorage = PersistentStorage(project)
    private val cacheStorage = LocalStorage()

    fun initTimer() {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (hasNewVersion()) {
                    balloonNotification.showBalloon()
                }
            }
        }, 0, updatePeriod)
    }

    private fun hasNewVersion(): Boolean {
        val remoteLastCacheVersion = cacheStorage.lastVersion ?: return false
        val localCacheVersion = persistentStorage.localCacheVersion

        if (localCacheVersion != remoteLastCacheVersion) {
            persistentStorage.localCacheVersion = remoteLastCacheVersion
            return true
        }
        return true
    }

    private fun startRebase() {
        LOG.warn("rebase called")
    }

    private fun startCheckout() {
        //download by commit
        //GitBrancher.getInstance(project).checkout(hashOrRefName, false, listOf(repository), null)
        val tempFolder: File
        try {
            tempFolder = cacheStorage.downloadCaches(persistentStorage.localCacheVersion, project)
        } catch (e: Exception) {
            LOG.error(e)
            return
        }

        try {
            val loaderManager = LoaderManager(tempFolder, project)
            loaderManager.load()
        } catch (e: Exception) {
            LOG.error(e)
            return
        } finally {
            tempFolder.deleteRecursively()
        }

        BuildManager.getInstance().clearState(project)
        LOG.warn("Loader work is finished, reloading...")
        ProjectManagerEx.getInstanceEx().reloadProject(project)
    }
}