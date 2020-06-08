package loaders

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.util.containers.stream
import org.apache.log4j.Level
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors


class LoaderManager(tempFolder: File, private val project: Project) {
    private val LOG = Logger.getInstance("jps_caches.LoaderManager").also { it.setLevel(Level.WARN) }
    private val expectedFolders =
        mutableListOf("compile-server", "out", "dist", "buildSrc"/*, ".gradle", ".idea", "external_build_system"*/)
    val cachesFolder = File(tempFolder.absolutePath + "/prebuilt_caches")

    fun load() {
        if (!checkNewCaches(cachesFolder)) {
            LOG.warn("caches are invalid")
            return //TODO throw error
        }

        ProgressManager.getInstance().progressIndicator.apply {
            text = "Loading new caches"
        }

        initLoaders(cachesFolder).map { item ->
            CompletableFuture
                .supplyAsync { item.copy(project) }
                .join()
        }
    }

    private fun initLoaders(cachesFolder: File): MutableList<ILoader> {
        val folders = cachesFolder.listFiles()!!
        return mutableListOf(
            //IdeaFolderLoader(folders.first { file -> file.name == ".idea" }),
            DistFolderLoader(folders.first { file -> file.name == "dist" }),
            OutFolderLoader(folders.first { file -> file.name == "out" }),
            CompileServerLoader(folders.first { file -> file.name == "compile-server" }),
            BuildSrcFolderLoader(folders.first { file -> file.name == "buildSrc" })
            //GradleFolderLoader(folders.first { file -> file.name == ".gradle" }),
            //ExternalBuildSystemFolderLoader(folders.first { file -> file.name == "external_build_system" }),
        )
    }


    private fun checkNewCaches(cachesFolder: File): Boolean {
        val folderNames = cachesFolder.listFiles().stream().map { it.name }.collect(Collectors.toList())
        return folderNames.sort() == expectedFolders.sort()
    }
}

