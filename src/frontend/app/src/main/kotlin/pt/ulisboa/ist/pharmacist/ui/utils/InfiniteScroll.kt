package pt.ulisboa.ist.pharmacist.ui.utils

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow

data class InfiniteScrollData(
    val totalCount: Int,
    val reachedBottom: Boolean
)

/**
 * A composable that handles infinite scrolling in a list.
 * @param listState the state of the list
 * @param loadMore a callback to be invoked when the user reaches the end of the list
 */
@Composable
fun InfiniteScrollHandler(
    listState: LazyListState,
    loadMore: () -> Unit
) {

    LaunchedEffect(true) {
        snapshotFlow {
            InfiniteScrollData(
                totalCount = listState.layoutInfo.totalItemsCount,
                reachedBottom = listState.reachedBottom()
            )
        }
            .collect {
                if (it.reachedBottom) {
                    val lastVisibleItemIndex =
                        listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                    Log.d("MEDICINES_SCROLL", "Reached bottom $lastVisibleItemIndex")
                    loadMore()
                }
            }
    }
}


private fun LazyListState.reachedBottom(): Boolean {
    val layoutInfo = this.layoutInfo
    val totalItemsNumber = layoutInfo.totalItemsCount
    val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
    return totalItemsNumber == 0 || lastVisibleItemIndex == totalItemsNumber - 1
}
