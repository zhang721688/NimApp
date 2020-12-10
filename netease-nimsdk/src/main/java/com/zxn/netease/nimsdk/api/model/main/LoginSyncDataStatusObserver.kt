package com.zxn.netease.nimsdk.api.model.main

import android.os.Handler
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.auth.AuthServiceObserver
import com.netease.nimlib.sdk.auth.constant.LoginSyncStatus
import com.zxn.netease.nimsdk.api.NimUIKit
import com.zxn.netease.nimsdk.common.util.log.LogUtil
import com.zxn.netease.nimsdk.common.util.log.sdk.wrapper.AbsNimLog
import java.util.*

/**
 * 登录
 */
class LoginSyncDataStatusObserver {
    private var uiHandler: Handler? = null
    private var timeoutRunnable: Runnable? = null

    /**
     * 状态
     */
    private var syncStatus = LoginSyncStatus.NO_BEGIN

    /**
     * 监听
     */
    private val observers: MutableList<Observer<Void?>> = ArrayList()

    /**
     * 注销时清除状态&监听
     */
    fun reset() {
        syncStatus = LoginSyncStatus.NO_BEGIN
        observers.clear()
    }

    /**
     * 在App启动时向SDK注册登录后同步数据过程状态的通知
     * 调用时机：主进程Application onCreate中
     */
    fun registerLoginSyncDataStatus(register: Boolean) {
        AbsNimLog.i(TAG, "observe login sync data completed event on Application create")
        NIMClient.getService(AuthServiceObserver::class.java)
            .observeLoginSyncDataStatus(loginSyncStatusObserver, register)
        NIMClient.getService(AuthServiceObserver::class.java)
            .observeLoginSyncTeamMembersCompleteResult(syncTeamMemberObserver, register)
    }

    private val loginSyncStatusObserver = Observer<LoginSyncStatus> { status ->
        syncStatus = status
        if (status == LoginSyncStatus.BEGIN_SYNC) {
            LogUtil.i(TAG, "login sync data begin")
        } else if (status == LoginSyncStatus.SYNC_COMPLETED) {
            LogUtil.i(TAG, "login sync data completed")
            onLoginSyncDataCompleted(false)
        }
    }
    private val syncTeamMemberObserver = Observer<Boolean> { result ->
        LogUtil.i(
            TAG,
            "login sync all team members result = $result"
        )
    }

    /**
     * 监听登录后同步数据完成事件，缓存构建完成后自动取消监听
     * 调用时机：登录成功后
     *
     * @param observer 观察者
     * @return 返回true表示数据同步已经完成或者不进行同步，返回false表示正在同步数据
     */
    fun observeSyncDataCompletedEvent(observer: Observer<Void?>): Boolean {
        if (syncStatus == LoginSyncStatus.NO_BEGIN || syncStatus == LoginSyncStatus.SYNC_COMPLETED) {
            /*
             * NO_BEGIN 如果登录后未开始同步数据，那么可能是自动登录的情况:
             * PUSH进程已经登录同步数据完成了，此时UI进程启动后并不知道，这里直接视为同步完成
             */
            return true
        }

        // 正在同步
        if (!observers.contains(observer)) {
            observers.add(observer)
        }

        // 超时定时器
        if (uiHandler == null) {
            uiHandler = Handler(NimUIKit.getContext().mainLooper)
        }
        if (timeoutRunnable == null) {
            timeoutRunnable = Runnable { // 如果超时还处于开始同步的状态，模拟结束
                if (syncStatus == LoginSyncStatus.BEGIN_SYNC) {
                    onLoginSyncDataCompleted(true)
                }
            }
        }
        uiHandler!!.removeCallbacks(timeoutRunnable!!)
        uiHandler!!.postDelayed(timeoutRunnable!!, (TIME_OUT_SECONDS * 1000).toLong())
        return false
    }

    /**
     * 登录同步数据完成处理
     */
    private fun onLoginSyncDataCompleted(timeout: Boolean) {
        LogUtil.i(TAG, "onLoginSyncDataCompleted, timeout=$timeout")

        // 移除超时任务（有可能完成包到来的时候，超时任务都还没创建）
        if (timeoutRunnable != null) {
            uiHandler!!.removeCallbacks(timeoutRunnable!!)
        }

        // 通知上层
        for (o in observers) {
            o.onEvent(null)
        }

        // 重置状态
        reset()
    }

    internal object InstanceHolder {
        val instance = LoginSyncDataStatusObserver()
    }

    companion object {
        private val TAG = LoginSyncDataStatusObserver::class.java.simpleName
        private const val TIME_OUT_SECONDS = 10

        /**
         * 单例
         */
        @JvmStatic
        val instance: LoginSyncDataStatusObserver
            get() = InstanceHolder.instance
    }
}