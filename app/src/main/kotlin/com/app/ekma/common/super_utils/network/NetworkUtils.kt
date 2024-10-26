package com.app.ekma.common.super_utils.network

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.app.ekma.common.super_utils.network.NetworkUtil.getConnectivityStatusString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

object NetworkUtil : CoroutineScope {
    const val TYPE_WIFI = 1
    const val TYPE_MOBILE = 2
    const val TYPE_NOT_CONNECTED = 0
    const val NETWORK_STATUS_NOT_CONNECTED = 0
    const val NETWORK_STATUS_WIFI = 1
    const val NETWORK_STATUS_MOBILE = 2
    private lateinit var connectivityManager: ConnectivityManager
    private var isAvailable = false
    private val _hasNetworkFlow = MutableStateFlow(false)
    val hasNetworkFlow = _hasNetworkFlow.asStateFlow()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default

    @JvmStatic
    fun init(context: Context) {
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.let {
            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    isAvailable = true
                    callback.keys.forEach { owner ->
                        if (owner.lifecycle.currentState in listOf(
                                Lifecycle.State.RESUMED,
                                Lifecycle.State.STARTED
                            )
                        ) {
                            callback[owner]!!.invoke()
                        }
                    }
                    launch {
                        _hasNetworkFlow.emit(true)
                    }
                }

                override fun onLost(network: Network) {
                    launch {
                        _hasNetworkFlow.emit(false)
                    }
                    isAvailable = false
                }
            }

            it.registerDefaultNetworkCallback(networkCallback)
        }
    }

    private fun getConnectivityStatus(): Int {
        val activeNetwork = connectivityManager.activeNetworkInfo
        if (null != activeNetwork) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) return TYPE_WIFI
            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) return TYPE_MOBILE
        }
        return TYPE_NOT_CONNECTED
    }

    fun getConnectivityStatusString(): Int {
        val conn = getConnectivityStatus()
        var status = 0
        when (conn) {
            TYPE_WIFI -> {
                status = NETWORK_STATUS_WIFI
            }

            TYPE_MOBILE -> {
                status = NETWORK_STATUS_MOBILE
            }

            TYPE_NOT_CONNECTED -> {
                status = NETWORK_STATUS_NOT_CONNECTED
            }
        }
        return status
    }

    @JvmStatic
    fun hasConnect() = getConnectivityStatus() != TYPE_NOT_CONNECTED

    private val callback = ConcurrentHashMap<LifecycleOwner, () -> Unit>()

    private val networkCallbacks = hashMapOf<Context, ConnectivityManager.NetworkCallback>()

    @JvmStatic
    fun observeNetwork(lifecycleOwner: LifecycleOwner, onHasNetwork: () -> Unit) {
        callback[lifecycleOwner] = onHasNetwork
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                callback.remove(owner)
            }

            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                if (hasConnect()) {
                    callback[owner]?.invoke()
                }
            }
        })
    }

    @JvmStatic
    fun unObserveNetwork(lifecycleOwner: LifecycleOwner) {
        callback.remove(lifecycleOwner)
    }
}

class NetworkStateLiveData(private val cm: ConnectivityManager) : LiveData<Boolean>() {
    constructor(application: Application) : this(
        application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    )

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            postValue(true)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            postValue(false)
        }
    }

    override fun onActive() {
        super.onActive()
        val request = NetworkRequest.Builder()
        cm.registerNetworkCallback(request.build(), networkCallback)
    }

    override fun onInactive() {
        super.onInactive()
        cm.unregisterNetworkCallback(networkCallback)
    }
}

fun Context.isConnection(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    } else {
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }
}