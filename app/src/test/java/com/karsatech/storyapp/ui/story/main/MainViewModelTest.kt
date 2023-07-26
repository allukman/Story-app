package com.karsatech.storyapp.ui.story.main

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.karsatech.storyapp.data.remote.StoryRepository
import com.karsatech.storyapp.data.remote.response.DetailStory
import com.karsatech.storyapp.ui.adapter.StoryAdapter
import com.karsatech.storyapp.utils.DataDummy
import com.karsatech.storyapp.utils.MainDispatcherRules
import com.karsatech.storyapp.utils.UserPreference
import com.karsatech.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ListStoryViewModelTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRules()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var preference: UserPreference

    private val dummyStoriesResponse = DataDummy.generateDummyStories()

    @Before
    fun setup() {
        preference = mock(UserPreference::class.java)
    }

    @Test
    fun `when getStories Should Not Null and Return Success`() = runTest {
        val data: PagingData<DetailStory> = StoryPagingSource.snapshot(dummyStoriesResponse.listStory)
        val expectedStories = MutableLiveData<PagingData<DetailStory>>()
        expectedStories.value = data
        Mockito.`when`(storyRepository.getStories()).thenReturn(expectedStories)

        val listStoryViewModel = MainViewModel(preference,storyRepository)
        val actualStories: PagingData<DetailStory> = listStoryViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStories)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStoriesResponse.listStory, differ.snapshot())
        Assert.assertEquals(dummyStoriesResponse.listStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStoriesResponse.listStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when getStories Should Return Empty Data`() = runTest {
        val emptyData: PagingData<DetailStory> = PagingData.empty()
        val expectedStories = MutableLiveData<PagingData<DetailStory>>()
        expectedStories.value = emptyData
        Mockito.`when`(storyRepository.getStories()).thenReturn(expectedStories)

        val listStoryViewModel = MainViewModel(preference,storyRepository)
        val actualStories: PagingData<DetailStory> = listStoryViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStories)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertTrue(differ.snapshot().isEmpty())
        Assert.assertEquals(0, differ.snapshot().size)
    }

}

class StoryPagingSource : PagingSource<Int, LiveData<List<DetailStory>>>() {
    companion object {
        fun snapshot(items: List<DetailStory>): PagingData<DetailStory> {
            return PagingData.from(items)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<DetailStory>>>): Int {
        return 0
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<DetailStory>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}