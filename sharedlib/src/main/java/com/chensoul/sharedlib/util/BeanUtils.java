package com.chensoul.sharedlib.util;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.util.CollectionUtils;

/**
 * @author zhijun.chen
 * @since 0.0.1
 */
public class BeanUtils extends org.springframework.beans.BeanUtils {
	/**
	 * 获取所有的属性值为空属性名数组
	 *
	 * @param source 对象
	 * @return 属性值
	 */
	public static String[] getNullPropertyNames(Object source) {
		BeanWrapper beanWrapper = new BeanWrapperImpl(source);
		PropertyDescriptor[] pds = beanWrapper.getPropertyDescriptors();
		List<String> nullPropertyNames = new ArrayList<>();
		for (PropertyDescriptor pd : pds) {
			String propertyName = pd.getName();
			if (beanWrapper.getPropertyValue(propertyName) == null) {
				nullPropertyNames.add(propertyName);
			}
		}
		return nullPropertyNames.toArray(new String[nullPropertyNames.size()]);
	}

	public static Map<String, Object> beanToMap(Object bean) {
		return null == bean ? null : BeanMap.create(bean);
	}

	public static <T> T mapToBean(Map<String, ?> map, Class<T> clazz) {
		T bean = null;
		try {
			bean = BeanUtils.instantiateClass(clazz);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		BeanMap.create(bean).putAll(map);
		return bean;
	}

	public static <T> List<Map<String, Object>> beansToMaps(List<T> beans) {
		return CollectionUtils.isEmpty(beans) ? Collections.emptyList() : (List) beans.stream().map(BeanUtils::beanToMap).collect(Collectors.toList());
	}

	public static <T> List<T> mapsToBeans(List<? extends Map<String, ?>> maps, Class<T> clazz) {
		return CollectionUtils.isEmpty(maps) ? Collections.emptyList() : (List) maps.stream().map((e) -> mapToBean(e, clazz)).collect(Collectors.toList());
	}
}
