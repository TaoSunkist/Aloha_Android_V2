package com.wealoha.social.adapter.feed;

import android.view.View;

import com.wealoha.social.api.post.bean.Post;

public abstract class BaseFeedHolder extends AbsViewHolder {

	protected BaseFeedHolder parentHolder;
	protected BaseFeedHolder childHodler;
	protected int holderPosition;
	protected Holder2AdtCallback holder2AdtCallback;
	protected Holder2FragCallback holder2FragCallback;

	public interface Holder2AdtCallback {

		/***
		 * 删除feed后回调给holder
		 * 
		 * @param position
		 *            holder 的位置
		 * @return void
		 */
		public void deletePostCallback(int position);

		public void setHolder2FragCallback(Holder2FragCallback callback);
	}

	public interface Holder2FragCallback {

		public int getHolderType();

		public void praiseCallback();

		public void deleteCallback();

		public void commentClickCallback();
	}

	/***
	 * 设置回调holder 父组件的函数
	 * 
	 * @param holder2FragCallback
	 * @return void
	 */
	public void setHolder2FragCallback(Holder2FragCallback holder2FragCallback) {
		this.holder2FragCallback = holder2FragCallback;
	}

	public void setHolder2AdtCallback(Holder2AdtCallback holder2AdtCallback) {
		this.holder2AdtCallback = holder2AdtCallback;
	}

	/***
	 * 赞的操作
	 * 
	 * @return void
	 */
	public abstract void praisePost();

	/***
	 * 保存子视图引用， 并调用子视图中 {@link #addParentHolder(BaseFeedHolder)} 将父视图引用保存给子视图，所以实现这个方法的子类应该调用父类的该方法以便完成上述操作
	 * 
	 * @param feedcontentHolder
	 */
	public void addChildHolder(BaseFeedHolder feedcontentHolder) {
		this.childHodler = feedcontentHolder;
		if (childHodler != null) {
			childHodler.addParentHolder(this);
		}
	};

	public void addParentHolder(BaseFeedHolder parentHolder) {
		this.parentHolder = parentHolder;
	}

	public BaseFeedHolder getContentHolder() {
		return childHodler;
	};

	/***
	 * 
	 * @param post
	 * @param holderPosition
	 *            holder当前的位置
	 * @return
	 * @return View
	 */
	public View resetViewData(Post post, int holderPosition) {
		this.holderPosition = holderPosition;

		return resetViewData(post);
	};

	/***
	 * 
	 * @param post
	 * @return
	 * @return View
	 */
	public abstract View resetViewData(Post post);

	/***
	 * 移除指定的tag，有关tag的方法和变量以后都要移动到父类中
	 * 
	 * @param 要移除的用户id
	 * @return void
	 */
	public abstract void removeTag(String userid);

	public abstract boolean isPlayer();

	public abstract int getRawTopY();

	public abstract int getRawBottomY();
}
