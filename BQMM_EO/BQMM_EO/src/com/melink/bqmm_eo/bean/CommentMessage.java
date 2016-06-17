package com.melink.bqmm_eo.bean;

import java.util.Date;

/**
 * 评论消息实体
 */
public class CommentMessage {
	
	/**
	 * 消息Id
	 */
	private String msgId;
	/**
	 * 消息用户名
	 */
    private String userName;
    /**
     * 消息时间
     */
    private Date time;
    /**
     * 是否为混排消息
     */
    private boolean isMixedMessage;
    /**
     * 消息内容
     */
    private String mixedCommentMessage;
    
    public CommentMessage(String msgId,String userName,boolean isMixedMessage,String mixedCommentMessage,Date time){
    	super();
    	this.msgId = msgId;
    	this.userName = userName;
    	this.time = time;
    	this.isMixedMessage = isMixedMessage;
    	this.mixedCommentMessage = mixedCommentMessage;
    }
    
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getMixedCommentMessage() {
		return mixedCommentMessage;
	}

	public void setMixedCommentMessage(String mixedCommentMessage) {
		this.mixedCommentMessage = mixedCommentMessage;
	}

	public boolean isMixedMessage() {
		return isMixedMessage;
	}

	public void setMixedMessage(boolean isMixedMessage) {
		this.isMixedMessage = isMixedMessage;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
   

    
}
