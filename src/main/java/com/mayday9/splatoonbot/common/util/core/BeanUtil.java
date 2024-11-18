/*
 *      Copyright (c) 2018-2028, DreamLu All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: DreamLu 卢春梦 (596392912@qq.com)
 */
package com.mayday9.splatoonbot.common.util.core;


import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 实体工具类
 *
 * @author L.cm
 */
public class BeanUtil extends org.springframework.beans.BeanUtils {

	/**
	 * 实例化对象
	 *
	 * @param clazz 类
	 * @param <T>   泛型标记
	 * @return 对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<?> clazz) {
		return (T) instantiateClass(clazz);
	}

	/**
	 * 实例化对象
	 *
	 * @param clazzStr 类名
	 * @param <T>      泛型标记
	 * @return 对象
	 */
	public static <T> T newInstance(String clazzStr) {
		try {
			Class<?> clazz = ClassUtil.forName(clazzStr, null);
			return newInstance(clazz);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取Bean的属性, 支持 propertyName 多级 ：test.user.name
	 *
	 * @param bean         bean
	 * @param propertyName 属性名
	 * @return 属性值
	 */
	@Nullable
	public static Object getProperty(@Nullable Object bean, String propertyName) {
		if (bean == null) {
			return null;
		}
		BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
		return beanWrapper.getPropertyValue(propertyName);
	}

	/**
	 * 设置Bean属性, 支持 propertyName 多级 ：test.user.name
	 *
	 * @param bean         bean
	 * @param propertyName 属性名
	 * @param value        属性值
	 */
	public static void setProperty(Object bean, String propertyName, Object value) {
		Objects.requireNonNull(bean, "bean Could not null");
		BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
		beanWrapper.setPropertyValue(propertyName, value);
	}

	/**
	 * Copy the property values of the given source bean into the target class.
	 * <p>Note: The source and target classes do not have to match or even be derived
	 * from each other, as long as the properties match. Any bean properties that the
	 * source bean exposes but the target bean does not will silently be ignored.
	 * <p>This is just a convenience method. For more complex transfer needs,
	 *
	 * @param source the source bean
	 * @param targetClazz the target bean class
	 * @param <T>    泛型标记
	 * @return T
	 * @throws BeansException if the copying failed
	 */
	@Nullable
	public static <T> T copyProperties(@Nullable Object source, Class<T> targetClazz) throws BeansException {
		if (source == null) {
			return null;
		}
		T to = newInstance(targetClazz);
		BeanUtil.copyProperties(source, to);
		return to;
	}

	/**
	 * Copy the property values of the given source bean into the target class.
	 * <p>Note: The source and target classes do not have to match or even be derived
	 * from each other, as long as the properties match. Any bean properties that the
	 * source bean exposes but the target bean does not will silently be ignored.
	 * <p>This is just a convenience method. For more complex transfer needs,
	 *
	 * @param sourceList the source list bean
	 * @param targetClazz the target bean class
	 * @param <T>    泛型标记
	 * @return List
	 * @throws BeansException if the copying failed
	 */
	public static <T> List<T> copyProperties(@Nullable Collection<?> sourceList, Class<T> targetClazz) throws BeansException {
		if (sourceList == null || sourceList.isEmpty()) {
			return Collections.emptyList();
		}
		List<T> outList = new ArrayList<>(sourceList.size());
		for (Object source : sourceList) {
			if (source == null) {
				continue;
			}
			T bean = BeanUtil.copyProperties(source, targetClazz);
			outList.add(bean);
		}
		return outList;
	}




}
