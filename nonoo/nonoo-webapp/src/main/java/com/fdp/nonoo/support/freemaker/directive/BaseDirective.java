package com.fdp.nonoo.support.freemaker.directive;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.fdp.nonoo.common.Filter;
import com.fdp.nonoo.common.Order;
import com.fdp.nonoo.util.FreemarkerUtils;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public abstract class BaseDirective implements TemplateDirectiveModel {
	private static final String USE_CACHE = "useCache";
	private static final String CACHE_REGION = "cacheRegion";
	private static final String ID = "id";
	private static final String COUNT = "count";
	private static final String ORDER_BY = "orderBy";
	private static final String REPALCE_PARTTEN = "\\s*,\\s*";
	private static final String SPLIT_PARTTEN = "\\s+";

	protected boolean isUseCache(Environment paramEnvironment,
			Map<String, TemplateModel> paramMap) throws TemplateModelException {
		Boolean isUserCache = (Boolean) FreemarkerUtils.getParameter(USE_CACHE,Boolean.class, paramMap);
		return (isUserCache != null) ? isUserCache.booleanValue() : true;
	}

	protected String getCacheName(Environment environment,
			Map<String, TemplateModel> paramMap) throws TemplateModelException {
		String cacheName = (String) FreemarkerUtils.getParameter(CACHE_REGION, String.class, paramMap);
		return (cacheName != null) ? cacheName : environment.getTemplate().getName();
	}

	protected Long getId(Map<String, TemplateModel> param)
			throws TemplateModelException {
		return (Long) FreemarkerUtils.getParameter(ID, Long.class, param);
	}

	protected Integer getCount(Map<String, TemplateModel> param)
			throws TemplateModelException {
		return (Integer) FreemarkerUtils.getParameter(COUNT, Integer.class,param);
	}

	protected List<Filter> getFilterParameter(
			Map<String, TemplateModel> paramMap, Class<?> paramClass,
			String[] params) throws TemplateModelException {
		List<Filter> filterList = new ArrayList<Filter>();
		PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(paramClass);
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			String parameterName = propertyDescriptor.getName();
			Class<?> classz = propertyDescriptor.getPropertyType();
			if ((ArrayUtils.contains(params, parameterName))|| (!(paramMap.containsKey(parameterName)))){
				continue;
			}
			
			Object parameter = FreemarkerUtils.getParameter(parameterName,classz, paramMap);
			
			if (parameter==null){
				filterList.add(Filter.isNull(parameterName));
			}else{
			filterList.add(Filter.eq(parameterName, parameter));
			}
		}
		return filterList;
	}

	protected List<Order> getOrderParameter(
			Map<String, TemplateModel> paramMap, String[] params)
			throws TemplateModelException {
		String str1 = StringUtils.trim((String) FreemarkerUtils.getParameter(ORDER_BY, String.class, paramMap));
		List<Order> orderList = new ArrayList<Order>();
		if (StringUtils.isNotEmpty(str1)) {
			String[] parameters = str1.split(REPALCE_PARTTEN);
			for (String parameter : parameters) {
				if (!(StringUtils.isNotEmpty(parameter))){
					continue;
				}
				String property = null;
				Order.Direction direction = null;
				String[] orders = parameter.split(SPLIT_PARTTEN);
				if (orders.length == 1) {
					property = orders[0];
				} else {
					if (orders.length < 2)
						continue;
					property = orders[0];
					try {
						direction = Order.Direction.valueOf(orders[1]);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
				if (ArrayUtils.contains(params, property)){
					continue;
				}
				orderList.add(new Order(property, direction));
			}
		}
		return orderList;
	}

	protected void setVariable(String parameter, Object object,
			Environment environment, TemplateDirectiveBody body)
			throws TemplateException, IOException {
		TemplateModel template = FreemarkerUtils.getVariable(parameter,environment);
		FreemarkerUtils.setVariable(parameter, object, environment);
		body.render(environment.getOut());
		FreemarkerUtils.setVariable(parameter, template, environment);
	}

	protected void setVariable(Map<String, Object> param,
			Environment env, TemplateDirectiveBody body)
			throws TemplateException, IOException {
		Map<String, Object> templateMap = new HashMap<String, Object>();
		Iterator<String> iterator = param.keySet().iterator();
		while (iterator.hasNext()) {
			String name = (String) iterator.next();
			TemplateModel template = FreemarkerUtils.getVariable(name,env);
			templateMap.put(name, template);
		}
		FreemarkerUtils.setVariables(param, env);
		body.render(env.getOut());
		FreemarkerUtils.setVariables(templateMap, env);
	}
}