
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ex.ProjectManagerEx
import loaders.Downloader
import loaders.LoaderManager
import org.apache.log4j.Level
import java.io.File
import java.util.*


class NewVersionChecker(var project: Project, private val address: String, private val updatePeriod: Long = 300000) {

    private val LOG = Logger.getInstance("#NewVersionChecker").also { it.setLevel(Level.WARN) }
    private var localCacheName = "jps_caches_kotlin.localCacheVersion"
    private val balloonNotification = BalloonNotification( ::startRebase, ::startCheckout)
    private val persistentStorage = PropertiesComponent.getInstance(project)

    fun initTimer() {
        val timer = Timer()
        timer.schedule(object :TimerTask() {
            override fun run() {
                if(hasNewVersion()){
                    balloonNotification.showBalloon()
                }
            }
        }, 0, updatePeriod)
    }

    private fun hasNewVersion() : Boolean {
        val localCacheVersion = persistentStorage.getValue(localCacheName, "")
        val rootDir = File(this.address)
        val subDirs = rootDir.list { dir, name -> File(dir, name).isDirectory }
        subDirs?.sort()
        if(localCacheVersion != subDirs?.last()) {
            persistentStorage.setValue(localCacheName, subDirs?.last())
            return true
        }
        return true
//TODO: switch for production
        //return false
    }

    private fun startRebase() {
        LOG.warn("rebase called")
        ProjectManagerEx.getInstanceEx().reloadProject(project)
    }

    private fun startCheckout() {
        //GitBrancher.getInstance(project).checkout(hashOrRefName, false, listOf(repository), null)
        val downloader = Downloader(address)
        val time = System.currentTimeMillis()
        val tempFolder = downloader.apply(project, persistentStorage.getValue(localCacheName, ""))
        LOG.warn((System.currentTimeMillis()-time).toString())
        if (tempFolder == null){
            return
        }
        val loaderManager = LoaderManager(tempFolder, project)
        loaderManager.apply()
        LOG.warn("FINISH!")
        ProjectManagerEx.getInstanceEx().reloadProject(project)
        //download by commit
        //unzip
        //apply
        //rerun
    }
}