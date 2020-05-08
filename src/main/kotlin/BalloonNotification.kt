import com.intellij.notification.NotificationDisplayType.BALLOON
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType.INFORMATION
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import java.util.concurrent.CompletableFuture


class BalloonNotification(private val rebase: () -> Unit, private val checkout: () -> Unit) {
    private val title = "New prebuilt version of master is ready to use"
    private val displayId = "KotlinUpdateJPSCachesNotification"
    private val notificationGroup = NotificationGroup(displayId, BALLOON, true)

    fun showBalloon() {
        val notification = notificationGroup.createNotification(title, INFORMATION).also {
            it.addAction(object : AnAction("Rebase my branch", "", null) {
                override fun actionPerformed(e: AnActionEvent) {
                    it.expire()
                    CompletableFuture.runAsync { rebase() }
                }
            })
            it.addAction(object : AnAction("Checkout", "", null) {
                override fun actionPerformed(e: AnActionEvent) {
                    it.expire()
                    CompletableFuture.runAsync { checkout() }
                }
            })
        }
        Notifications.Bus.notify(notification)
    }
}