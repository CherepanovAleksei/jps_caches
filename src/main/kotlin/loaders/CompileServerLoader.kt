package loaders

import com.intellij.compiler.server.BuildManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import org.apache.log4j.Level
import java.io.File
import java.io.IOException

class CompileServerLoader(private val newCachesFolder: File) : ILoader {
    private val LOG = Logger.getInstance("jps_caches.CompileServerLoader")
        .also { it.setLevel(Level.WARN) }
    private val TIMESTAMPS_FOLDER_NAME = "timestamps"
    private val FS_STATE_FILE = "fs_state.dat"
    private val USTAMP_FILE = "ustamp"

    override fun copy(project: Project) {
        val time = System.currentTimeMillis()
//TODO refactor this!!!
        val newTimestampFolder =
            File(newCachesFolder, TIMESTAMPS_FOLDER_NAME)
        if (newTimestampFolder.exists()) newTimestampFolder.deleteRecursively()

        val newUstampFile = File(newCachesFolder, USTAMP_FILE)
        if (newUstampFile.exists()) FileUtil.delete(newUstampFile)

        val newFsStateFile =
            File(newCachesFolder, FS_STATE_FILE)
        if (newFsStateFile.exists()) FileUtil.delete(newFsStateFile)

        val oldCachesFolder: File? = BuildManager.getInstance().getProjectSystemDirectory(project)//add error
        LOG.warn(oldCachesFolder?.absolutePath)


        if (oldCachesFolder != null) {
            // Copy timestamp old folder to new cache dir
            val oldTimestamps = File(
                oldCachesFolder,
                TIMESTAMPS_FOLDER_NAME
            )
            if (oldTimestamps.exists()) {
                FileUtil.createDirectory(newTimestampFolder)
                FileUtil.moveDirWithContent(
                    oldTimestamps,
                    newTimestampFolder
                ).also { if (!it) LOG.error("Can't move compile_server folder") }
            }

            val oldUstampFile = File(oldCachesFolder, USTAMP_FILE)
            if (oldUstampFile.exists()) {
                FileUtil.copy(oldUstampFile, newUstampFile)
            }

            // Create new empty fsStateFile
            try {
                newFsStateFile.createNewFile()
            } catch (e: IOException) {
                LOG.warn("Couldn't create new empty FsState file", e)
            }

            LOG.warn(oldCachesFolder.deleteRecursively().toString())
            FileUtil.moveDirWithContent(
                newCachesFolder,
                oldCachesFolder
            ).also { if (!it) LOG.error("Can't move compile_server folder") }
            LOG.warn((System.currentTimeMillis()-time).toString())
        }
    }

    override fun move() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun rollback() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}