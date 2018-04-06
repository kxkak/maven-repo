/**
 * Project: tuangou-remote-common
 * 
 * File Created at 2013-5-16
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

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import java.util.Collection;

public final class Predicates2 {

	public static <T> Predicate<Identifiable<T>> identityIn(Collection<T> identities) {
        return Predicates.compose(Predicates.<T>in(identities), Functions2.<Identifiable<T>, T>newIdFunction());
	}

    private Predicates2() {
    }

}
