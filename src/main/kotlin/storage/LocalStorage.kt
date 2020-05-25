package storage

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.util.io.ZipUtil
import org.apache.log4j.Level
import java.io.File

class LocalStorage : IStorage {
    private val LOG = Logger.getInstance("jps_caches.LocalStorage").also { it.setLevel(Level.WARN) }

    override val path: String
        get() = "/Users/Sergey.Rostov/jb/root_dir"

    override val lastVersion: String?
        get() {
            val rootDir = File(path)
            if (!rootDir.exists() || !rootDir.isDirectory) {
                LOG.warn("caches root folder is invalid")
                return null
            }
            val subDirs = rootDir.list { dir, name -> File(dir, name).isDirectory }
            subDirs?.sort()
            return subDirs?.last()
        }

    override fun downloadCaches(cachesVersion: String?, project: Project): File {
        val indicator = ProgressManager.getInstance().progressIndicator.apply {
            text = "Downloading new caches"
        }

        val tempFolder = FileUtil.createTempDirectory(
            File(project.basePath!!), "newJpsCaches.", ".tmp"
        )

        if (cachesVersion.isNullOrEmpty()) throw Exception("Local caches version is null")

        val storageRootFolder = File(path)
        val cacheFolder = File("$storageRootFolder/$cachesVersion")
        if (!storageRootFolder.isDirectory || !cacheFolder.exists())
            throw Exception("Cache storage is invalid")

        val remoteZip = cacheFolder.listFiles()?.first { it.name.endsWith(".zip") }
            ?: throw Exception("There is no zip file in remote folder!")

        indicator.text = "Unzipping new caches"
        ZipUtil.extract(remoteZip, tempFolder, null)

        return tempFolder
    }
}