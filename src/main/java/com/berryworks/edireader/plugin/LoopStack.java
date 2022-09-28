/*
 * Copyright 2005-2015 by BerryWorks Software, LLC. All rights reserved.
 *
 * This file is part of EDIReader. You may obtain a license for its use directly from
 * BerryWorks Software, and you may also choose to use this software under the terms of the
 * GPL version 3. Other products in the EDIReader software suite are available only by licensing
 * with BerryWorks. Only those files bearing the GPL statement below are available under the GPL.
 *
 * EDIReader is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * EDIReader is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with EDIReader.  If not,
 * see <http://www.gnu.org/licenses/>.
 */

package com.berryworks.edireader.plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Stack of LoopContext instances used while parsing an EDI document.
 * <p>
 * As EDIReader parses each segment within an EDI document, it uses a LoopStack
 * to track the nested segment loops in which the current segment occurs.
 * The bottom of the stack is a LoopContext that describes the implied outer segment loop.
 * As each new level of loop nesting is entered, a LoopContext is pushed onto the stack.
 *
 * @see LoopContext
 */
public class LoopStack {

    private final List<LoopContext> stack;

    /**
     * Construct a LoopStack with a single LoopContext corresponding
     * to the outer loop.
     */
    public LoopStack() {
        stack = new ArrayList<>();
        push(new LoopContext(""));
    }

    /**
     * Returns a String representation of the stack.
     *
     * @return String representation
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        LoopContext bottom = getBottom();
        if ("".equals(bottom.getLoopName())) {
            if (stack.size() == 1)
                return "/";
        } else
            result.append("/").append(bottom.getLoopName());

        for (int i = 1; i < stack.size(); i++) {
            result.append('/');
            result.append(stack.get(i).getLoopName());
        }
        return result.toString();
    }

    /**
     * Pushes a LoopContext onto the top of the stack.
     *
     * @param context - the LoopContext to be pushed
     */
    public void push(LoopContext context) {
        stack.add(context);
    }

    /**
     * Removes the LoopContext at the top of the stack.
     *
     * @return the LoopContext
     */
    public LoopContext pop() {
        return stack.remove(stack.size() - 1);
    }

    /**
     * Returns the LoopContext at the bottom of the stack.
     *
     * @return the LoopContext
     */
    public LoopContext getBottom() {
        return stack.get(0);
    }

    /**
     * Returns the LoopContext at the top of the stack.
     *
     * @return the LoopContext
     */
    public LoopContext getTop() {
        return stack.get(stack.size() - 1);
    }

    /**
     * Replace the LoopContext at the bottom of the stack
     * with the one provided.
     *
     * @param bottom - the replacement LoopContext
     */
    public void setBottom(LoopContext bottom) {
        stack.set(0, bottom);
    }

    /**
     * Returns the stack size.
     *
     * @return int size
     */
    public int getSize() {
        return stack.size();
    }
}
