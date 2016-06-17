package com.melink.bqmm_eo.ui;

import java.lang.ref.WeakReference;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.melink.baseframe.utils.DensityUtils;
import com.melink.baseframe.utils.KJLoger;
import com.melink.bqmm_eo.R;
import com.melink.bqmmsdk.bean.Emoji;
import com.melink.bqmmsdk.sdk.BQMM;
import com.melink.bqmmsdk.sdk.BQMMMessageHelper;
import com.melink.bqmmsdk.sdk.IBqmmSendMessageListener;
import com.melink.bqmmsdk.ui.keyboard.BQMMKeyboard;
import com.melink.bqmmsdk.widget.BQMMEditView;
import com.melink.bqmmsdk.widget.BQMMSendButton;

public class CommentEditActivity extends FragmentActivity {
	private BQMMEditView mEditView;
	private BQMMKeyboard mKeyboard;
	private BQMMSendButton mSendButton;
	private CheckBox mOpenBox;
	private RelativeLayout mOpenArea;
	private BQMM bqmmsdk;
	private final int DISTANCE_SLOP = 180;
	private final String LAST_KEYBOARD_HEIGHT = "last_keyboard_height";
	private boolean mPendingShowPlaceHolder;
	private int mScreenHeight;
	private View mRootView;
	private Rect tmp = new Rect();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bqmm_comment_edit);
		initView();
	}

	private void initView() {
		mEditView = (BQMMEditView) findViewById(R.id.comment_editview);
		mKeyboard = (BQMMKeyboard) findViewById(R.id.comment_keyboard);
		mSendButton = (BQMMSendButton) findViewById(R.id.comment_sendbutton);
		mSendButton.setVisibility(View.VISIBLE);
		mOpenBox = (CheckBox) findViewById(R.id.comment_checkbox);
		mOpenArea = (RelativeLayout) findViewById(R.id.comment_checkbox_area);
		mRootView = findViewById(R.id.comment_rootview);
		mSendButton.setEnabled(false);
		mEditView.addTextChangedListener(getTextWatcher());
		//自定义EditView中大表情和小表情的显示大小
		mEditView.setBQMMEditViewFaceSize(TypedValue.COMPLEX_UNIT_DIP, 60);
		mEditView.setBQMMEditViewEmojiSize(TypedValue.COMPLEX_UNIT_DIP, 30);
		/**BQMM集成  */
		initBQMMSDK();
	}
	
	

	/**
	 * 初始化BQMMSDK
	 */
	private void initBQMMSDK() {
		bqmmsdk = BQMM.getInstance();
		bqmmsdk.setEditView(mEditView);
		bqmmsdk.setKeyboard(mKeyboard);
		bqmmsdk.setSendButton(mSendButton);
		//设置BQMMSDK是评论版还是IM版，新方法
		bqmmsdk.setBQMMSDKMode(false);
		bqmmsdk.load();
		mEditView.setOnTouchListener(new View.OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mOpenBox.setChecked(false);
				return false;
			}
		});
		BqmmSendMessageListener  bqmmSendMessageListener = new BqmmSendMessageListener(this);
		bqmmsdk.setBqmmSendMsgListener(bqmmSendMessageListener);

		/**
		 * 设置键盘的默认高度
		 */
		int defaultHeight = DensityUtils.dip2px(this, 250);
		int height = getPreferences(MODE_PRIVATE).getInt(LAST_KEYBOARD_HEIGHT,
				defaultHeight);
		ViewGroup.LayoutParams params = mKeyboard.getLayoutParams();
		if (params != null) {
			params.height = height;
			mKeyboard.setLayoutParams(params);
		}
		/**
		 * 表情键盘切换监听
		 */
		mEditView.getViewTreeObserver().addOnPreDrawListener(
				new ViewTreeObserver.OnPreDrawListener() {
					@SuppressLint("NewApi")
					@Override
					public boolean onPreDraw() {
						// Keyboard -> BQMM
						if (mPendingShowPlaceHolder) {
							// 在设置mPendingShowPlaceHolder时已经调用了隐藏Keyboard的方法，直到Keyboard隐藏前都取消重绘
							if (isKeyboardVisible()) {
								ViewGroup.LayoutParams params = mKeyboard
										.getLayoutParams();
								int distance = getDistanceFromInputToBottom();
								// 调整PlaceHolder高度
								if (distance > DISTANCE_SLOP
										&& distance != params.height) {
									params.height = distance;
									mKeyboard.setLayoutParams(params);
									getPreferences(MODE_PRIVATE)
											.edit()
											.putInt(LAST_KEYBOARD_HEIGHT,
													distance).apply();
								}
								return false;
							} else {
								showBqmmKeyboard();
								mPendingShowPlaceHolder = false;
								return false;
							}
						} else {// BQMM -> Keyboard
							if (isBqmmKeyboardVisible() && isKeyboardVisible()) {
								hideBqmmKeyboard();
								return false;
							}
						}
						return true;
					}
				});
		//键盘切换
		mOpenBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 除非软键盘和PlaceHolder全隐藏时直接显示PlaceHolder，其他情况此处处理软键盘，onPreDrawListener处理PlaceHolder
				if (isBqmmKeyboardVisible()) { // PlaceHolder -> Keyboard
					showSoftInput(mEditView);
				} else if (isKeyboardVisible()) { // Keyboard -> PlaceHolder
					mPendingShowPlaceHolder = true;
					hideSoftInput(mEditView);
				} else { // Just show PlaceHolder
					showBqmmKeyboard();
				}
			}
		});
	}

	/************************** 表情键盘 start **************************************/
	private void closebroad() {
		if (isBqmmKeyboardVisible()) {
			hideBqmmKeyboard();
		} else if (isKeyboardVisible()) {
			hideSoftInput(mEditView);
		}
	}

	private boolean isKeyboardVisible() {
		return (getDistanceFromInputToBottom() > DISTANCE_SLOP && !isBqmmKeyboardVisible())
				|| (getDistanceFromInputToBottom() > (mKeyboard.getHeight() + DISTANCE_SLOP) && isBqmmKeyboardVisible());
	}

	@SuppressLint("NewApi")
	private void showSoftInput(View view) {
		view.requestFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, 0);
	}

	@SuppressLint("NewApi")
	private void hideSoftInput(View view) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus && mScreenHeight <= 0) {
			mRootView.getGlobalVisibleRect(tmp);
			mScreenHeight = tmp.bottom;
		}
	}

	private void showBqmmKeyboard() {
		mKeyboard.showKeyboard();
	}

	private void hideBqmmKeyboard() {
		mKeyboard.hideKeyboard();
	}

	private boolean isBqmmKeyboardVisible() {
		return mKeyboard.isKeyboardVisible();
	}

	/**
	 * 输入框的下边距离屏幕的距离
	 */
	private int getDistanceFromInputToBottom() {
		return mScreenHeight - getInputBottom();
	}

	/**
	 * 输入框下边的位置
	 */
	private int getInputBottom() {
		mOpenArea.getGlobalVisibleRect(tmp);
		return tmp.bottom;
	}

	/************************** 表情键盘 end **************************************/

	private static class BqmmSendMessageListener implements IBqmmSendMessageListener {
		private WeakReference<CommentEditActivity> wf = null;
		BqmmSendMessageListener(CommentEditActivity activity) {
			wf = new WeakReference<CommentEditActivity>(activity);
		}
		@Override
		public void onSendMixedMessage(List<Object> messageContent, boolean isMixedMessage) {
			CommentEditActivity activity = wf.get();
			if (activity != null) {
				Intent intent = new Intent();
				//将消息List<Object> 转换成[.code]或者[~code]格式
				intent.putExtra("mixedCommentMessage", BQMMMessageHelper
						.getMixedMessage(messageContent));
				intent.putExtra("isMixedMessage", isMixedMessage);
				activity.setResult(CommentListActivity.REQUEST_CODE, intent);
				activity.finish();
				
			}
		}

		@Override
		public void onSendFace(Emoji emoji) {

		}
	}
	
	private TextWatcher getTextWatcher() {
		return new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (!arg0.toString().equals("")) {
					mSendButton.setEnabled(true);
				} else {
					mSendButton.setEnabled(false);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		};
	}

	@Override
	protected void onDestroy() {
		bqmmsdk.destory();
		super.onDestroy();
	}

}
