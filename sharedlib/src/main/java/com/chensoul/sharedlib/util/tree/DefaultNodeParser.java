package com.chensoul.sharedlib.util.tree;

import java.util.Map;
import org.springframework.util.CollectionUtils;

/**
 * 默认的简单转换器
 */
public class DefaultNodeParser<T> implements NodeParser<TreeNode<T>, T> {

	@Override
	public void parse(TreeNode<T> treeNode, Tree<T> tree) {
		tree.setId(treeNode.getId());
		tree.setParentId(treeNode.getParentId());
		tree.setWeight(treeNode.getWeight());
		tree.setName(treeNode.getName());

		// 扩展字段
		final Map<String, Object> extra = treeNode.getExtra();
		if (!CollectionUtils.isEmpty(extra)) {
			extra.forEach(tree::putExtra);
		}
	}

}
