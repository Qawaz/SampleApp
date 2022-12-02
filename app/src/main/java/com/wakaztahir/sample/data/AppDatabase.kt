package com.wakaztahir.sample.data

import android.content.Context
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.wakaztahir.sample.controller.AppSettings
import com.wakaztahir.sampleDb.SampleDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppDatabase @Inject constructor(
    @ApplicationContext val context: Context,
    private val settings: AppSettings
) {

    fun interface InitializationListener {
        fun onInitialized(oldVersion: Long)
    }

    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val driver = AndroidSqliteDriver(SampleDatabase.Schema, context, name = "mindNodeDB")
    val db = SampleDatabase(driver)

    private var isInitialized = false
    private var oldVersion = 0L
    private var listener: InitializationListener? = null

    init {
        scope.launch { initialize() }
    }

    private suspend fun initialize() {
        val oldVersion = getDatabaseVersion().await()
        val newVersion = SampleDatabase.Schema.version
        if (oldVersion == 0L) {
            SampleDatabase.Schema.create(driver)
            setDatabaseVersion(newVersion).await()
        } else if (newVersion > oldVersion) {
            SampleDatabase.Schema.migrate(driver, oldVersion.toInt(), newVersion)
            setDatabaseVersion(newVersion)
        }
        this.oldVersion = oldVersion
        listener?.onInitialized(oldVersion)
        isInitialized = true
    }


    /** gets the current database version i.e. `user_version` using pragma **/
    private fun getDatabaseVersion() =
        driver.executeQuery<Long>(null, "PRAGMA $versionPragma", mapper = { cursor ->
            if (cursor.next()) {
                cursor.getLong(0) ?: 0
            } else {
                0
            }
        }, parameters = 0)

    private fun setDatabaseVersion(newVersion: Int): QueryResult<Long> =
        driver.execute(null, "PRAGMA $versionPragma=$newVersion", 0)


    fun setInitializationListener(listener: InitializationListener) {
        if (isInitialized) {
            listener.onInitialized(oldVersion)
        } else {
            this.listener = listener
        }
    }

    companion object {
        private const val versionPragma = "user_version"
    }

}