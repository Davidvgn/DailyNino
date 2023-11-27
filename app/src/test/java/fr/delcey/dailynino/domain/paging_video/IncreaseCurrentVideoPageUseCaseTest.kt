package fr.delcey.dailynino.domain.paging_video

import fr.delcey.dailynino.TestCoroutineRule
import io.mockk.confirmVerified
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class IncreaseCurrentVideoPageUseCaseTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val pagingVideoRepository: PagingVideoRepository = mockk()

    private lateinit var increaseCurrentVideoPageUseCase: IncreaseCurrentVideoPageUseCase

    @Before
    fun setUp() {
        justRun { pagingVideoRepository.incrementPage() }

        increaseCurrentVideoPageUseCase = IncreaseCurrentVideoPageUseCase(
            pagingVideoRepository,
        )
    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {
        // When
        increaseCurrentVideoPageUseCase.invoke()

        // Then
        verify(exactly = 1) {
            pagingVideoRepository.incrementPage()
        }
        confirmVerified(pagingVideoRepository)
    }
}