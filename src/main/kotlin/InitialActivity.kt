
import com.intellij.compiler.server.BuildManager
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.externalSystem.service.project.manage.ExternalProjectsDataStorage
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class InitialActivity : StartupActivity {
    private val LOG = Logger.getInstance("jps_caches.InitialActivity")

    override fun runActivity(project: Project) {
        showDebugBalloon(ExternalProjectsDataStorage.getProjectConfigurationDir(project).toFile().absolutePath)
        showDebugBalloon(BuildManager.getInstance().getProjectSystemDirectory(project)?.absolutePath!!)
        return
    }

    private val displayId = "KotlinUpdateJPSCachesNotificationDebug"
    private val notificationGroup = NotificationGroup(displayId, NotificationDisplayType.BALLOON, true)

    private fun showDebugBalloon(string: String) {
        val notification = notificationGroup.createNotification(string, NotificationType.INFORMATION)
        Notifications.Bus.notify(notification)
    }

}