package com.business.kp.utill;

import java.util.ArrayList;
import java.util.List;

public class BeanPopulate {
	
	public static List<RegistraionDetailsBean> beanList=new ArrayList<RegistraionDetailsBean>();

	public static List<RegistraionDetailsBean> getBeanList() {
		return beanList;
	}

	public static void setBeanList(List<RegistraionDetailsBean> beanList) {
		BeanPopulate.beanList = beanList;
	}

}
