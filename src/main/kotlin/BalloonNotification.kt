import com.intellij.notification.NotificationDisplayType.BALLOON
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType.INFORMATION
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator
import com.intellij.openapi.project.Project


class BalloonNotification(private val rebase: () -> Unit, private val checkout: () -> Unit, var project: Project) {
    private val title = "New prebuilt version of master is ready to use"
    private val displayId = "KotlinUpdateJPSCachesNotification"
    private val notificationGroup = NotificationGroup(displayId, BALLOON, true)

    fun showBalloon() {
        val notification = notificationGroup.createNotification(title, INFORMATION).also {
            it.addAction(object : AnAction("Rebase my branch", "", null) {
                override fun actionPerformed(e: AnActionEvent) {
                    it.expire()
                    startBackgroundProcess("Rebase process", rebase)
                }
            })
            it.addAction(object : AnAction("Checkout", "", null) {
                override fun actionPerformed(e: AnActionEvent) {
                    it.expire()
                    startBackgroundProcess("Checkout process", checkout)
                }
            })
        }
        Notifications.Bus.notify(notification)
    }

    private fun startBackgroundProcess(title: String, function: () -> Unit) {
        val task = object : Task.Backgroundable(project, title) {
            override fun run(indicator: ProgressIndicator) {
                function()
            }
        }

        val processIndicator = BackgroundableProcessIndicator(task)
        processIndicator.isIndeterminate = true
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, processIndicator)
    }
}