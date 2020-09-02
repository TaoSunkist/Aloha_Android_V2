package com.wealoha.social.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.squareup.picasso.Picasso;
import com.wealoha.social.adapter.feed.AbsViewHolder;
import com.wealoha.social.beans.ApiErrorCode;
import com.wealoha.social.api.BaseListApiService;
import com.wealoha.social.api.BaseListApiService.AdapterListDataCallback;
import com.wealoha.social.api.BaseListApiService.ListContextCallback;
import com.wealoha.social.beans.Direct;
import com.wealoha.social.beans.AbsNotify2;
import com.wealoha.social.beans.Notify2;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.XL;

/**
 * 自动和API交互的Adapter<br/>
 * <p>
 * E: 数据类型 P: 参数类型(可能没有)
 *
 * @author javamonk
 * @createTime 2015年3月4日 上午10:55:43
 */
public abstract class BaseListApiAdapter<E, P> extends BaseAdapter implements AdapterListDataCallback<E> {

    private static final String TAG = BaseListApiAdapter.class.getSimpleName();

    private final BaseListApiService<E, P> service;

    @Inject
    LayoutInflater inflater;

    @Inject
    Context context;

    protected final List<E> listData;

    private String earlyCursorId="";

    // 向上翻的状态
    private String lateCursorId="";

    private Object lock = new Object();

    private boolean loading;

    private boolean reachHead;
    private boolean reachEnd;

    private int dataVersion = 0;
    /**
     * 是否进行延时重置
     */
    private boolean resetDelay;

    protected BaseListApiAdapter(BaseListApiService<E, P> service) {
        this.service = service;
        this.listData = new ArrayList<E>();
        Injector.inject(this);
        this.service.setAdapterListCallback(this);
    }

    /**
     * 根据指定的type创建一个新的 {@link BaseAdapterHolder}
     * <p>
     * type
     * 可能为null, 仅当 {@link #isMultiListView()} 为true类型的视图才有值
     * item
     * inflater
     * parent
     *
     * @return
     */
    protected abstract AbsViewHolder newViewHolder(MultiListViewType type, E item, LayoutInflater inflater, ViewGroup parent);

    /**
     * 填充数据
     * <p>
     * holder
     * item
     */
    protected abstract <V extends AbsViewHolder> void fillView(V holder, E item, int position, View convertView);

    // ///// 以下是数据加载相关代码

    /**
     * 加载New的回调
     *
     * @author javamonk
     * @createTime 2015年3月6日 下午12:38:20
     */
    public static interface LoadNewCallback {

        /**
         * 加载成功
         * <p>
         * hasNew
         * 是否有新的
         */
        public void success(boolean hasNew);

        /**
         * 加载失败
         * <p>
         * code
         * 错误
         * exception
         * 异常，可能为空
         */
        public void fail(ApiErrorCode code, Exception exception);
    }

    /**
     * 尝试从头加载新的，如果有新数据，更新，如果没有新数据不动作<br/>
     *
     * <span style="color:red">注意！！使用这个方法的对象，一定要实现 {@link Object#hashCode()} {@link Object#equals(Object)}，参见
     * {@link AbsNotify2} </span>
     */
    public void tryLoadNew(int pageSize, P param, final LoadNewCallback callback) {
        XL.d("NotifyCount", "" + pageSize);
        // TODO 增加callback
        final int version = ++dataVersion;
        if (version != dataVersion) {
            // 如果加载过程中数据被更新了，就丢弃(比如reset了，但是下一页还在加载中)
            XL.w(TAG, "丢弃不一致的数据，期待版本: " + version + ", 当前版本: " + dataVersion);
            return;
        }
        // 使用desc，从头开始
        service.getList("", pageSize, Direct.Early, param, new BaseListApiService.ApiListCallback<E>() {

            @Override
            public void success(@NotNull List<? extends E> list, @NotNull String nextCursorId) {
                if (CollectionUtils.isEmpty(list)) {
                    callback.success(false);
                    return;
                }
                if (service.needReverse()) {
                    List<E> tmp = new ArrayList<E>();
                    tmp.addAll(list);
                    Collections.reverse(tmp);
                    list = tmp;
                }
                synchronized (listData) {
                    listData.addAll(0, list);
                    callback.success(list.size() > 0);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void fail(ApiErrorCode code, Exception exception) {
                callback.fail(code, exception);
            }
        });
    }

    /**
     * 加载下一页的回调
     *
     * @author javamonk
     * @createTime 2015年3月6日 下午12:37:39
     */
    public static interface LoadCallback {

        /**
         * 加载成功
         * <p>
         * hasPrev
         * 是否还有上一页
         * hasNext
         * 是否还有下一页
         */
        public void success(boolean hasEarly, boolean hasLate);

        /**
         * 加载失败
         * <p>
         * code
         * exception
         * 可能为空
         */
        public void fail(ApiErrorCode code, Exception exception);

        /***
         * 返回数据集合的状态
         *
         *  size
         *            数据量
         *  isnull
         *            是否为空
         */
        public void dataState(int size, boolean isnull);
    }

    /**
     * 定位到某一条
     * <p>
     * cursorId
     * pageSize
     * param
     * callback
     */
    public void loadContextByCursor(String commentid, int pageSize, P param, final LoadCallback callback) {
        if (!service.supportContextByCursor()) {
            XL.w(TAG, "不支持根据数据定位");
            return;
        }
        if (loading) {
            XL.d(TAG, "数据加载中..");
            return;
        }

        synchronized (lock) {
            if (!loading) {
                loading = true;
                final int version = ++dataVersion;
                service.getListWithContext(commentid, pageSize, param, new ListContextCallback<E>() {

                    @Override
                    public void success(@NotNull List<? extends E> list, @NotNull String preCursorId, @NotNull String nextCursorId) {
                        if (version != dataVersion) {
                            // 如果加载过程中数据被更新了，就丢弃(比如reset了，但是下一页还在加载中)
                            XL.w(TAG, "丢弃不一致的数据，期待版本: " + version + ", 当前版本: " + dataVersion);
                            return;
                        }
                        // 更新下一页游标
                        BaseListApiAdapter.this.earlyCursorId = nextCursorId;
                        BaseListApiAdapter.this.lateCursorId = lateCursorId;
                        XL.v(TAG, "取到数据: " + list.size());
                        if (CollectionUtils.isNotEmpty(list)) {

                            if (service.needReverse()) {
                                // asc的顺序是对的，不用反转
                                List<E> tmp = new ArrayList<E>();
                                tmp.addAll(list);
                                Collections.reverse(tmp);
                                list = tmp;
                            }
                            synchronized (listData) {
                                if (service.appendToHeader()) {
                                    listData.addAll(0, list);
                                } else {
                                    listData.addAll(list);
                                }
                            }
                        }

                        // 失败后也要重置状态
                        loading = false;
                        if (nextCursorId == null) {
                            reachHead = true;
                        }
                        if (lateCursorId == null) {
                            reachEnd = true;
                        }

                        // 通知ui
                        notifyDataSetChanged();

                        callback.success(!reachHead, !reachEnd);
                    }

                    @Override
                    public void fail(ApiErrorCode code, Exception exception) {
                        callback.fail(code, exception);

                        loading = false;
                    }
                });

            }
        }
    }

    /**
     * 向上加载一页数据
     * <p>
     * pageSize
     * param
     * callback
     */
    public void loadLatePage(int pageSize, P param, final LoadCallback callback) {
        if (!service.supportPrev()) {
            XL.w(TAG, "不支持根向上翻页");
            return;
        }
        if (reachEnd) {
            XL.i("COMMENT_TEST", "数据加载到结尾了");
            return;
        }
        loadNextPage(pageSize, param, Direct.Late, callback);
    }

    /**
     * 加载下一页数据，如果还没有加载过数据，或者调用过 {@link #resetState()} 从头开始加载
     * <p>
     * pageSize
     * param
     * 给service的参数，会原样传过去
     * callback
     * 如果当前的调用被忽略，callback不会被回调，仅当数据成功加载后才回调
     */
    public void loadEarlyPage(int pageSize, P param, final LoadCallback callback) {
        if (reachHead) {
            XL.d(TAG, "数据加载到最顶部了");
            return;
        }
        loadNextPage(pageSize, param, Direct.Early, callback);
    }

    public void loadNextPage(int pageSize, P param, final Direct direct, final LoadCallback callback) {
        if (loading) {
            XL.d(TAG, "数据加载中..");
            return;
        }

        synchronized (lock) {
            if (!loading) {
                loading = true;
                final int version = ++dataVersion;
                String cursor = (direct == Direct.Early ? earlyCursorId : lateCursorId);
                service.getList(cursor, pageSize, direct, param, new BaseListApiService.ApiListCallback<E>() {

                    @Override
                    public void success(@NotNull List<? extends E> list, @NotNull String cursorId) {
                        if (version != dataVersion) {
                            // 如果加载过程中数据被更新了，就丢弃(比如reset了，但是下一页还在加载中)
                            XL.w(TAG, "丢弃不一致的数据，期待版本: " + version + ", 当前版本: " + dataVersion);
                            callback.fail(null, null);
                            return;
                        }
                        resetDataListDelay();// 延时重载数据
                        // 更新下一页游标
                        if (direct == Direct.Early) {
                            if (TextUtils.isEmpty(cursorId)) {
                                reachHead = true;
                            } else {
                                BaseListApiAdapter.this.earlyCursorId = cursorId;
                            }
                        } else {
                            if (TextUtils.isEmpty(cursorId)) {
                                reachEnd = true;
                            } else {
                                BaseListApiAdapter.this.lateCursorId = cursorId;
                            }
                        }

                        XL.v(TAG, "取到数据: " + list.size());
                        if (CollectionUtils.isNotEmpty(list)) {
                            if (service.needReverse() && direct == Direct.Early) {
                                // asc的顺序是对的，不用反转
                                List<E> tmp = new ArrayList<E>();
                                tmp.addAll(list);
                                Collections.reverse(tmp);
                                list = tmp;
                            }
                            synchronized (listData) {
                                if (direct == Direct.Early) {
                                    if (service.appendToHeader()) {
                                        listData.addAll(0, list);
                                    } else {
                                        listData.addAll(list);
                                    }
                                } else {
                                    // TODO asc的数据如何放置
                                    listData.addAll(list);
                                }
                            }
                        }
                        // 失败后也要重置状态
                        loading = false;

                        // 通知ui
                        notifyDataSetChanged();
                        callback.success(!reachHead, !reachEnd);
                        returnListDataState(callback);
                    }

                    @Override
                    public void fail(ApiErrorCode code, Exception exception) {
                        callback.fail(code, exception);
                        loading = false;
                    }
                });

            }
        }
    }

    /***
     * 返回数据集合的状态
     *
     *  callback
     * @return void
     */
    private void returnListDataState(LoadCallback callback) {
        if (callback == null) {
            return;
        }
        // 返回数据集合的状态
        boolean isnull = false;
        int size = 0;
        if (listData == null) {
            isnull = true;
        } else {
            size = listData.size();
        }
        callback.dataState(size, isnull);
    }

    public void appendListItem(Direct direct, List<E> list) {
        if (loading) {
            return;
        }
        synchronized (listData) {
            if (direct == Direct.Early) {
                listData.addAll(0, list);
            } else {
                listData.addAll(list);
            }
        }

    }

    /**
     * 清理已加载的所有数据，清理当前状态
     */
    public void resetState() {
        synchronized (lock) {
            listData.clear();
            earlyCursorId = "";
            lateCursorId = "";
            reachEnd = false;
            reachHead = false;
            loading = false;
            dataVersion++;
            // 通知ui
            notifyDataSetChanged();
        }
    }

    /***
     * 先请求数据，后重置数据，这样就能保证在得到新数据之前旧数据还显示在屏幕上，否则请求成功前，会因为数据被清空而生成空视图 注意：此方法要在
     * {@link #loadNextPage(int, Object, Direct, LoadCallback) 方法调用前调用}
     */
    public void resetStateDelay() {
        synchronized (lock) {
            earlyCursorId = "";
            lateCursorId = "";
            reachEnd = false;
            reachHead = false;
            loading = false;
            dataVersion++;
            resetDelay = true;// 标记数据需要在请求数据之后重置
        }
    }

    /***
     * 延时重置数据（没有重置状态），要先调用{@link #resetStateDelay()}后， 此方法才会在
     * {@link #loadNextPage(int, Object, Direct, LoadCallback)} 方法中被调用，
     *
     *
     * @see {@link #resetStateDelay()}
     */
    public void resetDataListDelay() {
        if (resetDelay == true) {
            synchronized (lock) {
                listData.clear();
                notifyDataSetChanged();
                resetDelay = false;
            }
        }
    }

    // ///// 以下是ui相关代码

    /**
     * 支持多种视图，配合 {@link #getItemMultiViewType(Object)} 使用
     *
     * @return default: false
     */
    protected boolean isMultiListView() {
        return false;
    }

    /**
     * 获取视图数量
     *
     * @return
     */
    protected int getMultiListViewTypeCount() {
        if (!isMultiListView()) {
            return 1;
        }
        throw new UnsupportedOperationException("子类没有实现这个方法！");
    }

    /**
     * 获取视图的类型
     * <p>
     * item
     * 应当有个字段是实现了 {@link MultiListViewType} 的枚举
     *
     * @return
     */
    protected MultiListViewType getItemMultiViewType(E item) {
        throw new UnsupportedOperationException("子类没有实现这个方法！");
    }

    @Override
    public int getCount() {
        XL.d(TAG + TAG, "getCount:" + listData.size());
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= listData.size()) {
            return null;
        }
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return getMultiListViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (!isMultiListView()) {
            // 普通列表
            return IGNORE_ITEM_VIEW_TYPE;
        }

        @SuppressWarnings("unchecked")
        E item = (E) getItem(position);
        return getItemMultiViewType(item).getViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        XL.d(TAG + TAG, "getView");
        @SuppressWarnings("unchecked")
        E item = (E) getItem(position);

        AbsViewHolder holder;
        if (convertView != null) {
            XL.v(TAG, "取到缓存的视图: " + position);
            holder = (AbsViewHolder) convertView.getTag();
        } else {
            XL.v(TAG, "新建视图: " + position);
            MultiListViewType type = null;
            if (isMultiListView()) {
                // 如果是复合列表
                type = getItemMultiViewType(item);
            }
            holder = newViewHolder(type, item, inflater, parent);
            if (holder == null) {
                throw new IllegalStateException("类型为: " + type + " 的列表项目没有正确处理(返回了null ViewHolder)");
            }
            convertView = holder.getView();
            convertView.setTag(holder);
        }

        // 可以更新视图了
        long t = System.currentTimeMillis();
        fillView(holder, item, position, convertView);
        XL.v("Perfs", "BaseListApiAdapter.fillView: " + (System.currentTimeMillis() - t));
        // 不是新建的了
        holder.setNewHolder(false);
        return convertView;
    }

    /***
     * 移除列表中的某一项
     *
     *  position
     * @return void
     */
    public void removeItem(int position) {
        if (listData.size() > position) {
            listData.remove(position);
        }
        notifyDataSetChanged();
    }

    /***
     * 改变notify 的未读状态， 期待更好的实现方法
     *
     *  state
     *            要改变到的状态
     *  position
     *            要改变的item 的位置
     * @return void
     */
    public void changeItemState(boolean state, int position) {
        if (listData.size() > position) {
            E e = listData.get(position);
            if (e instanceof Notify2) {
                ((Notify2) e).changeReadState(state);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public List<E> getListData() {
        return listData;
    }

}
