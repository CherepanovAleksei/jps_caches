package loaders

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.util.containers.stream
import org.apache.log4j.Level
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors


class LoaderManager(private val tempFolder: File, private val project: Project) {
    private val LOG = Logger.getInstance("#LoaderManager").also { it.setLevel(Level.WARN) }
    private val expectedFolders =
        mutableListOf("buildSrc", "compile-server", ".gradle", ".idea", "out", "external_build_system")

    fun apply() {
        if (!checkNewCaches(tempFolder)) {
            LOG.warn("caches are invalid")
            return //TODO throw error
        }

        val loaders = initLoaders(tempFolder)
            .map { item -> CompletableFuture.supplyAsync { item.copy(project) } }

        CompletableFuture.allOf(*loaders.toTypedArray<CompletableFuture<Unit>>())
            .thenApply { v ->
                loaders.map { obj ->
                    obj.join()
                }
            }.get()
    }

    private fun initLoaders(tempFolder: File): MutableList<ILoader> {
        val folders = tempFolder.listFiles()!!
        return mutableListOf(
            IdeaFolderLoader(folders.first { file -> file.name == ".idea" }),
            CompileServerLoader(folders.first { file -> file.name == "compile-server" }),
            //GradleFolderLoader(folders.first { file -> file.name == ".gradle" }),
            //ExternalBuildSystemFolderLoader(folders.first { file -> file.name == "external_build_system" }),
            OutFolderLoader(folders.first { file -> file.name == "out" }),
            BuildSrcFolderLoader(folders.first { file -> file.name == "buildSrc" })
        )
    }


    private fun checkNewCaches(tempFolder: File): Boolean {
        val folderNames = tempFolder.listFiles().stream().map { it.name }.collect(Collectors.toList())
        return folderNames.sort() == expectedFolders.sort()
    }
}

