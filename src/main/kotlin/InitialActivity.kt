import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class InitialActivity : StartupActivity {
    private val newVersionChecker = NewVersionChecker("/home/mrneumann/jb/root_dir", 5000)

    override fun runActivity(project: Project) {
        newVersionChecker.initTimer()
    }
}