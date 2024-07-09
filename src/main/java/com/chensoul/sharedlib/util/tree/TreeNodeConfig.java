package com.chensoul.sharedlib.util.tree;

import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 树配置属性相关
 */
@Data
@Accessors(chain = true)
public class TreeNodeConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 默认属性配置对象
	 */
	public static TreeNodeConfig DEFAULT_CONFIG = new TreeNodeConfig();

	// 属性名配置字段
	private String idKey = "id";

	private String parentIdKey = "parentId";

	private String weightKey = "weight";

	private String nameKey = "name";

	private String childrenKey = "children";

	private Boolean reversed = false;

	// 可以配置递归深度 从0开始计算 默认此配置为空,即不限制
	private Integer deep;

}
