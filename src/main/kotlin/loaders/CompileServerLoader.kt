package loaders

import com.intellij.compiler.server.BuildManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import org.apache.log4j.Level
import java.io.File
import java.io.IOException

class CompileServerLoader(private val compileServerFolder: File) : ILoader {
    private val LOG = Logger.getInstance("#CompileServerLoader")
        .also { it.setLevel(Level.WARN) }
    private val TIMESTAMPS_FOLDER_NAME = "timestamps"
    private val FS_STATE_FILE = "fs_state.dat"

    override fun copy(project: Project) {
        val time = System.currentTimeMillis()
        val newTimestampFolder =
            File(compileServerFolder, TIMESTAMPS_FOLDER_NAME)
        if (newTimestampFolder.exists()) FileUtil.delete(newTimestampFolder)

        val currentDirForBuildCache: File? = BuildManager.getInstance().getProjectSystemDirectory(project)//add error
        LOG.warn(currentDirForBuildCache?.absolutePath)
        if (currentDirForBuildCache != null) {
            // Copy timestamp old folder to new cache dir
            val timestamps = File(
                currentDirForBuildCache,
                TIMESTAMPS_FOLDER_NAME
            )
            if (timestamps.exists()) {
                try {
                    newTimestampFolder.mkdirs()
                    FileUtil.copyDir(timestamps, newTimestampFolder) //need copy?
                } catch (e: IOException) {
                    LOG.warn("Couldn't copy timestamps from old JPS cache", e)
                }
            }

            // Create new empty fsStateFile
            val fsStateFile =
                File(compileServerFolder, FS_STATE_FILE)
            fsStateFile.delete()
            try {
                fsStateFile.createNewFile()
            } catch (e: IOException) {
                LOG.warn("Couldn't create new empty FsState file", e)
            }
            LOG.warn(currentDirForBuildCache.deleteRecursively().toString())

            FileUtil.moveDirWithContent(
                compileServerFolder,
                currentDirForBuildCache
            ).also { if(!it) LOG.error("Can't move compile_server folder") }
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