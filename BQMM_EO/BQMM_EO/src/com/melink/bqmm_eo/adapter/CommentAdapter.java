package com.melink.bqmm_eo.adapter;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.melink.baseframe.utils.KJLoger;
import com.melink.baseframe.utils.StringUtils;
import com.melink.bqmm_eo.R;
import com.melink.bqmm_eo.bean.CommentMessage;
import com.melink.bqmmsdk.widget.BQMMMessageText;

public class CommentAdapter extends BaseAdapter {
	private Context mContext;
	private List<CommentMessage> datas = null;
	private boolean isScrolling = false;

	public CommentAdapter(Context cxt, List<CommentMessage> datas) {
		this.mContext = cxt;
		this.datas = datas;
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View v, ViewGroup parent) {
		final ViewHolder holder;
		final CommentMessage itemMsg = datas.get(position);
		if (v == null) {
			holder = new ViewHolder();
			v = View.inflate(mContext, R.layout.bqmm_comment_item, null);
			holder.userName = (TextView) v.findViewById(R.id.item_username);
			holder.date = (TextView) v.findViewById(R.id.item_date);
			holder.time = (TextView) v.findViewById(R.id.item_time);
			holder.content = (BQMMMessageText) v.findViewById(R.id.item_content);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		holder.date.setText(StringUtils.getDateTime(itemMsg.getTime(),"yyyy-MM-dd " + "HH:mm:ss"));
		holder.userName.setText(itemMsg.getUserName());
		holder.content.setBigEmojiShowSize(140);
		holder.content.setSmallEmojiShowSize(60);
		holder.content.isScrolling(isScrolling);
		/**
		 *  消息展示
		 * BQMMMessageText.EMOJITYPE 混排消息 
		 *
		 **/
		holder.content.showMessage(itemMsg.getMsgId(),itemMsg.getMixedCommentMessage(), BQMMMessageText.EMOJITYPE);
		return v;
	}
	class ViewHolder {
		TextView userName;
		TextView date;
		TextView time;
		BQMMMessageText content;
	}
	public void isViewScrolling(boolean isScrolling) {
		this.isScrolling = isScrolling;
		if(!isScrolling){
			notifyDataSetChanged();
		}
	}
}
