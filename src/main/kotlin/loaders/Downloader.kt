package loaders

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import org.apache.log4j.Level
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


class Downloader(private val address: String){
    private val LOG = Logger.getInstance("#Downloader").also { it.setLevel(Level.WARN) }

    fun apply(project: Project, subfolder: String?): File? {
        //check commit
        if (subfolder == "") return null
        val remoteFolder = File("$address/$subfolder")
        val remoteZip = remoteFolder.listFiles()?.first { it.name.endsWith(".zip") }
        if (remoteZip == null) {
            LOG.error("There is no zip file in remote folder!")
            return null
        }
        val tempFolder = FileUtil.createTempDirectory(File(project.basePath!!),"newJpsCaches.", ".tmp")
        val tmpZip = FileUtil.createTempFile(tempFolder,"JpsCaches",".zip", true)
        FileUtil.copy(remoteZip, tmpZip)
        extract(tmpZip, tempFolder)
        val res = tmpZip.delete()
        LOG.warn(res.toString())
        return tempFolder
    }

    private val BUFFER_SIZE = 4096

    @Throws(IOException::class)
    fun extract(zip: File, target: File) {
        val zis = ZipInputStream(FileInputStream(zip))
        try {
            var entry: ZipEntry? = zis.nextEntry
            while (entry != null) {
                val file = File(target, entry.name)
                if (!file.toPath().normalize().startsWith(target.toPath())) {
                    throw IOException("Bad zip entry")
                }
                if (entry.isDirectory) {
                    file.mkdirs()
                    entry = zis.nextEntry
                    continue
                }
                val buffer = ByteArray(BUFFER_SIZE)
                file.parentFile.mkdirs()
                val out = BufferedOutputStream(FileOutputStream(file))
                var count: Int
                while (zis.read(buffer).also { count = it } != -1) {
                    out.write(buffer, 0, count)
                }
                out.close()
                entry = zis.nextEntry
            }
        } finally {
            zis.close()
        }
    }

    fun rollback(){}

    private fun downloadByCommit(commit: String) {
        //TODO: download from server
        /*address

        ProgressManager.getInstance().run(object : Backgroundable(project, "Rebasing...") {
            override fun run(indicator: ProgressIndicator) {
                GitRebaseUtils.rebase(
                    project,
                    listOf(dialog.getSelectedRepository()),
                    dialog.getSelectedParams(),
                    indicator
                )
            }
        })*/
    }
}
