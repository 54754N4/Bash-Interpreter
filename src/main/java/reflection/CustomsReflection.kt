package reflection

import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import java.util.concurrent.Executors
import kotlin.reflect.KClass

/** Ok wow. So :
 * -> functions needs to be inlined
 * -> so we can make generic parameters type T reified
 * -> so we can generically get java class references
 * + use covariance projection for parameter type since subType extends T (e.g. out T)
 */
const val defaultScanThreads = 6

inline fun <reified T : Any> retrieveKotlinSubtypes(path: String, threads: Int = defaultScanThreads): Set<KClass<out T>> {
    val converted = mutableSetOf<KClass<out T>>()
    retrieveSubtypes<T>(path, threads).forEach {jClass -> converted.add(jClass.kotlin)}
    return converted
}
inline fun <reified T : Any> retrieveSubtypes(path: String, threads: Int = defaultScanThreads) = retrieveClasses<T>(path, threads)
inline fun <reified T> retrieveClasses(path: String, threads: Int = defaultScanThreads): Set<Class<out T>> = Reflections(
    ConfigurationBuilder()
        .setUrls(ClasspathHelper.forPackage(path))    // set to look inside custom commands
        .setScanners(SubTypesScanner())               // enable the sub-types scanner
        .setExecutorService(Executors.newFixedThreadPool(threads))
).getSubTypesOf(T::class.java)
