/*
 * Copyright (C) 2013 The Calrissian Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.calrissian.accumulorecipes.rangestore.iterator;

import org.apache.accumulo.core.client.IteratorSetting;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.iterators.Filter;
import org.apache.accumulo.core.iterators.IteratorEnvironment;
import org.apache.accumulo.core.iterators.SortedKeyValueIterator;

import java.io.IOException;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.splitPreserveAllTokens;
import static org.calrissian.accumulorecipes.commons.support.Constants.NULL_BYTE;

/**
 * Filters out all intervals that have a lower bound greater than the criteria lower bound.
 * This should only be run against data that is upper bound indexed.
 */
public class ReverseScanFilter extends Filter {

    private static final String LOW_BOUND_OPTION = "lowbound";

    private String queryLowBound = "";

    /**
     * A convenience method for setting the filter condition for the iterator.
     *
     * @param iterConfig    Iterator settings to configure
     * @param queryLowBound The normalized representation of the criteria low bound for a range.
     */
    public static void setQueryLowBound(IteratorSetting iterConfig, String queryLowBound) {
        iterConfig.addOption(LOW_BOUND_OPTION, queryLowBound);
    }

    @Override
    public void init(SortedKeyValueIterator<Key, Value> source, Map<String, String> options, IteratorEnvironment env) throws IOException {
        super.init(source, options, env);

        if (options.containsKey(LOW_BOUND_OPTION))
            queryLowBound = options.get(LOW_BOUND_OPTION);

    }

    @Override
    public boolean accept(Key key, Value value) {
        String vals[] = splitPreserveAllTokens(key.getRow().toString(), NULL_BYTE);
        return vals.length == 3 && vals[2].compareTo(queryLowBound) < 0;
    }
}
