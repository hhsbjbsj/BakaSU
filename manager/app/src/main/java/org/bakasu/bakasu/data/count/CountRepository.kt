package org.bakasu.bakasu.data.count

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.bakasu.bakasu.data.shell.KsuCliRepository

data class CountState(
    val superuserCount: Int = 0,
    val moduleCount: Int = 0,
)

class CountRepository(
    private val ksuCliRepository: KsuCliRepository,
) {
    private val mutableState = MutableStateFlow(CountState())
    val state: StateFlow<CountState> = mutableState.asStateFlow()

    suspend fun refresh() {
        val (superuserCount, moduleCount) = withContext(Dispatchers.IO) {
            val suCount = runCatching { ksuCliRepository.getSuperuserCount() }.getOrDefault(0)
            val modCount = withTimeoutOrNull(2000) {
                runCatching { ksuCliRepository.getModuleCount() }.getOrDefault(0)
            } ?: 0
            suCount to modCount
        }
        mutableState.update {
            it.copy(superuserCount = superuserCount, moduleCount = moduleCount)
        }
    }
}
