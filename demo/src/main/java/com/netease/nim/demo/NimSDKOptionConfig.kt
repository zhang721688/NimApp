package com.netease.nim.demo

import android.content.Context
import android.graphics.Color
import android.os.Environment
import android.text.TextUtils
import com.netease.nim.demo.DemoCache.context
import com.netease.nim.demo.DemoCache.notificationConfig
import com.netease.nim.demo.config.preference.UserPreferences
import com.netease.nim.demo.main.activity.WelcomeActivity
import com.netease.nimlib.sdk.NosTokenSceneConfig
import com.netease.nimlib.sdk.NotificationFoldStyle
import com.netease.nimlib.sdk.SDKOptions
import com.netease.nimlib.sdk.StatusBarNotificationConfig
import com.netease.nimlib.sdk.mixpush.MixPushConfig
import com.netease.nimlib.sdk.msg.MessageNotifierCustomization
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.zxn.netease.nimsdk.api.wrapper.MessageRevokeTip
import com.zxn.netease.nimsdk.api.wrapper.NimUserInfoProvider
import com.zxn.netease.nimsdk.business.session.viewholder.MsgViewHolderThumbBase
import java.io.IOException

/**
 * 云信sdk 自定义的SDK选项设置
 */
internal object NimSDKOptionConfig {
    fun getSDKOptions(context: Context): SDKOptions {
        val options = SDKOptions()
        // 如果将新消息通知提醒托管给SDK完成，需要添加以下配置。
        initStatusBarNotificationConfig(options)
        // 配置 APP 保存图片/语音/文件/log等数据的目录
        options.sdkStorageRootPath = getAppCacheDir(context) + "/nim" // 可以不设置，那么将采用默认路径
        // 配置是否需要预下载附件缩略图
        options.preloadAttach = true
        // 配置附件缩略图的尺寸大小
        options.thumbnailSize = MsgViewHolderThumbBase.getImageMaxEdge()
        // 通知栏显示用户昵称和头像
        options.userInfoProvider = NimUserInfoProvider(DemoCache.context)
        // 定制通知栏提醒文案（可选，如果不定制将采用SDK默认文案）
        options.messageNotifierCustomization = messageNotifierCustomization
        // 在线多端同步未读数
        options.sessionReadAck = true
        // 动图的缩略图直接下载原图
        options.animatedImageThumbnailEnabled = true
        // 采用异步加载SDK
        options.asyncInitSDK = true
        // 是否是弱IM场景
        options.reducedIM = false
        // 是否检查manifest 配置，调试阶段打开，调试通过之后请关掉
        options.checkManifestConfig = false
        // 是否启用群消息已读功能，默认关闭
        options.enableTeamMsgAck = true
        // 打开消息撤回未读数-1的开关
        options.shouldConsiderRevokedMessageUnreadCount = true
        // 云信私有化配置项
        configServerAddress(options, context)
        options.mixPushConfig = buildMixPushConfig()
        //        options.mNosTokenSceneConfig = createNosTokenScene();
        options.loginCustomTag = "登录自定义字段"
        options.useXLog = false
        // 会话置顶是否漫游
        options.notifyStickTopSession = true
        // 设置数据库加密秘钥（替换为自己的秘钥），开启加密；如不需要加密，则去掉
        options.databaseEncryptKey = "123456"
        return options
    }

    const val TEST_NOS_SCENE_KEY = "test_nos_scene_key"

    /**
     * nos 场景配置
     */
    private fun createNosTokenScene(): NosTokenSceneConfig {
        val nosTokenSceneConfig = NosTokenSceneConfig()
        nosTokenSceneConfig.updateDefaultIMSceneExpireTime(1)
        nosTokenSceneConfig.updateDefaultProfileSceneExpireTime(2)
        // scene key 建议常量化，这样使用起来比较方便
        nosTokenSceneConfig.appendCustomScene(TEST_NOS_SCENE_KEY, 4)
        return nosTokenSceneConfig
    }

    /**
     * 配置 APP 保存图片/语音/文件/log等数据的目录
     * 这里示例用SD卡的应用扩展存储目录
     */
    fun getAppCacheDir(context: Context): String? {
        var storageRootPath: String? = null
        try {
            // SD卡应用扩展存储区(APP卸载后，该目录下被清除，用户也可以在设置界面中手动清除)，请根据APP对数据缓存的重要性及生命周期来决定是否采用此缓存目录.
            // 该存储区在API 19以上不需要写权限，即可配置 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="18"/>
            if (context.externalCacheDir != null) {
                storageRootPath = context.externalCacheDir!!.canonicalPath
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (TextUtils.isEmpty(storageRootPath)) {
            // SD卡应用公共存储区(APP卸载后，该目录不会被清除，下载安装APP后，缓存数据依然可以被加载。SDK默认使用此目录)，该存储区域需要写权限!
            storageRootPath = Environment.getExternalStorageDirectory()
                .toString() + "/" + DemoCache.context!!.packageName
        }
        return storageRootPath
    }

    private fun configServerAddress(options: SDKOptions, context: Context) {
        val serverConfig = DemoPrivatizationConfig.getServerAddresses(context)
        if (serverConfig != null) {
            options.serverConfig = serverConfig
        }
        val appKey = DemoPrivatizationConfig.getAppKey(context)
        if (!TextUtils.isEmpty(appKey)) {
            options.appKey = appKey
        }
    }

    private fun initStatusBarNotificationConfig(options: SDKOptions) {
        // load 应用的状态栏配置
        val config = loadStatusBarNotificationConfig()
        // load 用户的 StatusBarNotificationConfig 设置项
        var userConfig = UserPreferences.getStatusConfig()
        if (userConfig == null) {
            userConfig = config
        } else {
            // 新增的 UserPreferences 存储项更新，兼容 3.4 及以前版本
            // 新增 notificationColor 存储，兼容3.6以前版本
            // APP默认 StatusBarNotificationConfig 配置修改后，使其生效
            userConfig.notificationEntrance = config.notificationEntrance
            userConfig.notificationFolded = config.notificationFolded
            userConfig.notificationFoldStyle = config.notificationFoldStyle
            userConfig.notificationColor = config.notificationColor
        }
        // 持久化生效
        UserPreferences.setStatusConfig(userConfig)
        // SDK statusBarNotificationConfig 生效
        options.statusBarNotificationConfig = userConfig
    }

    // 这里开发者可以自定义该应用初始的 StatusBarNotificationConfig
    private fun loadStatusBarNotificationConfig(): StatusBarNotificationConfig {
        val config = StatusBarNotificationConfig()
        // 点击通知需要跳转到的界面
        config.notificationEntrance = WelcomeActivity::class.java
        config.notificationSmallIconId = R.drawable.ic_stat_notify_msg
        config.notificationColor = context!!.resources.getColor(R.color.color_blue_3a9efb)
        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://com.netease.nim.demo/raw/msg"
        config.notificationFolded = true
        //        config.notificationFolded = false;
        config.notificationFoldStyle = NotificationFoldStyle.ALL
        config.downTimeEnableNotification = true
        // 呼吸灯配置
        config.ledARGB = Color.GREEN
        config.ledOnMs = 1000
        config.ledOffMs = 1500
        // 是否APP ICON显示未读数红点(Android O有效)
        config.showBadge = true
        // save cache，留做切换账号备用
        notificationConfig = config
        return config
    }

    private val messageNotifierCustomization: MessageNotifierCustomization =
        object : MessageNotifierCustomization {
            override fun makeNotifyContent(nick: String, message: IMMessage): String? {
                return null // 采用SDK默认文案
            }

            override fun makeTicker(nick: String, message: IMMessage): String? {
                return null // 采用SDK默认文案
            }

            override fun makeRevokeMsgTip(revokeAccount: String, item: IMMessage): String {
                return MessageRevokeTip.getRevokeTipContent(item, revokeAccount)
            }
        }

    private fun buildMixPushConfig(): MixPushConfig {
        // 第三方推送配置
        val config = MixPushConfig()
        // 小米推送
        config.xmAppId = "2882303761517521532"
        config.xmAppKey = "5541752152532"
        config.xmCertificateName = "DEMO_MI_PUSH"


        // 华为推送
        config.hwAppId = "101420927"
        config.hwCertificateName = "DEMO_HW_PUSH"


        // 魅族推送
        config.mzAppId = "111710"
        config.mzAppKey = "282bdd3a37ec4f898f47c5bbbf9d2369"
        config.mzCertificateName = "DEMO_MZ_PUSH"

        // fcm 推送，适用于海外用户，不使用fcm请不要配置
        config.fcmCertificateName = "DEMO_FCM_PUSH"


        // vivo推送
        config.vivoCertificateName = "DEMO_VIVO_PUSH"

        // oppo推送
        config.oppoAppId = "3477155"
        config.oppoAppKey = "6clw0ue1oZ8cCOogKg488o0os"
        config.oppoAppSercet = "e163705Bd018bABb3e2362C440A94673"
        config.oppoCertificateName = "DEMO_OPPO_PUSH"
        return config
    }
}