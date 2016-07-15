/*
 * Copyright 2005-2016 by BerryWorks Software, LLC. All rights reserved.
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

package com.berryworks.edireader;

import com.berryworks.edireader.plugin.LoopDescriptor;
import com.berryworks.edireader.plugin.PluginControllerImpl;
import com.berryworks.edireader.plugin.PluginPreparation;
import com.berryworks.edireader.tokenizer.Tokenizer;

import java.util.List;

/**
 * Parent class for all EDIReader plugins.
 * <p>
 * <b>Naming plugin classes.</b>
 * The class should extend com.berryworks.edireader.Plugin and must be named
 * precisely according to a specific pattern. If the plugin is for an
 * ANSI X.12 transaction set, the class name must be
 * ANSI_<i>nnn</i> where <i>nnn</i>
 * is the 3-digit number that appears in the ST segment to designate the particular
 * transaction set.
 * For a UN/EDIFACT message type, the class name must be
 * EDIFACT_<i>name</i> where <i>name</i>
 * is the symbolic name for the message type that appears in the UNH segment.
 * For instance, a plugin for an EDIFACT purchase order message would have the
 * class name EDIFACT_ORDERS.
 * <p>
 * EDIReader uses this class name to make the runtime linkage to the plugin.
 * Once it has parsed the ST segment (or UNH segment for EDIFACT) it
 * forms the classname that the plugin would have if it is available and
 * uses the classloader to locate a class of this name in the CLASSPATH.
 * If no such class exists, then EDIReader continues without a plugin and
 * generates XML that does not reflect the internal looping structure of the
 * document.
 * <p>
 * For this linkage to work properly, the fully-qualified class name must
 * match. Therefore, your plugin must bear a package name of
 * com.berryworks.edireader.plugin
 * although it can be compiled separately and does not have to be placed
 * inside any particular jar file.
 * <p>
 * <b>LoopDescriptors.</b>
 * The essence of a Plugin is an array of LoopDescriptors. An EDIReader plugin is expected
 * to subclass Plugin and initialize the loops array with loop descriptors.
 * Each LoopDescriptor expresses a specific situation that corresponds to the entry or exit of a
 * loop instance while parsing.
 * Refer to the documentation for LoopDescriptor for further details.
 * <p>
 * <b>PluginController.</b>
 * A parser for a particular EDI standard, typically AnsiReader or EdifactReader,
 * uses an instance of a PluginController to interact with the plugin while parsing the
 * segments of an EDI document.
 * For each segment encountered within a document, the parser calls the transition method
 * on the PluginController to determine if the segment corresponds to a loop transaction,
 * either the entry of a new loop or the completion of a loop. The transition() method then
 * in turn calls the query() method on the plugin, which considers the LoopDescriptors
 * associated with the segment type to determine if one applies to the current context.
 * The query() method considers the LoopDescriptors in the order in which they were
 * provided in the loops array. The first one that is determined to "match" the context
 * expressed in the query arguments is returned by the query method. Therefore, the ordering
 * of the LoopDescriptors for a given segment type is very important as described in the
 * documentation for LoopDescriptor.
 * <p>
 * <b>Plugin Optimization.</b>
 * The description above suggests that the entire array of LoopDescriptors in
 * a plugin is examined serially for each segment within an EDI document.
 * This is logically but not literally true.
 * For performance reasons, an optimized form of a plugin is created when a
 * plugin class is loaded so that the LoopDescriptors associated with a
 * particular segment type can be accessed efficiently. The PluginPreparation
 * class is for this purpose.
 *
 * @see com.berryworks.edireader.plugin.LoopDescriptor
 * @see PluginController
 * @see com.berryworks.edireader.plugin.PluginPreparation
 */
public abstract class Plugin {

    public static final String ANY_CONTEXT = "*";
    public static final String INITIAL_CONTEXT = "/";
    public static final String CURRENT = ".";
    protected static int pluginsLoaded;

    /**
     * LoopDescriptor[] is a table of state transfer information specific to a
     * particular document type. The transition method uses this table to
     * determine if a state transition occurred or not.
     */
    protected LoopDescriptor[] loops;

    protected boolean debug;
    protected final String documentType;
    protected final String documentName;
    protected PluginPreparation optimizedForm;
    private boolean validating;

    public Plugin(String documentType, String documentName) {
        this.documentType = documentType;
        this.documentName = documentName;
        pluginsLoaded++;
    }

    protected Plugin(String documentType, String documentName, boolean validating) {
        this(documentType, documentName);
        this.validating = validating;
    }

    /**
     * Perform any initialization needed for the plugin before use with a new document.
     * <p>
     * The only cases where this is needed is for plugins that have state. Most plugins are stateless and therefore
     * an instance of a plugin can be reused for many documents. However, it is possible to develop a subclass of Plugin
     * that maintains state. For example, a FilteringPlugin might need to make decisions based on what segment types
     * have been seen previously in a given document. In such a case, you may override the init() method in order to
     * reset the state before starting a new document. In developing such a state-bearing plugin, you must carefully
     * consider thread safety issues for multi-threaded environments. The use of ThreadLocal is recommended in such
     * cases.
     */
    public void init() {
    }

    /**
     * Get the document type (for example, "837")
     *
     * @return The documentType value
     */
    public String getDocumentType() {
        return documentType;
    }

    /**
     * Get the readable name for the document (for example, "Health Care Claim")
     *
     * @return The documentName value
     */
    public String getDocumentName() {
        return documentName;
    }

    /**
     * Get the array of LoopDescriptors
     *
     * @return LoopDescriptors
     */
    public LoopDescriptor[] getLoopDescriptors() {
        return loops;
    }

    /**
     * Query the plugin about a loop that starts with a designated segment type,
     * given that you are already within a particular loop.
     *
     * @param segment          type of segment encountered
     * @param currentLoopStack stack representing nested loops in current state
     * @param currentLevel     nesting level of current state
     * @return descriptor matching query parameters, or null if none
     */
    public LoopDescriptor query(String segment, String currentLoopStack, int currentLevel) {
        LoopDescriptor result = null;
        if (debug) trace("plugin query for segment " + segment);

        if (currentLoopStack == null)
            currentLoopStack = "*";

        if (loops == null)
            return null;

        if (optimizedForm == null)
            throw new RuntimeException(
                    "Internal error: plugin not properly constructed");

        List<LoopDescriptor> descriptorList = optimizedForm.getList(segment);
        if (descriptorList == null) {
            if (debug) trace("No descriptors found");
            return null;
        }
        if (debug) trace("Number of descriptors found: " + descriptorList.size());

        for (LoopDescriptor descriptor : descriptorList) {
            if (descriptor.getFirstSegment().equals(segment)) {
                int levelContext = descriptor.getLevelContext();
                if (debug) trace("checking level context " + levelContext);
                if (levelContext > -1) {
                    if (levelContext == currentLevel) {
                        result = descriptor;
                        break;
                    }
                    continue;
                }
                String candidateContext = descriptor.getLoopContext();
                if (debug) trace("checking loop context " + candidateContext +
                        " with current loop stack " + currentLoopStack);
                if ("*".equals(candidateContext)) {
                    result = descriptor;
                    break;
                } else if (candidateContext.startsWith("/")
                        && candidateContext.length() > 1
                        && currentLoopStack.startsWith(candidateContext)) {
                    if (debug) trace("startsWith satisfied");
                    result = descriptor;
                    break;
                } else if (currentLoopStack.endsWith(candidateContext)) {
                    result = descriptor;
                    break;
                }
            } else {
                throw new RuntimeException(
                        "Internal error: optimized plugin structure invalid");
            }
        }
        // A loop descriptor with a null loop name serves as a NOT rule.
        // No further loop descriptors are considered, and query returns a null
        // indicating that no transition should occur. This provides a way to
        // express a specific context where the appearance of a segment does NOT
        // mark the entry of a new loop.
        if (result != null && result.getName() == null)
            result = null;
        return result;
    }

    private void trace(String s) {
        EDIReader.trace(s);
    }

    public void debug(boolean d) {
        this.debug = d;
    }

    public static int getCount() {
        return pluginsLoaded;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Plugin ").append(getClass().getName());
        result.append("\n  ").append(getDocumentName()).append(" (").append(getDocumentType()).append(')');
        if (loops != null) {
            for (LoopDescriptor loop : loops)
                result.append('\n').append(loop.toString());
        }
        return result.toString();
    }

    public void prepare() {
        optimizedForm = new PluginPreparation(loops);
    }

    public boolean isValidating() {
        return validating;
    }

    public PluginControllerImpl createController(String standard, Tokenizer tokenizer) {
        return new PluginControllerImpl(standard, tokenizer);
    }

    protected LoopDescriptor[] concatenate(LoopDescriptor[] descriptorsA, LoopDescriptor[] descriptorsB) {
        LoopDescriptor[] result = new LoopDescriptor[descriptorsA.length + descriptorsB.length];
        int loopsIndex = 0;
        for (LoopDescriptor d : descriptorsA) {
            result[loopsIndex++] = d;
        }
        for (LoopDescriptor d : descriptorsB) {
            result[loopsIndex++] = d;
        }
        return result;
    }

    public String getDocumentVersion() {
        // Try to derive the version from the name of the class
        // based on the naming convention used by EDIReader.
        String result = null;

        final String simpleName = this.getClass().getSimpleName();
        final String[] split = simpleName.split("_");
        if (split.length == 4) {
            result = split[3];
        }

        return result;
    }

    public PluginDiff compare(Plugin pluginB) {
        final PluginDiff result = new PluginDiff(); // not match by default
        if (pluginB == null) {
            return result.mismatch("second plugin is null");
        }

        if (!this.getDocumentType().equals(pluginB.getDocumentType())) {
            return result.mismatch("types differ");
        }

        if (!this.getDocumentName().equals(pluginB.getDocumentName())) {
            return result.mismatch("names differ");
        }

        final LoopDescriptor[] loopsOfB = pluginB.loops;
        if (loops == null) {
            if (loopsOfB != null) {
                return result.mismatch("second plugin has non-null loops while this plugin does not");
            }
        } else {
            if (loopsOfB == null) {
                return result.mismatch("second plugin has null loops while this plugin does not");
            }
            if (loops.length != loopsOfB.length) {
                return result.mismatch("second plugin has a different number of loops than this plugin");
            }

//            TODO iterator through the loops and check for match
        }

        result.setMatch(true);

        return result;
    }

    public static class PluginDiff {

        private boolean match;
        private String reason;

        public boolean isMatch() {
            return match;
        }

        public void setMatch(boolean match) {
            this.match = match;
        }

        public String getReason() {
            return isMatch() ? "matches" : reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public PluginDiff mismatch(String s) {
            setMatch(false);
            setReason(s);
            return this;
        }
    }
}
