import com.intellij.openapi.diagnostic.Logger
import org.apache.log4j.Level
import java.io.File
import java.util.*


class NewVersionChecker(private val address: String, private val updatePeriod: Long = 300000) {

    private val LOG = Logger.getInstance("#NewVersionChecker").also { it.setLevel(Level.WARN) }
    private var latestVersion: String? = null
    private val balloonNotification = BalloonNotification({rebase()}, {checkout()})

    fun initTimer() {
        val timer = Timer()
        timer.schedule(object :TimerTask() {
            override fun run() {
                if(checkNewVersion()){
                    balloonNotification.throwBalloon()
                }
            }
        }, 0, updatePeriod)
    }

    private fun checkNewVersion() : Boolean {
        //TODO another initial check
        val rootDir = File(this.address)
        val subDirs = rootDir.list { dir, name -> File(dir, name).isDirectory }
        subDirs?.sort()
        if(latestVersion != subDirs?.last()) {
            latestVersion = subDirs?.last()
            return true
        }
        return false
    }

    private fun rebase() {
        LOG.warn("rebase called")
    }

    private fun checkout() {
        LOG.warn("checkout called")
    }
}