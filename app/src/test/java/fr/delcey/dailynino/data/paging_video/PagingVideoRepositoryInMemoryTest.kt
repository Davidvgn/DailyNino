package fr.delcey.dailynino.data.paging_video

import app.cash.turbine.test
import fr.delcey.dailynino.TestCoroutineRule
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PagingVideoRepositoryInMemoryTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var pagingVideoRepositoryInMemory: PagingVideoRepositoryInMemory

    @Before
    fun setUp() {
        pagingVideoRepositoryInMemory = PagingVideoRepositoryInMemory()
    }

    @Test
    fun `initial case - getCurrentPageFlow should send 1`() = testCoroutineRule.runTest {
        // When
        pagingVideoRepositoryInMemory.getCurrentPageFlow().test {
            val result = awaitItem()

            // Then
            assertEquals(1, result)
        }
    }

    @Test
    fun `edge case - incrementPage should send 2`() = testCoroutineRule.runTest {
        // Given
        pagingVideoRepositoryInMemory.getCurrentPageFlow().test {
            awaitItem() // 1

            // When
            pagingVideoRepositoryInMemory.incrementPage()
            val result = awaitItem()

            // Then
            assertEquals(2, result)
        }
    }

    @Test
    fun `edge case - incrementPage twice should send 3`() = testCoroutineRule.runTest {
        // Given
        pagingVideoRepositoryInMemory.getCurrentPageFlow().test {
            awaitItem() // 1

            pagingVideoRepositoryInMemory.incrementPage()
            awaitItem() // 2

            // When
            pagingVideoRepositoryInMemory.incrementPage()
            val result = awaitItem()

            // Then
            assertEquals(3, result)
        }
    }

    @Test
    fun `edge case - reset should send 1 even when already on first page`() = testCoroutineRule.runTest {
        // Given
        pagingVideoRepositoryInMemory.getCurrentPageFlow().test {
            awaitItem() // 1

            // When
            pagingVideoRepositoryInMemory.resetPage()
            val result = awaitItem()

            // Then
            assertEquals(1, result)
        }
    }

    @Test
    fun `edge case - reset should send 1 when on any page`() = testCoroutineRule.runTest {
        // Given
        pagingVideoRepositoryInMemory.getCurrentPageFlow().test {
            awaitItem() // 1

            pagingVideoRepositoryInMemory.incrementPage()
            awaitItem() // 2

            // When
            pagingVideoRepositoryInMemory.resetPage()
            val result = awaitItem()

            // Then
            assertEquals(1, result)
        }
    }
}