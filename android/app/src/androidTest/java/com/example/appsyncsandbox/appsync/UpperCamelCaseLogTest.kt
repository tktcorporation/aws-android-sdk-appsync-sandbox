package com.example.appsyncsandbox.appsync

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.amazonaws.amplify.generated.graphql.CreateUpperCamelCaseLogMutation
import com.amazonaws.amplify.generated.graphql.GetUpperCamelCaseLogQuery
import com.amazonaws.amplify.generated.graphql.ListUpperCamelCaseLogsQuery
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
import type.CreateUpperCamelCaseLogInput
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
class UpperCamelCaseUpperCamelCaseLogTest {
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
        assertEquals(log.Content(), "sample")
        assertEquals(log.Kind(), "kind")
    }

    @Test
    fun listTest() = runBlocking {
        val list = list()
        assert(list.count() > 0)
        val log = list.first()
        assertNotNull(log)
        assertEquals(log.Content(), "sample")
        assertEquals(log.Kind(), "kind")
    }

    @Test
    fun getTest() = runBlocking {
        val list = list()
        assert(list.count() > 0)
        val log = list.first()

        val result = get(log.Kind(), log.TimestampMs())

        assertNotNull(result)
        assertEquals(result!!.Content(), "sample")
        assertEquals(result.Kind(), "kind")
    }

    private suspend fun create():
            CreateUpperCamelCaseLogMutation.CreateUpperCamelCaseLog {
        val input = CreateUpperCamelCaseLogInput
            .builder()
            .content("sample")
            .id(UUID.randomUUID().toString())
            .kind("kind")
            .timestampMs(System.currentTimeMillis().toDouble())
            .build()
        return suspendCoroutine { continuation ->
            val mutation = CreateUpperCamelCaseLogMutation.builder().input(input).build()
            val caller = client.mutate(mutation)
            caller.enqueue(object :
                GraphQLCall.Callback<CreateUpperCamelCaseLogMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    throw e
                }

                override fun onResponse(response: Response<CreateUpperCamelCaseLogMutation.Data>) {
                    if (response.errors().size > 0) {
                        continuation.resumeWithException(Exception(response.errors().toString()))
                        return
                    }
                    val result = response.data()!!.createUpperCamelCaseLog()!!
                    continuation.resume(result)
                }
            })
        }
    }

    private suspend fun list() : List<ListUpperCamelCaseLogsQuery.Item> {
        val filter = ListUpperCamelCaseLogsQuery.builder().build()
        return suspendCoroutine { continuation ->
            client.query(filter).responseFetcher(NETWORK_FIRST).enqueue(object :
                GraphQLCall.Callback<ListUpperCamelCaseLogsQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    throw e
                }

                override fun onResponse(response: Response<ListUpperCamelCaseLogsQuery.Data>) {
                    if (response.errors().size > 0) {
                        continuation.resumeWithException(Exception(response.errors().toString()))
                        return
                    }
                    val result = response.data()!!.listUpperCamelCaseLogs()!!.items()!!
                    continuation.resume(result)
                }
            })
        }
    }

    private suspend fun get(kind: String, timestampMs: Double) : GetUpperCamelCaseLogQuery.GetUpperCamelCaseLog? {
        val query = GetUpperCamelCaseLogQuery
            .builder()
            .kind(kind)
            .timestampMs(timestampMs)
            .build()
        return suspendCoroutine { continuation ->
            client.query(query).responseFetcher(NETWORK_FIRST).enqueue(object :
                GraphQLCall.Callback<GetUpperCamelCaseLogQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    throw e
                }

                override fun onResponse(response: Response<GetUpperCamelCaseLogQuery.Data>) {
                    if (response.errors().size > 0) {
                        continuation.resumeWithException(Exception(response.errors().toString()))
                        return
                    }
                    val result = response.data()!!.upperCamelCaseLog
                    continuation.resume(result)
                }
            })
        }
    }
}