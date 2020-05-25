import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project

class PersistentStorage(project: Project) {
    private val persistentStorage = PropertiesComponent.getInstance(project)
    private val localCacheName = "jps_caches_kotlin.localCacheVersion"

    var localCacheVersion: String?
        get() = persistentStorage.getValue(localCacheName, "").ifEmpty { null }
        set(value) = persistentStorage.setValue(localCacheName, value)
}