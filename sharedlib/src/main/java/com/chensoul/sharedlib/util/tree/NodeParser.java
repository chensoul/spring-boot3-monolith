package com.chensoul.sharedlib.util.tree;

/**
 * 树节点解析器 可以参考{@link DefaultNodeParser}
 */
@FunctionalInterface
public interface NodeParser<T, E> {

	/**
	 * @param object   源数据实体
	 * @param treeNode 树节点实体
	 */
	void parse(T object, Tree<E> treeNode);

}
