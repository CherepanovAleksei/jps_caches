
import com.intellij.compiler.server.BuildManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.externalSystem.service.project.manage.ExternalProjectsDataStorage
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class InitialActivity : StartupActivity {
    private val LOG = Logger.getInstance("jps_caches.InitialActivity")


    override fun runActivity(project: Project) {
//        val repositoryList = GitRepositoryUtil.getCommitsIterator(project)
//        var commitId = ""
//        var commitsBehind = 0
//        for (commitsIterator in repositoryList) {
//            commitsBehind = 0
//            while (commitsIterator.hasNext() ) {
//                commitId = commitsIterator.next()
//                commitsBehind++
//            }
//        }
//return
        LOG.warn(ExternalProjectsDataStorage.getProjectConfigurationDir(project).toFile().absolutePath)
        LOG.warn(BuildManager.getInstance().getProjectSystemDirectory(project)?.absolutePath)
        NewVersionChecker(project, 900000).initTimer()
    }
}