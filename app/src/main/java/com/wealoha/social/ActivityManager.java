package com.wealoha.social;

import java.util.Stack;

import android.app.Activity;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description activity 管理
 * @copyright wealoha.com
 * @Date:2014-12-4
 */
public class ActivityManager {

	public static Stack<BaseFragAct> stack = new Stack<BaseFragAct>();// Activity栈

	/**
	 * @Description:移除所有activity
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-12-4
	 */
	public static void popAll() {
		while (!stack.isEmpty()) {
			pop();
		}
	}

	/**
	 * @Description: 移除位于栈顶的activity
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-12-4
	 */
	public static void pop() {
		Activity activity = stack.pop();
		if (activity != null && !activity.isFinishing()) {
			activity.finish();
		}
	}

	/**
	 * @Description:移除指定activity
	 * @param activity
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-12-4
	 */
	public static void pop(Activity activity) {
		if (activity != null && !activity.isFinishing()) {
			activity.finish();
			activity.overridePendingTransition(0, R.anim.right_out);
		}
		stack.remove(activity);
	}

	/**
	 * @Description:移除并关闭指定某一类的activity
	 * @param cls
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-12-4
	 */
	public static void popClass(Class<? extends BaseFragAct> cls) {
		Stack<BaseFragAct> newStack = new Stack<BaseFragAct>();
		for (BaseFragAct a : stack) {
			if (a.getClass().equals(cls)) {
				if (!a.isFinishing()) {
					a.finish();
				}
			} else {
				newStack.push(a);
			}
		}
		stack = newStack;
	}

	/**
	 * @Description: 获取在栈顶的activity
	 * @return
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-12-4
	 */
	public static BaseFragAct current() {
		if (stack.isEmpty()) {
			return null;
		}
		return stack.peek();
	}

	/**
	 * @Description:是否存在栈内
	 * @param pClass
	 * @return
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-12-4
	 */
	public static boolean compareInstance(Class<?> pClass) {
		for (BaseFragAct iterable : stack) {
			if (iterable.getClass().getSimpleName().equals(pClass.getSimpleName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @Description: 获取保存的在栈中的activity
	 * @param pClass
	 * @return
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-12-5
	 */
	public static BaseFragAct isSaveStack(Class<?> pClass) {
		for (BaseFragAct iterable : stack) {
			if (iterable.getClass().getSimpleName().equals(pClass.getSimpleName())) {
				return iterable;
			}
		}
		return null;
	}

	/**
	 * @Description:添加activity到栈中
	 * @param activity
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-12-4
	 */
	public static void push(BaseFragAct activity) {
		stack.push(activity);
	}

	/**
	 * @Description:保留某一类的activity，其它的都关闭并移出栈
	 * @param cls
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-12-4
	 */
	public static void retain(Class<? extends BaseFragAct> cls) {
		Stack<BaseFragAct> newStack = new Stack<BaseFragAct>();
		for (BaseFragAct a : stack) {
			if (a.getClass().equals(cls)) {
				newStack.push(a);
			} else {
				if (!a.isFinishing()) {
					a.finish();
				}
			}
		}
		stack = newStack;
	}
}