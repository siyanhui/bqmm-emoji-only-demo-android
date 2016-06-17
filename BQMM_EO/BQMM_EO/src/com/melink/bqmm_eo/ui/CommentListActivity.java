package com.melink.bqmm_eo.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.melink.bqmm_eo.R;
import com.melink.bqmm_eo.adapter.CommentAdapter;
import com.melink.bqmm_eo.bean.CommentMessage;

public class CommentListActivity extends Activity {

	private LinearLayout mCommentButton;
	private ListView mCommentList;
	private List<CommentMessage> mData = new ArrayList<CommentMessage>();
	private CommentAdapter mAdapter;
	private int msgId = 0;
	public final static int REQUEST_CODE = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bqmm_comment_list);
		initView();
	}
	
	@SuppressLint("InflateParams")
	private void initView() {
		mCommentButton = (LinearLayout) findViewById(R.id.listlayout_comment);
		mCommentList = (ListView) findViewById(R.id.listlayout_list);
		View emptyView = findViewById(R.id.listlayout_empty);
		mCommentList.setEmptyView(emptyView);
		mAdapter = new CommentAdapter(CommentListActivity.this, mData);
		mCommentList.setAdapter(mAdapter);
		//跳转至编辑页面
		mCommentButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(CommentListActivity.this,CommentEditActivity.class);
				startActivityForResult(intent, REQUEST_CODE);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == REQUEST_CODE ){
			String mixedCommentMessage = data.getStringExtra("mixedCommentMessage");
			boolean isMixedMessage = data.getBooleanExtra("isMixedMessage", false);
			CommentMessage commentMsg = new CommentMessage(String.valueOf(msgId++),"表情mm",isMixedMessage,mixedCommentMessage,new Date());
			mData.add(commentMsg);
			refreshListView();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	/**
	 * 刷新页面
	 */
	private void refreshListView() {
		if(mData == null || mData.size() <= 0){
			return;
		}
		if(mAdapter == null){
			mAdapter = new CommentAdapter(CommentListActivity.this, mData);
			mCommentList.setAdapter(mAdapter);
		}else{
			mAdapter.notifyDataSetChanged();
		}
		mCommentList.setSelection(mCommentList.getAdapter().getCount() -1);
	}
}
