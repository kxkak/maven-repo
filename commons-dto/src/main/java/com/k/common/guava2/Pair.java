/**
 * Project: tuangou-remote-common
 * 
 * File Created at 2012-11-26
 * $Id$
 * 
 * Copyright 2010 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.k.common.guava2;

import java.io.Serializable;

public class Pair<A, B> implements Serializable, Cloneable {

	public static <A, B> Pair<A, B> of(A first, B second) {
		return new Pair<A, B>(first, second);
	}
	private final A first;
	private final B second;

	public Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }
}
