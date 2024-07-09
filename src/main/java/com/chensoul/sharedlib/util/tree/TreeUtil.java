package com.chensoul.sharedlib.util.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 树工具类
 *
 * @author liangbaikai
 */
public class TreeUtil {
	/**
	 * 构建单root节点树
	 *
	 * @param <E>      ID类型
	 * @param list     源数据集合
	 * @param parentId 最顶层父id值 一般为 0 之类
	 * @return List
	 * @since 5.7.2
	 */
	public static <E> Tree<E> buildSingle(List<TreeNode<E>> list, E parentId) {
		return buildSingle(list, parentId, TreeNodeConfig.DEFAULT_CONFIG, new DefaultNodeParser<>());
	}

	public static <E> Tree<E> buildSingle(List<TreeNode<E>> list, E parentId, TreeNodeConfig treeNodeConfig) {
		return buildSingle(list, parentId, treeNodeConfig, new DefaultNodeParser<>());
	}

	/**
	 * 构建单root节点树
	 *
	 * @param <T>        转换的实体 为数据源里的对象类型
	 * @param <E>        ID类型
	 * @param list       源数据集合
	 * @param parentId   最顶层父id值 一般为 0 之类
	 * @param nodeParser 转换器
	 * @return List
	 * @since 5.7.2
	 */
	public static <T, E> Tree<E> buildSingle(List<T> list, E parentId, NodeParser<T, E> nodeParser) {
		return buildSingle(list, parentId, TreeNodeConfig.DEFAULT_CONFIG, nodeParser);
	}

	/**
	 * 树构建
	 *
	 * @param <E>      ID类型
	 * @param list     源数据集合
	 * @param parentId 最顶层父id值 一般为 0 之类
	 * @return List
	 */
	public static <E> List<Tree<E>> build(List<TreeNode<E>> list, E parentId) {
		return build(list, parentId, TreeNodeConfig.DEFAULT_CONFIG, new DefaultNodeParser<>());
	}

	public static <E> List<Tree<E>> build(List<TreeNode<E>> list, E parentId, TreeNodeConfig treeNodeConfig) {
		return build(list, parentId, treeNodeConfig, new DefaultNodeParser<>());
	}

	/**
	 * 树构建
	 *
	 * @param <T>        转换的实体 为数据源里的对象类型
	 * @param <E>        ID类型
	 * @param list       源数据集合
	 * @param parentId   最顶层父id值 一般为 0 之类
	 * @param nodeParser 转换器
	 * @return List
	 */
	public static <T, E> List<Tree<E>> build(List<T> list, E parentId, NodeParser<T, E> nodeParser) {
		return build(list, parentId, TreeNodeConfig.DEFAULT_CONFIG, nodeParser);
	}

	/**
	 * 树构建
	 *
	 * @param <T>            转换的实体 为数据源里的对象类型
	 * @param <E>            ID类型
	 * @param list           源数据集合
	 * @param rootId         最顶层父id值 一般为 0 之类
	 * @param treeNodeConfig 配置
	 * @param nodeParser     转换器
	 * @return List
	 */
	public static <T, E> List<Tree<E>> build(List<T> list, E rootId, TreeNodeConfig treeNodeConfig,
											 NodeParser<T, E> nodeParser) {
		return buildSingle(list, rootId, treeNodeConfig, nodeParser).getChildren();
	}

	/**
	 * 构建单root节点树
	 *
	 * @param <T>            转换的实体 为数据源里的对象类型
	 * @param <E>            ID类型
	 * @param list           源数据集合
	 * @param rootId         最顶层父id值 一般为 0 之类
	 * @param treeNodeConfig 配置
	 * @param nodeParser     转换器
	 * @return List
	 * @since 5.7.2
	 */
	public static <T, E> Tree<E> buildSingle(List<T> list, E rootId, TreeNodeConfig treeNodeConfig,
											 NodeParser<T, E> nodeParser) {
		return TreeSupplier.of(rootId, treeNodeConfig).append(list, nodeParser).get();
	}

	/**
	 * 树构建，按照权重排序
	 *
	 * @param <E>    ID类型
	 * @param map    源数据Map
	 * @param rootId 最顶层父id值 一般为 0 之类
	 * @return List
	 * @since 5.6.7
	 */
	public static <E> List<Tree<E>> build(Map<E, Tree<E>> map, E rootId) {
		return buildSingle(map, rootId).getChildren();
	}

	/**
	 * 单点树构建，按照权重排序
	 *
	 * @param <E>    ID类型
	 * @param map    源数据Map
	 * @param rootId 根节点id值 一般为 0 之类
	 * @return {@link Tree}
	 * @since 5.7.2
	 */
	public static <E> Tree<E> buildSingle(Map<E, Tree<E>> map, E rootId) {
		final Tree<E> tree = getFirstNoneNull(map.values());
		if (null != tree) {
			final TreeNodeConfig config = tree.getConfig();
			return TreeSupplier.of(rootId, config).append(map).get();
		}

		return createEmptyNode(rootId);
	}

	public static <T> T getFirstNoneNull(Iterable<T> iterable) {
		if (null == iterable) {
			return null;
		}
		Iterator<T> iterator = iterable.iterator();
		if (null != iterator) {
			while (iterator.hasNext()) {
				final T next = iterator.next();
				if (null != next) {
					return next;
				}
			}
		}
		return null;
	}


	/**
	 * 获取ID对应的节点，如果有多个ID相同的节点，只返回第一个。<br>
	 * 此方法只查找此节点及子节点，采用递归深度优先遍历。
	 *
	 * @param <T>  ID类型
	 * @param node 节点
	 * @param id   ID
	 * @return 节点
	 * @since 5.2.4
	 */
	public static <T> Tree<T> getNode(Tree<T> node, T id) {
		if (Objects.equals(id, node.getId())) {
			return node;
		}

		final List<Tree<T>> children = node.getChildren();
		if (null == children) {
			return null;
		}

		// 查找子节点
		Tree<T> childNode;
		for (Tree<T> child : children) {
			childNode = child.getNode(id);
			if (null != childNode) {
				return childNode;
			}
		}

		// 未找到节点
		return null;
	}

	/**
	 * 获取所有父节点名称列表
	 *
	 * <p>
	 * 比如有个人在研发1部，他上面有研发部，接着上面有技术中心<br>
	 * 返回结果就是：[研发一部, 研发中心, 技术中心]
	 *
	 * @param <T>                节点ID类型
	 * @param node               节点
	 * @param includeCurrentNode 是否包含当前节点的名称
	 * @return 所有父节点名称列表，node为null返回空List
	 * @since 5.2.4
	 */
	public static <T> List<CharSequence> getParentsName(Tree<T> node, boolean includeCurrentNode) {
		final List<CharSequence> result = new ArrayList<>();
		if (null == node) {
			return result;
		}

		if (includeCurrentNode) {
			result.add(node.getName());
		}

		Tree<T> parent = node.getParent();
		while (null != parent) {
			result.add(parent.getName());
			parent = parent.getParent();
		}
		return result;
	}

	/**
	 * 创建空Tree的节点
	 *
	 * @param id  节点ID
	 * @param <E> 节点ID类型
	 * @return {@link Tree}
	 * @since 5.7.2
	 */
	public static <E> Tree<E> createEmptyNode(E id) {
		return new Tree<E>().setId(id);
	}

}
