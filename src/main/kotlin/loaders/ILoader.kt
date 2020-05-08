package loaders

import com.intellij.openapi.project.Project

interface ILoader {
    fun copy(project: Project): Unit
    fun move()
    fun rollback()
}