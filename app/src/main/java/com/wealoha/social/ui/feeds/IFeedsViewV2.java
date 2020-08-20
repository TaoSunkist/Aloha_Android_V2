package com.wealoha.social.ui.feeds;

import java.util.List;

import com.wealoha.social.beans.Post;
import com.wealoha.social.ui.base.ScrollToLoadView;

public interface IFeedsViewV2 extends ScrollToLoadView {

	void setData(List<Post> postList);

	void postViewScroll(int x, int y, String id);
}
