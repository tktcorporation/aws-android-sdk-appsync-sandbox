package com.example.appsyncsandbox.appsync

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.amazonaws.amplify.generated.graphql.CreateLogMutation
import com.amazonaws.amplify.generated.graphql.GetLogQuery
import com.amazonaws.amplify.generated.graphql.ListLogsQuery
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers.NETWORK_FIRST
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import kotlinx.coroutines.runBlocking

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import type.CreateLogInput
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class LogTest {
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private val client = AWSAppSyncClient.builder()
        .context(appContext)
        .awsConfiguration(AWSConfiguration(appContext))
        .build()

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.appsyncsandbox", appContext.packageName)
    }

    @Test
    fun createTest() = runBlocking {
        val log = create()
        assertEquals(log.content(), "sample")
        assertEquals(log.kind(), "kind")
    }

    @Test
    fun listTest() = runBlocking {
        val list = list()
        assert(list.count() > 0)
        val log = list.first()
        assertNotNull(log)
        assertEquals(log.content(), "sample")
        assertEquals(log.kind(), "kind")
    }

    @Test
    fun getTest() = runBlocking {
        val list = list()
        assert(list.count() > 0)
        val log = list.first()

        val result = get(log.kind(), log.timestampMs())

        assertNotNull(result)
        assertEquals(result!!.content(), "sample")
        assertEquals(result.kind(), "kind")
    }

    private suspend fun create():
            CreateLogMutation.CreateLog {
        val input = CreateLogInput
            .builder()
            .content("sample")
            .id(UUID.randomUUID().toString())
            .kind("kind")
            .timestampMs(System.currentTimeMillis().toDouble())
            .build()
        return suspendCoroutine { continuation ->
            val mutation = CreateLogMutation.builder().input(input).build()
            val caller = client.mutate(mutation)
            caller.enqueue(object :
                GraphQLCall.Callback<CreateLogMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    throw e
                }

                override fun onResponse(response: Response<CreateLogMutation.Data>) {
                    if (response.errors().size > 0) {
                        continuation.resumeWithException(Exception(response.errors().toString()))
                        return
                    }
                    val result = response.data()!!.createLog()!!
                    continuation.resume(result)
                }
            })
        }
    }

    private suspend fun list() : List<ListLogsQuery.Item> {
        val filter = ListLogsQuery.builder().build()
        return suspendCoroutine { continuation ->
            client.query(filter).responseFetcher(NETWORK_FIRST).enqueue(object :
                GraphQLCall.Callback<ListLogsQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    throw e
                }

                override fun onResponse(response: Response<ListLogsQuery.Data>) {
                    if (response.errors().size > 0) {
                        continuation.resumeWithException(Exception(response.errors().toString()))
                        return
                    }
                    val result = response.data()!!.listLogs()!!.items()!!
                    continuation.resume(result)
                }
            })
        }
    }

    private suspend fun get(kind: String, timestampMs: Double) : GetLogQuery.GetLog? {
        val query = GetLogQuery
            .builder()
            .kind(kind)
            .timestampMs(timestampMs)
            .build()
        return suspendCoroutine { continuation ->
            client.query(query).responseFetcher(NETWORK_FIRST).enqueue(object :
                GraphQLCall.Callback<GetLogQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    throw e
                }

                override fun onResponse(response: Response<GetLogQuery.Data>) {
                    if (response.errors().size > 0) {
                        continuation.resumeWithException(Exception(response.errors().toString()))
                        return
                    }
                    val result = response.data()!!.log
                    continuation.resume(result)
                }
            })
        }
    }
}