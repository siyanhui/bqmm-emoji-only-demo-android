#Android评论（小表情）版SDK接入说明



##APP 打包混淆配置

```
-keep class **.R$*{<fields>;}
-dontwarn com.melink.**
-dontwarn com.thirdparty.**
-keep class com.melink.** {*;} 
-keep class com.thirdparty.* {*;}
```
    
##初始化 SDK
1.在 DemoApplication中做初始化，传入申请的appid，appsecret
   

```java
BQMM.getInstance().initConfig(getApplicationContext(),"YOUR_APP_ID", "YOUR_APP_SECRET");
```

`YOUR_APP_ID`和`YOUR_APP_SECRET`可在[注册](http://open.biaoqingmm.com/open/register/index.html)页面申请。


2.在相关布局文件中引入键盘相关控件布局

```xml
<com.melink.bqmmsdk.widget.BQMMEditView
        android:id="@+id/comment_editview"
        android:layout_width="fill_parent"
        android:layout_height="0px"
        android:layout_weight="1"
        android:background="@null"
        android:focusable="true"
        android:gravity="top|left"
        android:hint="输入您要评论的内容..."
        android:padding="5dp" />
        
<com.melink.bqmmsdk.ui.keyboard.BQMMKeyboard
        android:id="@+id/comment_keyboard"
        android:layout_width="match_parent"
        android:layout_height="240dp" />
        
<com.melink.bqmmsdk.widget.BQMMSendButton
        android:id="@+id/comment_sendbutton"
        android:layout_width="55dp"
        android:layout_height="30dp"
        android:layout_marginRight="10dp"
        android:layout_alignParentRight="true"
        android:text="@string/bqmm_send"
        android:textSize="15sp" 
        android:background="@drawable/bqmm_sendbutton_bg"
        android:textColor="@color/bqmm_sendbutton_textcolor"
        android:layout_centerVertical="true"
        android:visibility="gone"
        />
```

##在表情键盘页面声明控件及初始化BQMMSDK

```java
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
```

可参考`CommentEditActivity`


##监听表情发送事件

```java
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
```	


##显示表情消息 

使用BQMMMessageText的默认方式展示消息

```xml
 <com.melink.bqmmsdk.widget.BQMMMessageText
    android:id="@+id/item_content"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginTop="7dp"
    android:textColor="@color/bqmm_textBlack"
    android:textSize="@dimen/bqmm_smallTextSize" />
```
	    
```java 
/**
 *  消息展示
 * BQMMMessageText.EMOJITYPE 混排消息 
 *
 **/
holder.content.showMessage(itemMsg.getMsgId(),itemMsg.getMixedCommentMessage(), BQMMMessageText.EMOJITYPE);
```

设置表情的显示大小

```java
/**
 * 设置控件中展示小表情的尺寸大小
 * 
 * @param size
 */
public void setSmallEmojiShowSize(int size)
	
```












