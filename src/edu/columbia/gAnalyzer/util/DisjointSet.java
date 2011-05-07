package edu.columbia.gAnalyzer.util;

import java.util.*;
/**
 * Connected Component of graph in edge list format.
 * 
 * 
 *  By reference to open source API by IBM
 *  /*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

 

public class DisjointSet {

	/**
	 * A node in the disjoint set forest.  Each tree in the forest is
	 * a disjoint set, where the root of the tree is the set representative.
	 */
	private static int number_connected_components=0;
	
	public int get_number_connected_components() {
		return number_connected_components;
	}
	
	public DisjointSet() {
		number_connected_components = 0;
	}

	private static class Node {
		/** The node rank used for union by rank optimization */
		int rank;
		/** The parent of this node in the tree. */
		Object parent;

		Node(Object parent, int rank) {
			this.parent = parent;
			this.rank = rank;
		}
	}

	/**
	 * Map of Object -> Node, where each key is an object in the
	 * disjoint set, and the Node represents its position and rank
	 * within the set.
	 */
	private final HashMap objectsToNodes = new HashMap();

	/**
	 * Returns the set token for the given object, or null if the
	 * object does not belong to any set.  All object
	 * in the same set have an identical set token.
	 * @param o The object to return the set token for
	 * @return The set token, or <code>null
	 */
	public Object findSet(Object o) {
		DisjointSet.Node node = (DisjointSet.Node) objectsToNodes.get(o);
		if (node == null)
			return null;
		if (o != node.parent)
			node.parent = findSet(node.parent);
		return node.parent;
	}

	/**
	 * Adds a new set to the group of disjoint sets for the given object.
	 * It is assumed that the object does not yet belong to any set.
	 * @param o The object to add to the set
	 */
	public void makeSet(Object o) {
		objectsToNodes.put(o, new Node(o, 0));
		number_connected_components++;
	}

	/**
	 * Removes all elements belonging to the set of the given object.
	 * @param o The object to remove
	 */
	public void removeSet(Object o) {
		Object set = findSet(o);
		if (set == null)
			return;
		for (Iterator it = objectsToNodes.keySet().iterator(); it.hasNext();) {
			Object next = it.next();
			//remove the set representative last, otherwise findSet will fail
			if (next != set && findSet(next) == set)
				it.remove();
		}
		objectsToNodes.remove(set);
	}

	/**
	 * Copies all objects in the disjoint set to the provided list
	 * @param list The list to copy objects into
	 */
	public void toList(List list) {
		list.addAll(objectsToNodes.keySet());
	}

	/**
	 * Unions the set represented by token x with the set represented by 
	 * token y. Has no effect if either x or y is not in the disjoint set, or
	 * if they already belong to the same set.
	 * @param x The first set to union
	 * @param y The second set to union
	 */
	public void union(Object x, Object y) {
		Object setX = findSet(x);
		Object setY = findSet(y);
		if (setX == null || setY == null || setX == setY)
			return;
		DisjointSet.Node nodeX = (DisjointSet.Node) objectsToNodes.get(setX);
		DisjointSet.Node nodeY = (DisjointSet.Node) objectsToNodes.get(setY);
		//join the two sets by pointing the root of one at the root of the other
		if (nodeX.rank > nodeY.rank) {
			nodeY.parent = x;
		} else {
			nodeX.parent = y;
			if (nodeX.rank == nodeY.rank)
				nodeY.rank++;
		}
		number_connected_components--;
	}

}
