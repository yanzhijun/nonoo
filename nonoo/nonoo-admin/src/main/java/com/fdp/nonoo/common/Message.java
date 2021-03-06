package com.fdp.nonoo.common;

import com.fdp.nonoo.util.SpringUtils;

public class Message {
	private Type type;
	private String content;

	public Message() {
	}

	public Message(Type type, String content) {
		this.type = type;
		this.content = content;
	}

	public Message(Type type, String content, Object[] args) {
		this.type = type;
		this.content = SpringUtils.getMessage(content, args);
	}

	public static Message success(String content, Object[] args) {
		return new Message(Type.success, content, args);
	}

	public static Message warn(String content, Object[] args) {
		return new Message(Type.warn, content, args);
	}

	public static Message error(String content, Object[] args) {
		return new Message(Type.error, content, args);
	}

	public Type getType() {
		return this.type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String toString() {
		return SpringUtils.getMessage(this.content, new Object[0]);
	}

	public static enum Type {
		success, warn, error

	}

}