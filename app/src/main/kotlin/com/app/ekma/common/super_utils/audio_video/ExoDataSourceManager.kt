package com.app.ekma.common.super_utils.audio_video

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.CacheDataSink
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File
import java.io.Serializable

data class Trending(
    val id: String,
    val name: String,
    val url: String,
    val description: String,
    val thumb: String,
    val show: Boolean,
    val trendingType: String,
    val top: Int,
    val like: Int,
    val group: String,
    val music: String
) : Serializable

/*
How to use
    1. ExoDataSourceManager.getInstance(this)               at Application class
    2. ExoDataSourceManager.getInstance().preload(items)    after fetching data
    3. val mediaSource: MediaSource = ExoDataSourceManager.getInstance().toMediaSource(trending)    when setMediaSource for play
       exoPlayer.setMediaSource(mediaSource, true)
**/

class ExoDataSourceManager private constructor(context: Context) {

    companion object {
        private var INSTANCE: ExoDataSourceManager? = null

        @JvmStatic
        @JvmOverloads
        fun getInstance(context: Context? = null): ExoDataSourceManager {
            if (INSTANCE == null) {
                if (context == null) throw NullPointerException("No instance of ExoDataSourceManager found. Please provide context to init this")
                INSTANCE = ExoDataSourceManager(context)
            }
            return INSTANCE!!
        }
    }

    private var cacheInstance: Cache? = null
    private val cache by lazy {
        return@lazy cacheInstance ?: run {
            val exoCacheDir = File(context.filesDir, "cached_video")
            val evictor = LeastRecentlyUsedCacheEvictor(500 * 1024 * 1024)
            SimpleCache(exoCacheDir, evictor, ExoDatabaseProvider(context)).also {
                cacheInstance = it
            }
        }
    }

    private val cacheReadDataSourceFactory by lazy {
        FileDataSource.Factory()
    }

    private val cacheWriteDataSinkFactory by lazy {
        CacheDataSink.Factory().setCache(cache)
    }

    private val upstreamDataSourceFactory by lazy {
        DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)
    }

    private val mapCacheWriter = hashMapOf<Trending, CacheWriter>()

    fun preload(trendingVideos: List<Trending>) {
        trendingVideos.reversed().forEach {
            cache(it, 30)
        }
    }

    private fun cache(video: Trending, count: Int) = runCatching {
        if (cache.getCachedBytes(video.id, 0, C.LENGTH_UNSET.toLong()) > 350 * 1024) {
            return@runCatching
        }
        val videoUri = Uri.parse(video.url)
        val dataSpec = DataSpec(videoUri)

        var cacheWriter: CacheWriter? = null
        val listener = CacheWriter.ProgressListener { requestLength, bytesCached, _ ->
            val progress = (bytesCached / requestLength.toFloat()) * 100
            if (progress >= count) {
                cacheWriter?.cancel()
            }
        }
        cacheWriter = mapCacheWriter[video] ?: CacheWriter(
            getCacheDataSourceFactory(video).createDataSource(),
            dataSpec,
            null,
            listener
        )
        cacheWriter.cache()
        mapCacheWriter[video] = cacheWriter
    }

    fun toMediaSource(video: Trending) =
        ProgressiveMediaSource.Factory(getCacheDataSourceFactory(video))
            .createMediaSource(
                MediaItem.Builder()
                    .setUri(Uri.parse(video.url))
                    .setCustomCacheKey(video.id)
                    .build()
            )

    private fun getCacheDataSourceFactory(video: Trending) =
        CacheDataSource.Factory()
            .setCache(cache)
            .setCacheKeyFactory { video.id }
            .setUpstreamDataSourceFactory(upstreamDataSourceFactory)
            .setCacheReadDataSourceFactory(cacheReadDataSourceFactory)
            .setCacheWriteDataSinkFactory(cacheWriteDataSinkFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    fun cancelCaching(video: Trending?) = runCatching {
        mapCacheWriter[video]?.cancel()
    }

    fun removeCache(video: Trending) = runCatching {
        cache.removeResource(video.id)
    }
}