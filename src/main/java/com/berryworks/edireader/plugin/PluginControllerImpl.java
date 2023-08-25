/*
 * Copyright 2005-2019 by BerryWorks Software, LLC. All rights reserved.
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

import com.berryworks.edireader.EDISyntaxException;
import com.berryworks.edireader.Plugin;
import com.berryworks.edireader.PluginController;
import com.berryworks.edireader.tokenizer.Tokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Set;

import static com.berryworks.edireader.Plugin.CURRENT;

/**
 * Determines and maintains state transitions for the segment looping structure
 * within a particular EDI document.
 * <p>
 * An EDI parser delegates the job of detecting segment loop boundaries
 * to a PluginController. This allows the EDI parsers for ANSI and EDIFACT
 * to be fully consistent with their use of plugins and focus on the specifics of
 * the particular EDI standard.
 * <p>
 * This PluginControllerImpl provides the normal
 * segment loop support based on LoopDescriptors in Plugins.
 * It is possible, however, to extend this behavior by creating a subclass of
 * PluginControllerImpl and Plugin. A ValidatingPlugin is one example,
 * which provides for certain EDI validation rules, beyond those applied by
 * normal EDIReader parsing, to be applied while a document is being parsed.
 * Another example is a FilteringPlugin, which allows a plugin to provide
 * custom logic to filter out certain LoopDescriptors based on run-time decisions.
 *
 * @see com.berryworks.edireader.Plugin
 * @see com.berryworks.edireader.plugin.LoopDescriptor
 */
public class PluginControllerImpl extends PluginController {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
    protected boolean enabled;
    protected final String standard;
    protected String documentType;
    protected Plugin plugin;
    protected LoopStack loopStack;
    protected final Tokenizer tokenizer;

    /**
     * Name of the current loop. The implicit outer loop is represented by the
     * name "/".
     */
    protected String currentLoopName;

    /**
     * Descriptor that caused us to enter the loop we are now in.
     */
    protected LoopDescriptor loopDescriptor;

    /**
     * Number of loops that were closed as the result of the most recent
     * transition. A transition that re-enters the implicit outer loop does not
     * consider the outer loop in this count.
     */
    protected int numberOfLoopsClosed;
    private final Set<String> resultFlags = new HashSet<>();

    /**
     * Construct a PluginControllerImpl
     *
     * @param standard  - name of EDI standard (for example: "EDIFACT" or "ANSI")
     * @param tokenizer - reference to the Tokenizer to provide context for syntax exceptions
     */
    public PluginControllerImpl(String standard, Tokenizer tokenizer) {
        this.standard = standard;
        this.tokenizer = tokenizer;
        reset();
    }

    /**
     * Initialize the state.
     * <p>
     * Also used to reset the state so that the same controller and plugin be used with another document
     * of the same type.
     */
    public void reset() {
        loopStack = new LoopStack();
        currentLoopName = "/";
        loopDescriptor = new LoopDescriptor(currentLoopName, "", 0, "/");
    }

    /**
     * Compute a state transition that may have occurred as the result of the
     * presence of a particular segment type at this point in parsing the
     * document.
     *
     * @param segmentName type of segment encountered, for example: 837
     * @return true if there was a transition to a new loop, false otherwise
     * @throws com.berryworks.edireader.EDISyntaxException Description of the Exception
     */
    @Override
    public boolean transition(String segmentName) throws EDISyntaxException {
        if (!enabled)
            return false;

//        if (debug)
//            logger.debug("considering segment {} while in loop {} with stack {}",
//                    segmentName, loopDescriptor.getName(), loopStack.toString());

        boolean result = false;

        LoopDescriptor newDescriptor = plugin.query(
                segmentName,
                loopStack.toString(),
                loopDescriptor.getNestingLevel(),
                resultFlags);

//        if (debug)
//            logger.debug("considering segment {} using descriptor {}", segmentName, newDescriptor);

        if (!validateDescriptor(newDescriptor, segmentName, tokenizer))
            return false;

        // Set flags related to this descriptor.
        Set<String> flags = newDescriptor.getResultFlags();
        for (String flagName : flags) {
//            logger.debug("setting flag {}", flagName);
            resultFlags.add(flagName);
        }

        String newLoopName = newDescriptor.getName();
        if (CURRENT.equals(newLoopName) && newDescriptor.getNestingLevel() == loopDescriptor.getNestingLevel()) {
//            logger.debug("resuming current loop without transition");
        } else {
//            logger.debug("transitioning to level {}", newDescriptor.getNestingLevel());
            result = true;

            numberOfLoopsClosed = loopDescriptor.getNestingLevel() - newDescriptor.getNestingLevel();
            boolean resumeLoop = true;
            if (newLoopName.startsWith("/"))
                // Resume the current loop at the target level with
                // closing it.
                currentLoopName = newLoopName;
            else if (newLoopName.startsWith(".")) {
                // Resume the current loop at the target level with
                // closing it.
                currentLoopName = newDescriptor.getNestingLevel() == 0 ? "/" : ".";
            } else {
                // Close the current loop at the target level so that we can initiate a new one.
                numberOfLoopsClosed++;
                currentLoopName = newLoopName;
                resumeLoop = false;
            }

//            logger.debug("closing {} loops", numberOfLoopsClosed);

            if ((numberOfLoopsClosed < 0) || (numberOfLoopsClosed > loopDescriptor.getNestingLevel())) {
                EDISyntaxException se = new EDISyntaxException("Improper sequencing noted with segment " + segmentName, tokenizer);
                logger.warn(se.getMessage());
                throw se;
            } else if (numberOfLoopsClosed > 0) {
                // Pop that many off the tack
                for (int i = 0; i < numberOfLoopsClosed; i++) {
                    LoopContext completedLoop = loopStack.pop();
                    validateCompletedLoop(completedLoop);
//                    logger.debug("popped {} off the stack", completedLoop);
                }
            }
            loopDescriptor = newDescriptor;
            if (resumeLoop) {
//                if (debug)
//                    logger.debug("resuming loop at level {} with name {} ",
//                            loopDescriptor.getNestingLevel(), loopDescriptor.getName());
                if (loopDescriptor.getNestingLevel() == 0
                        && loopDescriptor.getName().length() > 1
                        && loopDescriptor.getName().startsWith("/")) {
//                    logger.debug("special legacy case: {}", loopDescriptor);
                    loopStack.setBottom(new LoopContext(loopDescriptor.getName().substring(1)));
                }
            } else {
                loopStack.push(createLoopContext(loopDescriptor.getName(), plugin, loopStack.toString()));
//                logger.debug("pushed {} onto the stack", loopDescriptor.getName());
            }
        }

        return validateSegment(newDescriptor, loopStack, tokenizer) && result;
    }

    /**
     * Used only within the internal implementation of this class and its subclasses.
     *
     * @param name   - loop name
     * @param plugin - this controller's Plugin
     * @param stack  - LoopStack expressed with toString()
     * @return LoopContext
     */
    protected LoopContext createLoopContext(String name, Plugin plugin, String stack) {
        return new LoopContext(name);
    }

    /**
     * Performs validation logic with respect to a candidate LoopDescriptor.
     * <p>
     * The default implementation provide by this class returns true, indicating that the
     * LoopDescriptor is valid, for any non-null LoopDescriptor.
     * The primary purpose of this method is to provide a "hook" so that a subclass of
     * PluginController can override this method and apply custom logic.
     *
     * @param descriptor  - LoopDescriptor governing the appearance of this segment
     * @param segmentName - name of the segment
     * @param tokenizer   - reference to the Tokenizer to provide context for syntax exceptions
     * @return true if the LoopDescriptor is considered valid.
     * @throws EDISyntaxException - corresponding to a validation fault
     */
    protected boolean validateDescriptor(LoopDescriptor descriptor,
                                         String segmentName,
                                         Tokenizer tokenizer) throws EDISyntaxException {
        return descriptor != null;
    }

    /**
     * Performs validation logic appropriate for the end of a segment loop.
     * <p>
     * The default implementation is empty. A PluginController subclass can override
     * this method in order to apply its own validation policies.
     *
     * @param completedLoop - describes the loop that was just completed
     * @throws EDISyntaxException - corresponding to a validation fault
     */
    protected void validateCompletedLoop(LoopContext completedLoop) throws EDISyntaxException {
    }

    /**
     * Validates the appearance of a segment, given the selected LoopDescriptor and the current LoopStack.
     * <p>
     * The default implementation always returns true.
     * The primary purpose of this method is to provide a hook for subclasses to perform
     * a particular type of validation.
     *
     * @param descriptor - the LoopDescriptor for the loop in which this segment appears
     * @param loopStack  - LoopStack corresponding to the current parsing state
     * @param tokenizer  - reference to the Tokenizer to provide context for syntax exceptions
     * @return true if the segment appearance is valid
     * @throws EDISyntaxException - thrown if an invalid condition is detected
     */
    protected boolean validateSegment(LoopDescriptor descriptor, LoopStack loopStack, Tokenizer tokenizer) throws EDISyntaxException {
        return true;
    }

    /**
     * Returns the LoopStack.
     *
     * @return LoopStack
     */
    protected LoopStack getLoopStack() {
        return loopStack;
    }

    /**
     * Return the name of a loop that was entered as the result of the most
     * recent transition.
     *
     * @return name of the entered loop, or null if no loop was entered
     */
    @Override
    public String getLoopEntered() {
        return currentLoopName;
    }

    /**
     * Get the number of loops that were closed as the result of the most recent
     * state transition. Re-entering the implicit outer loop does not count as a
     * loop closing.
     *
     * @return Description of the Return Value
     */
    @Override
    public int closedCount() {
        return numberOfLoopsClosed;
    }

    /**
     * Get the nesting level of the current loop.
     *
     * @return Description of the Return Value
     */
    @Override
    public int getNestingLevel() {
        return loopDescriptor.getNestingLevel();
    }

    /**
     * Returns true if this controller is currently enabled.
     *
     * @return true if currently enabled
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns the document name associated with the plugin.
     *
     * @return name of the document
     */
    @Override
    public String getDocumentName() {
        return enabled ? plugin.getDocumentName() : null;
    }

    /**
     * Returns the Plugin used by this PluginController.
     *
     * @return Plugin
     */
    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Returns true if the most recent loop transition was to resume an outer loop.
     *
     * @return true if the transition was to an outer loop
     */
    @Override
    public boolean isResumed() {
        String s = getLoopEntered();
        return s.startsWith("/") || ".".equals(s);
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

}
