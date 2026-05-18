package com.hooksniff.kotlin

/**
 * Pagination Helper for HookSniff Kotlin SDK.
 *
 * Usage:
 *   val paginator = Paginator.create(
 *       fetchPage = { opts -> hs.message.list(opts) },
 *       limit = 100
 *   )
 *   for (msg in paginator) {
 *       println(msg.id)
 *   }
 */
class Paginator<T>(
    private val fetchPage: (ListOptions) -> ListResponse<T>,
    private val limit: Int? = null,
) : Iterable<T> {

    override fun iterator(): Iterator<T> = PaginatorIterator()

    fun toList(): List<T> = this.toList()

    private inner class PaginatorIterator : Iterator<T> {
        private var currentPage: ListResponse<T>? = null
        private var currentIndex = 0
        private var done = false
        private var iterator: String? = null

        override fun hasNext(): Boolean {
            if (done) return false

            if (currentPage == null) {
                fetchNextPage()
                if (currentPage == null) return false
            }

            val page = currentPage!!
            if (currentIndex < page.data.size) return true

            if (!page.done && page.iterator != null) {
                iterator = page.iterator
                fetchNextPage()
                return currentPage?.data?.isNotEmpty() == true
            }

            done = true
            return false
        }

        override fun next(): T {
            if (!hasNext()) throw NoSuchElementException()
            return currentPage!!.data[currentIndex++]
        }

        private fun fetchNextPage() {
            currentPage = fetchPage(ListOptions(limit, iterator))
            currentIndex = 0
        }
    }

    data class ListOptions(val limit: Int? = null, val iterator: String? = null)

    data class ListResponse<T>(
        val data: List<T>,
        val done: Boolean,
        val iterator: String?,
    )

    companion object {
        fun <T> create(
            fetchPage: (ListOptions) -> ListResponse<T>,
            limit: Int? = null,
        ) = Paginator(fetchPage, limit)
    }
}
