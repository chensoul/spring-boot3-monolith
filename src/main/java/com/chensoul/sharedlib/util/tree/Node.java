package com.chensoul.sharedlib.util.tree;

import java.io.Serializable;

public interface Node<T> extends Comparable<Node<T>>, Serializable {

	T getId();

	Node<T> setId(T var1);

	T getParentId();

	Node<T> setParentId(T var1);

	CharSequence getName();

	Node<T> setName(CharSequence var1);

	Comparable<?> getWeight();

	Node<T> setWeight(Comparable<?> var1);

	default int compareTo(Node node) {
		Comparable weight = this.getWeight();
		if (null != weight) {
			Comparable weightOther = node.getWeight();
			return weight.compareTo(weightOther);
		} else {
			return 0;
		}
	}

}
