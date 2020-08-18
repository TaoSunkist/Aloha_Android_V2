package com.wealoha.social.beans.region;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2015年7月9日
 */
public class Regions implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5690191620144863785L;
	public String code;
	public String name;
	public ArrayList<Regions> regions;

}
