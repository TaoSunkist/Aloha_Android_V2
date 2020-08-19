package com.wealoha.social.inject;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

import android.content.Context;
import android.net.Uri;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.otto.Bus;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import com.wealoha.social.ConnectionChangeRecevier;
import com.wealoha.social.LocationActivity;
import com.wealoha.social.activity.AdvancedFeaturesAct;
import com.wealoha.social.activity.AlerPhoneNumActivity;
import com.wealoha.social.activity.CaptureActivity;
import com.wealoha.social.activity.ChangePasswordAct;
import com.wealoha.social.activity.ChatBigImgAct;
import com.wealoha.social.activity.ConfigDetailsAct;
import com.wealoha.social.activity.ConfigHaveInstagramConfig;
import com.wealoha.social.activity.ConfigInstagramAct;
import com.wealoha.social.activity.ConfigIntroductionAct;
import com.wealoha.social.activity.CropImageActivity;
import com.wealoha.social.activity.FaqStatmentAct;
import com.wealoha.social.activity.FeedCommentActivity;
import com.wealoha.social.activity.FeedNoticeAct;
import com.wealoha.social.activity.FilterSettingAct;
import com.wealoha.social.activity.FindYouAct;
import com.wealoha.social.activity.FragmentWrapperActivity;
import com.wealoha.social.activity.GDMapAct;
import com.wealoha.social.activity.ImgCropingActivity;
import com.wealoha.social.activity.InstagramWebViewAct;
import com.wealoha.social.activity.InvitationCodeAct;
import com.wealoha.social.activity.LauncherImgAct;
import com.wealoha.social.activity.LeaveCommentAct;
import com.wealoha.social.activity.LocationAct;
import com.wealoha.social.activity.LocationForFeedAct;
import com.wealoha.social.activity.LoginAct;
import com.wealoha.social.activity.MainAct;
import com.wealoha.social.activity.NaviIntroActivity;
import com.wealoha.social.activity.NewAlohaActivity;
import com.wealoha.social.activity.NewPasswordAct;
import com.wealoha.social.activity.OAuthSinaAct;
import com.wealoha.social.activity.PicSendActivity;
import com.wealoha.social.activity.ProFeatureAct;
import com.wealoha.social.activity.RegisterAct;
import com.wealoha.social.activity.StatementAct;
import com.wealoha.social.activity.TagFeedAct;
import com.wealoha.social.activity.UserDataAct;
import com.wealoha.social.activity.UserListActivity;
import com.wealoha.social.activity.VerifyAct;
import com.wealoha.social.activity.WebActivity;
import com.wealoha.social.activity.WebLaunchActivity;
import com.wealoha.social.activity.WelcomeAct;
import com.wealoha.social.adapter.ChatListAdapter;
import com.wealoha.social.adapter.ChatMsgViewAdapter;
import com.wealoha.social.adapter.CommentAdapter;
import com.wealoha.social.adapter.FeedCommentAdapter;
import com.wealoha.social.adapter.FeedLvAdapter;
import com.wealoha.social.adapter.FeedNoticeAdapter;
import com.wealoha.social.adapter.FindYouAdapter;
import com.wealoha.social.adapter.LocationAdapter;
import com.wealoha.social.adapter.LocationListAdapter;
import com.wealoha.social.adapter.NewAlohaAdapter;
import com.wealoha.social.adapter.Notify2Apapter;
import com.wealoha.social.adapter.ProfileListAdapter;
import com.wealoha.social.adapter.SwipeMenuAdapter;
import com.wealoha.social.adapter.UserListAdapter;
import com.wealoha.social.adapter.feed.Feed2Adapter;
import com.wealoha.social.adapter.feed.FeedHolder;
import com.wealoha.social.adapter.feed.ImageFeedHolder;
import com.wealoha.social.adapter.feed.VideoFeedHolder;
import com.wealoha.social.adapter.profile.Profile2Adapter;
import com.wealoha.social.adapter.profile.Profile2HeaderHolder;
import com.wealoha.social.adapter.profile.Profile2ImageHolder;
import com.wealoha.social.adapter.profile.Profile2ImagesAdapter;
import com.wealoha.social.adapter.profile.Profile2InfoHolder;
import com.wealoha.social.api.comment.Comment2API;
import com.wealoha.social.api.comment.service.Comment2Service;
import com.wealoha.social.api.feed.Feed2API;
import com.wealoha.social.api.feed.service.Feed2Service;
import com.wealoha.social.api.feed.service.PraisedPostService;
import com.wealoha.social.api.feed.service.SingletonFeedService;
import com.wealoha.social.api.feed.service.TagedPostService;
import com.wealoha.social.api.locationservice.LocationServiceAPI;
import com.wealoha.social.api.notify2.Json2ObjectByTypeSerializer;
import com.wealoha.social.api.notify2.Notify2API;
import com.wealoha.social.api.notify2.bean.Notify2Type;
import com.wealoha.social.api.notify2.dto.AbsNotify2DTO;
import com.wealoha.social.api.notify2.dto.NewAlohaNotify2DTO;
import com.wealoha.social.api.notify2.dto.PostCommentNotify2DTO;
import com.wealoha.social.api.notify2.dto.PostCommentReplyOnMyPost2DTO;
import com.wealoha.social.api.notify2.dto.PostCommentReplyOnOthersPost2DTO;
import com.wealoha.social.api.notify2.dto.PostLikeNotify2DTO;
import com.wealoha.social.api.notify2.dto.PostTagNotify2DTO;
import com.wealoha.social.api.notify2.service.Notify2Service;
import com.wealoha.social.api.notify2.service.UserListService;
import com.wealoha.social.api.post.PostAPI;
import com.wealoha.social.api.post.PostService;
import com.wealoha.social.api.post.TopicPostService;
import com.wealoha.social.api.privacy.UserSettingPrivacyAPI;
import com.wealoha.social.api.profile.service.Profile2Service;
import com.wealoha.social.api.topic.TopicAPI;
import com.wealoha.social.api.topic.TopicService;
import com.wealoha.social.api.user.User2API;
import com.wealoha.social.api.user.User2Service;
import com.wealoha.social.api.CommentService;
import com.wealoha.social.api.ConstantsService;
import com.wealoha.social.api.CountService;
import com.wealoha.social.api.FeedService;
import com.wealoha.social.api.InstagramService;
import com.wealoha.social.api.OauthService;
import com.wealoha.social.api.LocationService;
import com.wealoha.social.api.MatchService;
import com.wealoha.social.beans.message.ImageMessage;
import com.wealoha.social.beans.message.Message;
import com.wealoha.social.beans.message.MessageSerializer;
import com.wealoha.social.api.MessageService;
import com.wealoha.social.beans.message.TextMessage;
import com.wealoha.social.api.ClientLogService;
import com.wealoha.social.api.FindYouService;
import com.wealoha.social.api.SettingService;
import com.wealoha.social.api.AuthService;
import com.wealoha.social.api.ConnectService;
import com.wealoha.social.api.ProfileService;
import com.wealoha.social.api.UserPromotionService;
import com.wealoha.social.api.UserRegisterService;
import com.wealoha.social.api.UserService;
import com.wealoha.social.cache.ImageRender;
import com.wealoha.social.commons.CacheManager;
import com.wealoha.social.commons.CustomHttpDownloader;
import com.wealoha.social.commons.FileCacheManager;
import com.wealoha.social.commons.JsonController;
import com.wealoha.social.endpoint.ApiEndpoint;
import com.wealoha.social.endpoint.ApiEndpointSelector;
import com.wealoha.social.event.MainThreadBus;
import com.wealoha.social.fragment.AlohaFragment;
import com.wealoha.social.fragment.BlackListFragment;
import com.wealoha.social.fragment.ChangeNumberPart1;
import com.wealoha.social.fragment.ChangeNumberPart2;
import com.wealoha.social.fragment.ChangeNumberPart3;
import com.wealoha.social.fragment.ChatFragment;
import com.wealoha.social.fragment.Configfragment2;
import com.wealoha.social.fragment.CountryFragment;
import com.wealoha.social.fragment.FeedCommentFragment;
import com.wealoha.social.fragment.FeedFragment;
import com.wealoha.social.fragment.LoadingFragment;
import com.wealoha.social.fragment.PhoneAreaFragment;
import com.wealoha.social.fragment.PopularityLockFragment;
import com.wealoha.social.fragment.PostsListFragment;
import com.wealoha.social.fragment.PreferSettingNotificationFragment;
import com.wealoha.social.fragment.Profile2Fragment;
import com.wealoha.social.fragment.ProfileTestFragment;
import com.wealoha.social.fragment.SettingNotificationFragment;
import com.wealoha.social.fragment.SingletonFeedFragment;
import com.wealoha.social.fragment.SwipeMenuListFragment;
import com.wealoha.social.fragment.UserListFragment;
import com.wealoha.social.launch.LaunchBroadcastReceiver;
import com.wealoha.social.net.APIErrorHandler;
import com.wealoha.social.net.CustomGsonConverter;
import com.wealoha.social.presenters.AbsPresenter;
import com.wealoha.social.presenters.AttestationPresenter;
import com.wealoha.social.presenters.CheckPasswordPresenter;
import com.wealoha.social.presenters.ConfigPresenter;
import com.wealoha.social.presenters.FeedsPresenter;
import com.wealoha.social.presenters.PrivacyPresenter;
import com.wealoha.social.presenters.TopicDetailPresenter;
import com.wealoha.social.push.NoticeBarController;
import com.wealoha.social.push.XmPushBroadcast;
import com.wealoha.social.push.model.AlohaTimePush;
import com.wealoha.social.push.model.BasePush;
import com.wealoha.social.push.model.InboxMessageNewPush;
import com.wealoha.social.push.model.InstagramSyncPush;
import com.wealoha.social.push.model.NewHashtagPush;
import com.wealoha.social.push.model.NewMatchPush;
import com.wealoha.social.push.model.PostCommentPush;
import com.wealoha.social.push.model.PostCommentReplyOnMyPostPush;
import com.wealoha.social.push.model.PostCommentReplyOnOthersPostPush;
import com.wealoha.social.push.model.PostLikePush;
import com.wealoha.social.push.notification.AlohaPushNotification;
import com.wealoha.social.push.notification.AlohaTimeNotification;
import com.wealoha.social.push.notification.InboxMessageNewNotification;
import com.wealoha.social.push.notification.InstagramNotification;
import com.wealoha.social.push.notification.NewHashtagNotification;
import com.wealoha.social.push.notification.NewMatchNotification;
import com.wealoha.social.push.notification.Notification;
import com.wealoha.social.push.notification.NotificationDeserializer;
import com.wealoha.social.push.notification.PostCommentNotification;
import com.wealoha.social.push.notification.PostCommentReplyOnMyPostNotification;
import com.wealoha.social.push.notification.PostCommentReplyOnOthersPostNotification;
import com.wealoha.social.push.notification.PostLikeNotification;
import com.wealoha.social.render.BlurRenderTest;
import com.wealoha.social.render.BlurRendererLite;
import com.wealoha.social.services.AppLocationService;
import com.wealoha.social.store.PopupStore;
import com.wealoha.social.store.UserAgentProvider;
import com.wealoha.social.ui.attestation.AttestationActivity;
import com.wealoha.social.ui.config.ConfigFragment;
import com.wealoha.social.ui.dialogue.DialogueActivity;
import com.wealoha.social.ui.dialogue.DialoguePresenter;
import com.wealoha.social.ui.feeds.Feed2Fragment;
import com.wealoha.social.ui.feeds.Feed3Fragment;
import com.wealoha.social.ui.lock.CheckPasswordAct;
import com.wealoha.social.ui.lock.GestureLockAct;
import com.wealoha.social.ui.privacy.PrivacyActivity;
import com.wealoha.social.ui.topic.TopicDetailActivity;
import com.wealoha.social.ui.topic.TopicDetailAdapter;
import com.wealoha.social.utils.AMapUtil;
import com.wealoha.social.utils.ChatUtil;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FileTools;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.GuidUtil;
import com.wealoha.social.utils.PushUtil;
import com.wealoha.social.utils.RegionNodeUtil;
import com.wealoha.social.utils.RemoteLogUtil;
import com.wealoha.social.utils.StringUtil;
import com.wealoha.social.utils.Utils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.AtOnePopup2;
import com.wealoha.social.view.custom.MatchPopupWindow;
import com.wealoha.social.view.custom.ShareDialogFragment;
import com.wealoha.social.view.custom.WaterView;
import com.wealoha.social.view.custom.dialog.ListItemDialog;
import com.wealoha.social.view.custom.dialog.ReplyNoticeDialog;
import com.wealoha.social.view.custom.dialog.ReportBlackAlohaPopup;
import com.wealoha.social.view.custom.listitem.FeedItemHolder;
import com.wealoha.social.view.custom.listitem.ProfileHeaderHolder;
import com.wealoha.social.view.custom.listitem.ProfileImagesHolder;
import com.wealoha.social.view.custom.listitem.ProfileInfoHolder;
import com.wealoha.social.view.custom.listitem.UserListItem;
import com.wealoha.social.view.custom.popup.AtOnePopup;
import com.wealoha.social.view.custom.swipe.SwipeMenuListView;
import com.wealoha.social.widget.BaseListApiAdapter;
import com.wealoha.social.widget.feed.PostLayout;

import dagger.Module;
import dagger.Provides;

/**
 * Aloha的模块<br/>
 * <p>
 * 单例的都放在这里，使用 {@link Injector} 注入
 *
 * @author javamonk
 * @date 2014-10-28 下午3:26:04
 * @see
 * @since
 */
@Module(complete = false, library = true,
// 注入开始
// 需要注入的类放这里
        injects = { //

// Activity
                PrivacyActivity.class,//
                CheckPasswordAct.class,//
                GestureLockAct.class,//
                LauncherImgAct.class,//
                AttestationActivity.class,//
                FilterSettingAct.class,//
                TopicDetailActivity.class,//
                TopicDetailAdapter.class,//
                LoginAct.class, //
                MainAct.class, //
                RegisterAct.class, //
                WelcomeAct.class, //
                CropImageActivity.class, //
                VerifyAct.class, //
                NewAlohaActivity.class, UserDataAct.class, //
                WebActivity.class,//
                AdvancedFeaturesAct.class, //
                LeaveCommentAct.class,//
                PicSendActivity.class, //
                StatementAct.class, //
                NewPasswordAct.class,//
                ConfigDetailsAct.class,//
                ChangePasswordAct.class,//
                ConfigIntroductionAct.class,//
                NaviIntroActivity.class, LocationAct.class, LocationAdapter.class,//
                LocationListAdapter.class,//
                InvitationCodeAct.class,//
                UserListActivity.class, ConfigInstagramAct.class,//
                FeedCommentActivity.class,
// BlackListFragment.class,//
                FragmentWrapperActivity.class, //
                AlerPhoneNumActivity.class,//
                OAuthSinaAct.class, //
                ChatBigImgAct.class,//
                InstagramWebViewAct.class,//
                NewAlohaActivity.class,

                ConfigHaveInstagramConfig.class,//
                LocationForFeedAct.class,//
                FindYouAct.class,//
                LocationForFeedAct.class,//
                GDMapAct.class,//
                PreferSettingNotificationFragment.class,//

                DialogueActivity.class, //
                TagFeedAct.class,//
                ProFeatureAct.class,//
                WebLaunchActivity.class,//

// Fragment
                LoadingFragment.class,//
                ChatFragment.class, //
                FeedFragment.class, //
                AlohaFragment.class, //
// ProUserInfoFragment.class, //
                PhoneAreaFragment.class, //
                ConfigFragment.class, //
// ProUserInfoFragment.class,//
                CountryFragment.class,//
                SettingNotificationFragment.class,//
                Feed2Fragment.class,//
                SwipeMenuListFragment.class,//
                FaqStatmentAct.class,//
                DialogueActivity.class,//
                LocationActivity.class,//
                FeedNoticeAct.class, ProfileTestFragment.class,//
                ImgCropingActivity.class, //
                PopularityLockFragment.class,//
                UserListFragment.class,//
                Configfragment2.class,//
                Profile2Fragment.class,//
                SingletonFeedFragment.class,//
                FeedCommentFragment.class,//
                ChangeNumberPart1.class,//
                ChangeNumberPart2.class,//
                ChangeNumberPart3.class,//
                PostsListFragment.class,//
// Adapter
                CommentAdapter.class, FeedLvAdapter.class, //
                FeedCommentAdapter.class, ChatListAdapter.class, //
                ChatMsgViewAdapter.class,//
                BlackListFragment.class,//
                ShareDialogFragment.class,//
                NewAlohaAdapter.class,//
                ProfileListAdapter.class,//
                FeedNoticeAdapter.class,//
                ProfileInfoHolder.class,//
                SwipeMenuAdapter.class,//
                FindYouAdapter.class,//
                UserListAdapter.class,//
                Profile2Adapter.class,//
                Profile2Service.class,//
                Profile2ImagesAdapter.class,//
                Profile2InfoHolder.class, TopicService.class,//
// ProfileArrayAdaper.class,//
                TopicPostService.class,//
                Notify2Service.class, // 通知2
                Feed2Service.class, // 通知2
                Comment2Service.class, UserListService.class,//
                LocationService.class,//
                AppLocationService.class,//
                TagedPostService.class,//
                PraisedPostService.class,//
// 其它c
                RegionNodeUtil.class, //
                JsonController.class, //
                PushUtil.class, //
                BasePush.class,//
                PostCommentPush.class,//
                PostCommentReplyOnMyPostPush.class,//
                PostCommentReplyOnOthersPostPush.class,//
                AlohaTimePush.class,//
                NewHashtagPush.class,//
                InstagramSyncPush.class,//
                PostLikePush.class,//
                NewMatchPush.class,//
                InboxMessageNewPush.class,//
                MatchPopupWindow.class,//
                XmPushBroadcast.class, NoticeBarController.class,//
                ProfileHeaderHolder.class,//
                VideoFeedHolder.class, FeedItemHolder.class,//
                ProfileImagesHolder.class,//
                UserListItem.class,//
                WaterView.class,//
                SwipeMenuListView.class,//
                ConnectionChangeRecevier.class,//
                BaseListApiAdapter.class, //
                Notify2Apapter.class, //
                Feed2Adapter.class,//
                BlurRenderTest.class,//
                GuidUtil.class, //
                ContextUtil.class, //
                UserAgentProvider.class, //
                ChatUtil.class, //
                ReportBlackAlohaPopup.class,//
                FontUtil.class,//
                RemoteLogUtil.class, //
                ApiEndpointSelector.class, //
                ImageRender.class, //
                BlurRendererLite.class, //
                ReplyNoticeDialog.class,//
                LaunchBroadcastReceiver.class,//
                ListItemDialog.class,//
                CaptureActivity.class, PopupStore.class,//
                AtOnePopup.class,//
                AtOnePopup2.class,//
                FeedHolder.class,//
                Profile2ImageHolder.class,//
                Profile2Service.class, //
                Profile2HeaderHolder.class, //
                User2Service.class,//
                SingletonFeedService.class,//
                ImageFeedHolder.class, //
                VideoFeedHolder.class,//
                AMapUtil.class,//
                AbsPresenter.class,//
                PrivacyPresenter.class,//
                FeedsPresenter.class,//
                TopicDetailPresenter.class, //
                CheckPasswordPresenter.class,//
                ConfigPresenter.class,//
                DialoguePresenter.class,//
                AttestationPresenter.class,//
                com.wealoha.social.api.common.service.AbsBaseService.class,//
                PostService.class,//
                PostLayout.class,//
                Feed3Fragment.class,//
        })
public class AlohaModule {

    private final String TAG = AlohaModule.class.getSimpleName();

    // @Provides
    // @Singleton
    // public ImageUtil provideImageUtil() {
    // return new ImageUtil();
    // }

    @Provides
    @Singleton
    public RegionNodeUtil provideRegionNodeUtil() {
        return new RegionNodeUtil();
    }

    @Provides
    @Singleton
    public ContextUtil provideContextUtil() {
        return new ContextUtil();
    }

    @Provides
    @Singleton
    LruCache provideLruCache() {
        return new LruCache((int) Runtime.getRuntime().maxMemory() / 6);
    }

    @Provides
    @Singleton
    RemoteLogUtil provideRemoteLogUtil() {
        return new RemoteLogUtil();
    }

    @Provides
    @Singleton
    Picasso providePicasso(final Context context, LruCache lrucache, //
                           final RemoteLogUtil remoteLogUtil, final ContextUtil contextUtil, //
                           final UserAgentProvider userAgentProvider) {

        boolean curlSupported = false;

        Downloader theDownloader = null;
        if (curlSupported) {
            final boolean useProxy = isSystemProxyOK();

            // downloader
            // PicassoCurlDownloader
            try {
                // 优先使用sd卡
                // <sd card>/aloha/cache
                File cacheParent = new File(FileTools.getExternalStorage(remoteLogUtil), "aloha");

            } catch (IOException e1) {
                XL.w(TAG, "无法存取本地sd卡路径, 使用较小的内部存储");
                File cacheDir = context.getFileStreamPath("image_cache");

            }

        } else {
            try {
                theDownloader = new CustomHttpDownloader(new File( //
                        FileTools.getExternalStorage(remoteLogUtil), "aloha"), contextUtil);
            } catch (Exception e) {
                XL.w(TAG, "存储不可用", e);
                XL.w(TAG, "无法存取本地sd卡路径, 使用较小的内部存储");
                File cacheDir = context.getFileStreamPath("image_cache");
                theDownloader = new CustomHttpDownloader(cacheDir, contextUtil);
            }
        }

        return new Picasso.Builder(context)//
                .listener(new Picasso.Listener() {

                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                        // 通用的错误处理
                        XL.d(TAG, "fail to load: " + uri.toString(), e);
                        // Crashlytics.log(Log.ERROR, TAG, e.getMessage());
                        if (!(e instanceof IOException)) {
                            remoteLogUtil.log("PICASSO_LISTENER--URL:" + uri.toString(), e);
                        }
                    }
                }) //
                .memoryCache(lrucache) //
                 .indicatorsEnabled(true) // 调试用角标
                .downloader(theDownloader) //
                .build();
    }

    @Provides
    @Singleton
    MessageSerializer provideContentDeserializer() {
        MessageSerializer deserializer = new MessageSerializer();
        // 注册各种消息的反序列化器
        XL.d(TAG, "注册反序列化Message的类型");
        deserializer.registerContentType(TextMessage.TYPE, TextMessage.class);
        deserializer.registerContentType(ImageMessage.TYPE, ImageMessage.class);
        // deserializer.registerContentType(AudioMessage.TYPE,
        // AudioMessage.class);
        // deserializer.registerContentType(EmotionMessage.TYPE,
        // EmotionMessage.class);
        // deserializer.registerContentType(LbsMessage.TYPE, LbsMessage.class);
        return deserializer;
    }

    @Provides
    @Singleton
    NotificationDeserializer provideNotificationDeserializer() {
        NotificationDeserializer deserializer = new NotificationDeserializer();
        // 注册各种Push消息的反序列化器
        XL.d(TAG, "注册反序列化Notification的类型");
        deserializer.registerContentType(InboxMessageNewNotification.TYPE, InboxMessageNewNotification.class);
        deserializer.registerContentType(NewMatchNotification.TYPE, NewMatchNotification.class);
        deserializer.registerContentType(PostLikeNotification.TYPE, PostLikeNotification.class);
        deserializer.registerContentType(PostCommentNotification.TYPE, PostCommentNotification.class);
        deserializer.registerContentType(InstagramNotification.TYPE, InstagramNotification.class);
        deserializer.registerContentType(AlohaPushNotification.TYPE, AlohaPushNotification.class);
        deserializer.registerContentType(AlohaTimeNotification.TYPE, AlohaTimeNotification.class);
        deserializer.registerContentType(NewHashtagNotification.TYPE, NewHashtagNotification.class);
        deserializer.registerContentType(PostCommentReplyOnOthersPostNotification.TYPE, PostCommentReplyOnOthersPostNotification.class);
        deserializer.registerContentType(PostCommentReplyOnMyPostNotification.TYPE, PostCommentReplyOnMyPostNotification.class);
        // deserializer.registerContentType(Notification.TYPE,Notification.class);
        return deserializer;
    }

    @Provides
    @Singleton
    Gson provideGson(MessageSerializer messageDeserializer, //
                     NotificationDeserializer notificationDeserializer //
    ) {
        GsonBuilder gb = new GsonBuilder();
        XL.d(TAG, "注册含有子类的反序列化");
        gb.registerTypeAdapter(Message.class, messageDeserializer);
        gb.registerTypeAdapter(Notification.class, notificationDeserializer);

        // Notify2的反序列化器
        Json2ObjectByTypeSerializer notify2Deserializer = new Json2ObjectByTypeSerializer();
        notify2Deserializer.registerContentType(Notify2Type.NewAloha.getValue(), NewAlohaNotify2DTO.class);
        notify2Deserializer.registerContentType(Notify2Type.PostComment.getValue(), PostCommentNotify2DTO.class);
        notify2Deserializer.registerContentType(Notify2Type.PostLike.getValue(), PostLikeNotify2DTO.class);
        notify2Deserializer.registerContentType(Notify2Type.PostTag.getValue(), PostTagNotify2DTO.class);
        notify2Deserializer.registerContentType(Notify2Type.PostCommentReplyOnOthersPost.getValue(), PostCommentReplyOnOthersPost2DTO.class);
        notify2Deserializer.registerContentType(Notify2Type.PostCommentReplyOnMyPost.getValue(), PostCommentReplyOnMyPost2DTO.class);
        gb.registerTypeAdapter(AbsNotify2DTO.class, notify2Deserializer);
        return gb.create();
    }

    @Provides
    @Singleton
    PushUtil providePushUtil() {
        return new PushUtil();
    }

    @Provides
    @Singleton
    GuidUtil provideGuidUtil() {
        return new GuidUtil();
    }

    @Provides
    @Singleton
    CacheManager provideCacheManager(Context context, Gson gson) {
        return new FileCacheManager(context, gson);
    }

    @Provides
    @Singleton
    UserAgentProvider provideUserAgentProvider() {
        return new UserAgentProvider();
    }

    /**
     * retrofit使用
     *
     * @param context
     * @param userAgentProvider
     * @return
     */
    @Provides
    @Singleton
    RequestInterceptor provideRequestInterceptor(final Context context, //
                                                 final UserAgentProvider userAgentProvider, //
                                                 final GuidUtil guidUtil, final ContextUtil contextUtil, final ApiEndpoint apiEndpoint) {
        return new RequestInterceptor() {

            @Override
            public void intercept(RequestFacade request) {
                // 注意！！！这里修改完修改要改 ContextUtil.addGeneralHttpHeaders() 保持一致
                // request.addHeader("Accept-Encoding", "gzip");
                // request.addHeader("Accept",
                // GlobalConstants.Http.CONTENT_TYPE_JSON);
                // request.addHeader("Accept-Language",
                // userAgentProvider.getAcceptLanguage());
                // request.addHeader("Content-Type",
                // Constants.Http.CONTENT_TYPE_JSON);
                // Retrofit 给gzip识别用
                request.addHeader("User-Agent", userAgentProvider.getUserAgent() + " Retrofit");
                request.addHeader("User-Device-Id", guidUtil.getGuid());
                request.addHeader("Accept-Language", Utils.getLocaleLanguage());
                // 设备信息
                request.addHeader("User-Device", userAgentProvider.getDevice());

                // 可以访问和不可以访问的url
                if (CollectionUtils.isNotEmpty(apiEndpoint.getUrlOk())) {
                    request.addHeader("X-Url-Ok", StringUtils.join(apiEndpoint.getUrlOk(), " "));
                }
                if (CollectionUtils.isNotEmpty(apiEndpoint.getUrlFail())) {
                    request.addHeader("X-Url-Fail", StringUtils.join(apiEndpoint.getUrlFail(), " "));
                }
                // Store
                String androidStore = userAgentProvider.getAndroidStore();
                if (StringUtil.isNotEmpty(androidStore)) {
                    request.addHeader("Android-Store", androidStore);
                }
                // ticket
                String t = contextUtil.getCurrentTicket();
                if (t != null) {
                    request.addHeader("Cookie", "t=" + t);
                }
            }
        };
    }

    private boolean isCurlSupported() {
        try {
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    private Boolean systemProxyOk = true;

    /**
     * 测试代理
     *
     * @return
     */
    private boolean isSystemProxyOK() {
        if (systemProxyOk == null) {
            try {
            } catch (Throwable e) {
                XL.w(TAG, "测试系统代理失败", e);
                systemProxyOk = false;
            }
        }

        return systemProxyOk;
    }

    /**
     * retrofit使用
     *
     * @param requestInterceptor
     * @param gson
     * @return
     */
    @Provides
    @Singleton
    RestAdapter provideRestAdapter(RequestInterceptor requestInterceptor, Gson gson, Context context, Bus bus, ApiEndpoint endpoint) {
        // boolean curlSupported = isCurlSupported();
        // Client client;
        // if (curlSupported) {
        // final boolean useProxy = isSystemProxyOK();
        // client = new RetrofitCurlClient() //
        // .curlCalback(new CurlHttpCallback() {
        //
        // @Override
        // public void afterInit(CurlHttp curlHttp, String url) {
        // curlHttp //
        // .setConnectionTimeoutMillis(20 * 1000) //
        // .setTimeoutMillis(60 * 1000) //
        // .useSystemProxy(useProxy) //
        // .addHeader("Accept-Encoding", "gzip");
        // }
        //
        // });
        // } else {
        // OkHttpClient okClient = new OkHttpClient();
        // okClient.setConnectTimeout(15000, TimeUnit.MILLISECONDS);
        // okClient.setReadTimeout(20000, TimeUnit.MILLISECONDS);
        // // 不使用系统代理
        // //
        // https://www.crashlytics.com/irainbow-inc/android/apps/com.wealoha.social/issues/551551965141dcfd8f412ad6
        // okClient.setProxy(Proxy.NO_PROXY);
        // client = new OkClient(okClient);
        // }

        return new RestAdapter.Builder()
                // .setClient(new AndroidApacheClient()) // Issues:
                // https://github.com/square/retrofit/issues/454
                // .setClient() //
                .setClient(new OkClient())
                // .setEndpoint(GlobalConstants.ServerUrl.SERVER_URL)
                .setEndpoint(endpoint) // 定制入口
                .setRequestInterceptor(requestInterceptor) //
                // 输出log
                .setLogLevel(RestAdapter.LogLevel.FULL) //
                .setErrorHandler(new APIErrorHandler(context)) //
                .setConverter(new CustomGsonConverter(gson, bus)).build();
    }

    @Provides
    @Singleton
    ApiEndpoint provideApiEndpoint() {
        return new ApiEndpoint();
    }

    @Provides
    @Singleton
    UserPromotionService userPromotionService(RestAdapter restAdapter) {
        return restAdapter.create(UserPromotionService.class);
    }

    @Provides
    @Singleton
    SettingService settingService(RestAdapter restAdapter) {
        return restAdapter.create(SettingService.class);
    }

    @Provides
    @Singleton
    MatchService provideMatchService(RestAdapter restAdapter) {
        return restAdapter.create(MatchService.class);
    }

    @Provides
    @Singleton
    UserRegisterService provideUserRegisterService(RestAdapter restAdapter) {
        return restAdapter.create(UserRegisterService.class);
    }

    @Provides
    @Singleton
    AuthService provideAuthService(RestAdapter restAdapter) {
        return restAdapter.create(AuthService.class);
    }

    @Provides
    @Singleton
    ConnectService provideConnectService(RestAdapter restAdapter) {
        return restAdapter.create(ConnectService.class);
    }

    @Provides
    @Singleton
    MessageService provideMessageService(RestAdapter restAdapter) {
        return restAdapter.create(MessageService.class);
    }

    @Provides
    @Singleton
    UserService provideUserService(RestAdapter restAdapter) {
        return restAdapter.create(UserService.class);
    }

    @Provides
    @Singleton
    CountService proviceCountService(RestAdapter restAdapter) {
        return restAdapter.create(CountService.class);
    }

    @Provides
    @Singleton
    ImageRender provideImageRender(Picasso picasso) {
        return new ImageRender();
    }

    @Provides
    @Singleton
    CommentService provideImageRender(RestAdapter restAdapter) {
        return restAdapter.create(CommentService.class);
    }

    @Provides
    @Singleton
    ClientLogService provideClientLogService(RestAdapter restAdapter) {
        return restAdapter.create(ClientLogService.class);
    }

    @Provides
    @Singleton
    FindYouService provideFindYouService(RestAdapter restAdapter) {
        return restAdapter.create(FindYouService.class);
    }

    @Provides
    @Singleton
    ChatUtil provideChatUtil() {
        return new ChatUtil();
    }

    @Provides
    @Singleton
    FontUtil provideFontUtil(Context context) {
        return new FontUtil(context);
    }

    @Provides
    @Singleton
    BlurRendererLite provideBlurRendererLite() {
        return new BlurRendererLite();
    }

    @Provides
    @Singleton
    ConstantsService provideConstantsService(RestAdapter restAdapter) {
        return restAdapter.create(ConstantsService.class);
    }

    @Provides
    @Singleton
    ProfileService provideProfileService(RestAdapter restAdapter) {
        return restAdapter.create(ProfileService.class);
    }

    @Provides
    @Singleton
    OauthService provideOauthService() {
        Gson gson = new GsonBuilder() //
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES) //
                // .registerTypeAdapter(Date.class, new DateTypeAdapter()) //
                .create();
        RestAdapter restAdapter = new RestAdapter.Builder() //
                .setEndpoint("https://api.instagram.com") //
                .setConverter(new GsonConverter(gson, "UTF-8")) //
                // .setLog(logger) //
                .setClient(new OkClient())//
                .setLogLevel(LogLevel.FULL) //
                .build();

        return restAdapter.create(OauthService.class);
    }

    @Provides
    @Singleton
    InstagramService provideInstagramService(RestAdapter restAdapter) {
        return restAdapter.create(InstagramService.class);
    }

    @Provides
    @Singleton
    LocationService provideLocationService(RestAdapter restAdapter) {
        return restAdapter.create(LocationService.class);
    }

    @Provides
    @Singleton
    Notify2API provideNotify2API(RestAdapter restAdapter) {
        return restAdapter.create(Notify2API.class);
    }

    @Provides
    @Singleton
    Comment2API provideComment2API(RestAdapter restAdapter) {
        return restAdapter.create(Comment2API.class);
    }

    /**
     * 事件总线
     *
     * @return
     */
    @Singleton
    @Provides
    Bus provideOttoBus() {
        return new MainThreadBus();
    }

    @Provides
    @Singleton
    FeedService provideFeedService(RestAdapter restAdapter) {
        return restAdapter.create(FeedService.class);
    }

    @Provides
    @Singleton
    Feed2API provideFeed2API(RestAdapter restAdapter) {
        return restAdapter.create(Feed2API.class);
    }

    @Provides
    @Singleton
    LocationServiceAPI provideLocationServiceAPI(RestAdapter restAdapter) {
        return restAdapter.create(LocationServiceAPI.class);
    }

    @Provides
    @Singleton
    User2API provideUser2API(RestAdapter restAdapter) {
        return restAdapter.create(User2API.class);
    }

    @Provides
    @Singleton
    UserSettingPrivacyAPI provideUserSettingPrivacyAPI(RestAdapter restAdapter) {
        return restAdapter.create(UserSettingPrivacyAPI.class);
    }

    @Provides
    @Singleton
    TopicAPI provideTopicAPI(RestAdapter restAdapter) {
        return restAdapter.create(TopicAPI.class);
    }

    @Provides
    @Singleton
    PostAPI providePostAPI(RestAdapter restAdapter) {
        return restAdapter.create(PostAPI.class);
    }
}
