package storage

import com.intellij.openapi.project.Project
import java.io.File

interface IStorage {
    val path: String
    val lastVersion: String?
    fun downloadCaches(cachesVersion: String?, project: Project): File
}