package com.wealoha.social.fragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.InjectView;

import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.squareup.picasso.Picasso;
import com.wealoha.social.R;
import com.wealoha.social.beans.BlackListResult;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.User;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.commons.JsonController;
import com.wealoha.social.store.SyncEntProtocol;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.CropMode;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.view.custom.CircleImageView;
import com.wealoha.social.view.custom.SlideView;
import com.wealoha.social.view.custom.SlideView.OnSlideListener;

public class BlackListFragment extends BaseFragment implements OnSlideListener, OnClickListener {

    @Inject
    Picasso picasso;
    @Inject
    ContextUtil contextUtil;

    @InjectView(R.id.black_content_lv)
    ListView mSlideList;

    private List<User> mBTUsers;

    private BlackListAdapter mBlackAdapter;
    // 上次处于打开状态的SlideView,滑动控件必须变量
    private SlideView mLastSlideViewWithStatusOn;

    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        return inflater.inflate(R.layout.frag_black_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        initData();
        initView();
    }

    /*
     * @Override protected void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState);
     * setContentView(R.layout.frag_black_list); mContext = this; initData(); initView(); }
     */

    public void initData() {
        RequestParams params = new RequestParams();
        contextUtil.addGeneralHttpHeaders(params);
        SyncEntProtocol.getInstance().send(HttpMethod.GET, GlobalConstants.ServerUrl.GET_BLACKLIST, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {

                ToastUtil.shortToast(mContext, getString(R.string.network_error));
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {

                Result<BlackListResult> result = JsonController.parseJson(arg0.result, new TypeToken<Result<BlackListResult>>() {
                }.getType());
                Log.i("DELETE_COMMENT", arg0.result);
                if (result.isOk()) {
                    // result中还有nextcursorid,这里先只用comments和userMap
                    mBTUsers = result.getData().list;
                    // 更新
                    if (result.getData().list.size() > 0) {
                        refreshView();
                    }

                }

            }
        });
    }

    public void initView() {

    }

    public void refreshView() {
        mSlideList.setClickable(false);
        // 没有添加下拉刷新
        if (mBlackAdapter == null) {
            mBlackAdapter = new BlackListAdapter();
            mSlideList.setAdapter(mBlackAdapter);
        } else {
            mBlackAdapter.notifyDataSetChanged();
        }
    }

    class BlackListAdapter extends BaseAdapter {

        private List<SlideView> slideViewList;

        public BlackListAdapter() {
            slideViewList = new ArrayList<SlideView>();
            // 填充slideViewlist，暂时这样
            for (int i = 0; i < mBTUsers.size(); i++) {
                slideViewList.add(new SlideView(mContext));
            }
            // ToastUtil.shortToast(context, "new ");
        }

        // 重写
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            slideViewList = new ArrayList<SlideView>();
            // 填充slideViewlist，暂时这样
            for (int i = 0; i < mBTUsers.size(); i++) {
                slideViewList.add(new SlideView(mContext));
            }
        }

        @Override
        public int getCount() {

            return mBTUsers.size();
        }

        @Override
        public Object getItem(int position) {

            // 返回slideview，触发滑动事件
            return slideViewList.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // 滑动删除控件
            SlideView slideView = (SlideView) convertView;
            ViewHolder viewHolder = null;
            if (slideView == null) {
                // 加载评论的控件组
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_black_list, null);

                slideView = new SlideView(mContext);

                // 装入可滑动控件中
                slideView.setContentView(view);
                viewHolder = new ViewHolder(slideView);
                // 设置滑动监听器
                slideView.setOnSlideListener(BlackListFragment.this);
                // 缓存
                slideView.setTag(viewHolder);

            } else {
                // 获得缓存
                viewHolder = (ViewHolder) slideView.getTag();
            }
            // 确认默认状态
            slideView.shrink();
            int i = mBTUsers.size() - 1 - position;// 倒序，最新消息显示在最上面
            picasso.load(ImageUtil.getImageUrl(mBTUsers.get(i).getAvatarImage().getId(), viewHolder.userPhoto.getWidth(), CropMode.ScaleCenterCrop)).placeholder(R.drawable.myotee2).into(viewHolder.userPhoto);
            viewHolder.userName.setText(mBTUsers.get(i).getName());
            viewHolder.delete.setOnClickListener(BlackListFragment.this);

            // 在listview中 同过getItem可以的到当前条目的slideview，触发滑动事件
            slideViewList.set(position, slideView);
            return slideView;
        }

    }

    @Override
    public void onSlide(View view, int status) {
        Log.i("ALOHA_BALCK", "SLIDE");

        // 如果当前存在已经打开的SlideView，那么将其关闭
        if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
            mLastSlideViewWithStatusOn.shrink();
        }
        // 记录本次处于打开状态的view
        if (status == SLIDE_STATUS_ON) {
            mLastSlideViewWithStatusOn = (SlideView) view;
        }
    }

    @Override
    public void onClick(View v) {
        // mContext
        int position = mSlideList.getPositionForView(v);
        Log.i("DELETE_COMMENT", mBTUsers.get(position).getId() + "");
        // ToastUtil.shortToast(mContext, position + "");
        if (position != ListView.INVALID_POSITION) {
            // 因为显示的时候是倒序，所以要计算正确的position
            position = mBTUsers.size() - position - 1;
            removeBTUser(mBTUsers.get(position).getId());
            mBTUsers.remove(position);
        }
    }

    public void removeBTUser(String userid) {
        RequestParams params = new RequestParams();
        contextUtil.addGeneralHttpHeaders(params);
        SyncEntProtocol.getInstance().send(HttpMethod.GET, GlobalConstants.ServerUrl.REMOVE_BLACKLIST, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {

                ToastUtil.shortToast(mContext, getString(R.string.network_error));
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {

                Result<ResultData> result = JsonController.parseJson(arg0.result, new TypeToken<Result<ResultData>>() {
                }.getType());
                if (result.isOk()) {
                    // ToastUtil.shortToast(context,
                    // getString(R.string.network_error));
                    Log.i("ALOHA_REMOVE_BTUSER", "SUCCESS");
                }

            }
        });
    }

    static class ViewHolder {

        CircleImageView userPhoto;
        TextView userName;
        RelativeLayout delete;

        public ViewHolder(View view) {
            userPhoto = (CircleImageView) view.findViewById(R.id.black_user_photo);
            userName = (TextView) view.findViewById(R.id.black_user_name);
            // slideView布局中的删除控件
            delete = (RelativeLayout) view.findViewById(R.id.holder);
        }

    }
}
