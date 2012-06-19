/**
 * Copyright (c) 2010 Ray.
 * Wen Yi West RD, Hang Zhou, Zhe Jiang, China.
 * All rights reserved.
 *
 * "ExecuteResult.java is the copyrighted,
 * proprietary property of Ray which retain all right, 
 * title and interest therein."
 * 
 * Create by RayStone at 上午09:32:40.
 * RayStone [email:rayinhangzhou@gmail.com]
 * 
 * Revision History
 *
 * Date              Programmer                   Notes
 * ---------    ---------------------  -----------------------------------
 * 2010-4-17           RayStone                     initial
 **/

package commonTool;

import java.io.Serializable;

public class ExecuteResult implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8000143070116646864L;
	private boolean isTrue;
	private String message;
	private Object tag;

	/**
	 * 运行结果
	 * @param isTrue
	 * @param message
	 * @param tag
	 */
	public ExecuteResult(boolean isTrue, String message, Object tag) {
		this.setIsTrue(isTrue);
		this.setMessage(message);
		this.setTag(tag);
	}
	
	/**
	 * 运行结果
	 * @param isTrue
	 * @param message
	 */
	public ExecuteResult(boolean isTrue, String message) {
		this.setIsTrue(isTrue);
		this.setMessage(message);
	}

	public boolean getIsTrue() {
		return isTrue;
	}

	public void setIsTrue(boolean isTrue) {
		this.isTrue = isTrue;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getTag() {
		return tag;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}
}
