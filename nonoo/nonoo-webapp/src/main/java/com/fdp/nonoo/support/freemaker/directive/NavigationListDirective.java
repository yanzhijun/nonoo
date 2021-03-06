package com.fdp.nonoo.support.freemaker.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.fdp.nonoo.common.Filter;
import com.fdp.nonoo.common.Order;
import com.fdp.nonoo.entity.Navigation;
import com.fdp.nonoo.service.NavigationService;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Component("navigationListDirective")
public class NavigationListDirective extends BaseDirective {
	private static final String DEFAULT_VARIABLE_NAME = "navigations";

	@Resource(name = "navigationService")
	private NavigationService navigationService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		boolean bool = isUseCache(env, params);
		String cacheName = getCacheName(env, params);
		Integer count = getCount(params);
		List<Filter> filters = getFilterParameter(params, Navigation.class,new String[0]);
		List<Order> orders = getOrderParameter(params, new String[0]);
		List<Navigation> navigations;
		if (bool) {
			navigations = navigationService.findList(count, filters, orders,cacheName);
		} else {
			navigations = navigationService.findList(count, filters, orders);
		}
		setVariable(DEFAULT_VARIABLE_NAME, navigations, env, body);
	}
}