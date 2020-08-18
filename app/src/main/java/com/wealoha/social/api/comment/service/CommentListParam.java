package com.wealoha.social.api.comment.service;

/**
 * 
 * 
 * @author javamonk
 * @createTime 2015年3月12日 下午2:29:55
 */
public class CommentListParam {

	private final int a;
	private final int b;
	private final int c;

	public CommentListParam(int a, int b, int c) {
		super();
		this.a = a;
		this.b = b;
		this.c = c;
	}

	public int getA() {
		return a;
	}

	public int getB() {
		return b;
	}

	public int getC() {
		return c;
	}

}
