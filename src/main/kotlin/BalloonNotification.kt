import com.intellij.notification.*
import com.intellij.notification.NotificationDisplayType.STICKY_BALLOON
import com.intellij.notification.NotificationType.INFORMATION
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class BalloonNotification(private val rebase: () -> Unit, private val checkout: () -> Unit) {
    private val title = "New prebuilt version of master is ready to use"
    private val displayId = "KotlinUpdateJPSCachesNotification"
    private val notificationGroup = NotificationGroup(displayId, STICKY_BALLOON, true)

    fun throwBalloon(){
        val notification = notificationGroup.createNotification(title, INFORMATION).also {
            it.addAction(object : AnAction("Rebase my branch","", null){
                override fun actionPerformed(e: AnActionEvent) {
                    it.expire()
                    rebase()
                }
            })
            it.addAction(object : AnAction("Checkout","", null){
                override fun actionPerformed(e: AnActionEvent) {
                    it.expire()
                    checkout()
                }
            })
        }
        Notifications.Bus.notify(notification)
    }
}