
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class InitialActivity : StartupActivity {
    private val LOG = Logger.getInstance("jps_caches.InitialActivity")

    override fun runActivity(project: Project) {
        LOG.warn("plugin started")
        val newVersionChecker = NewVersionChecker(project, "/home/mrneumann/jb/root_dir"/*, 5000*/)
        newVersionChecker.initTimer()
    }
}