package com.example.pomodoro

import com.example.pomodoro.data.FocusUiState
import com.example.pomodoro.ui.components.ViewModelCountDown
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    lateinit var viewModel: ViewModelCountDown
    lateinit var focusUiState: FocusUiState
    lateinit var restUiState: FocusUiState

    @Before
    fun setup () {
        viewModel = ViewModelCountDown()
    }

    @Test
    fun test_StartCoroutine() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testScope = TestScope(testDispatcher)


        viewModel.startCountDown()
        testScheduler.advanceUntilIdle()
        delay(10)


        assertEquals(true, viewModel.controller.testStudyCountdown)
        assertEquals(true, viewModel.controller.testRestCountdown)
        assertEquals(3, viewModel.controller.testNumberLoopStudy)
        assertEquals(3, viewModel.controller.testNumberLoopRest)
        assertEquals(0, viewModel.focusUiState.value.sessions)
    }

}