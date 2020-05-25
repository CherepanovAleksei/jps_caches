package storage

import com.intellij.openapi.project.Project
import java.io.File

class RemoteStorage : IStorage {
    override val path: String
        get() = TODO("Not yet implemented")
    override val lastVersion: String?
        get() = TODO("Not yet implemented")

    override fun downloadCaches(cachesVersion: String?, project: Project): File {
        TODO("Not yet implemented")
    }

}