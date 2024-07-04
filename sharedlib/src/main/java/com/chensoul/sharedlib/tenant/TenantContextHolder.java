package com.chensoul.sharedlib.tenant;

import lombok.experimental.UtilityClass;

/**
 * @author zhijun.chen
 * @since 0.0.1
 */
@UtilityClass
public class TenantContextHolder {
	private final ThreadLocal<String> CONTEXT = new InheritableThreadLocal<>();

	public String getTenantId() {
		return CONTEXT.get();
	}

	public void setTenantId(String tenantId) {
		CONTEXT.set(tenantId);
	}

	public void clear() {
		CONTEXT.remove();
	}
}
