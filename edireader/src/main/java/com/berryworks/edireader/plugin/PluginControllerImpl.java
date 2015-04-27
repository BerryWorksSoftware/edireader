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

import com.berryworks.edireader.EDIAbstractReader;
import com.berryworks.edireader.EDISyntaxException;
import com.berryworks.edireader.Plugin;
import com.berryworks.edireader.PluginController;
import com.berryworks.edireader.tokenizer.Tokenizer;

import java.util.HashMap;
import java.util.Map;

/**
 * Determines and maintains state transitions for the segment looping structure
 * within a particular EDI document.
 * <p/>
 * An EDI parser delegates the job of detecting segment loop boundaries
 * to a PluginController. This allows the EDI parsers for ANSI and EDIFACT
 * to be fully consistent with their use of plugins and focus on the specifics of
 * the particular EDI standard.
 * <p/>
 * This PluginControllerImpl provides the normal
 * segment loop support based on LoopDescriptors in Plugins.
 * It is possible, however, to extend this behavior by subclassing
 * PluginControllerImpl and Plugin. A ValidatingPlugin is one example,
 * which provides for certain EDI validation rules, beyond those applied by
 * normal EDIReader parsing, to be applied while a document is being parsed.
 * Another example is a FilteringPlugin, which allows a plugin to provide
 * custom logic to filter out certain LoopDescriptors based on run-time decisions.
 *
 * @see com.berryworks.edireader.Plugin
 * @see com.berryworks.edireader.plugin.LoopDescriptor
 * @see com.berryworks.edireader.validator.FilteringPluginController
 * @see com.berryworks.edireader.validator.ValidatingPluginController
 */
public class PluginControllerImpl extends PluginController
{

  public static final String DEFAULT_EDIREADER_PLUGIN_PACKAGE = "com.berryworks.edireader.plugin";

  protected static final Map<String, Plugin> pluginCache = new HashMap<String, Plugin>();
  protected static String lastPluginLoaded = null;

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
  protected String currentLoopName = "/";

  /**
   * Descriptor that caused us to enter the loop we are now in.
   */
  protected LoopDescriptor loopDescriptor = new LoopDescriptor(
    currentLoopName, "", 0, "/");

  /**
   * Number of loops that were closed as the result of the most recent
   * transition. A transition that re-enters the implicit outer loop does not
   * consider the outer loop in this count.
   */
  protected int numberOfLoopsClosed;

  /**
   * This constructor is not for general use because create methods are provided as factory methods to instantiate a new instance of
   * a PluginController.
   *
   * @param standard
   * @param tokenizer
   */
  public PluginControllerImpl(String standard, Tokenizer tokenizer)
  {
    this.standard = standard;
    this.tokenizer = tokenizer;
  }

  /**
   * Creates a new instance of a PluginController, selecting a plugin based on the standard and type of document.
   *
   * @param standard
   * @param docType
   * @param tokenizer
   * @return instance
   */
  public static PluginControllerImpl create(String standard, String docType, Tokenizer tokenizer)
  {
    return create(standard, docType, null, null, tokenizer);
  }

  /**
   * Creates a new instance of a PluginController, selecting a plugin based on the standard, the type of document,
   * and the version and release characteristics.
   * <p/>
   * This factory method supports version-specific plugins for a given type of document. If first tries to load
   * a plugin specific to a particular release and version using the naming convention for plugin class names.
   * If no version-specific plugin is available, it uses the other factory method
   * to create a plugin based simply on the standard and type.
   *
   * @param standard
   * @param docType
   * @param docVersion
   * @param docRelease
   * @param tokenizer
   * @return instance
   */
  public static PluginControllerImpl create(String standard, String docType, String docVersion, String docRelease, Tokenizer tokenizer)
  {
    PluginControllerImpl result;
    Plugin plugin = loadPlugin(standard, docType, docVersion, docRelease);

    if (plugin == null)
    {
      result = new PluginControllerImpl(standard, tokenizer);
      result.enabled = false;
    }
    else
    {
      plugin.init();
      result = plugin.createController(standard, tokenizer);
      result.enabled = true;
    }

    result.documentType = docType;
    result.loopStack = new LoopStack();
    result.plugin = plugin;
    return result;
  }

  /**
   * Compute a state transition that may have occurred as the result of the
   * presence of a particular segment type at this point in parsing the
   * document.
   *
   * @param segmentName type of segment encountered, for example: 837
   * @return true if there was a transition to a new loop, false otherwise
   * @throws com.berryworks.edireader.EDISyntaxException
   *          Description of the Exception
   */
  @Override
  public boolean transition(String segmentName) throws EDISyntaxException
  {
    if (!enabled)
      return false;

    if (debug)
      trace("considering segment " + segmentName + " while in loop "
        + loopDescriptor.getName() + " with stack "
        + loopStack.toString());

    boolean result = false;

    LoopDescriptor newDescriptor = plugin.query(
      segmentName,
      loopStack.toString(),
      loopDescriptor.getNestingLevel());

    if (debug)
      trace("considering segment " + segmentName + " using descriptor " + newDescriptor);

    if (!validateDescriptor(newDescriptor, segmentName, tokenizer))
      return false;

    String newLoopName = newDescriptor.getName();
    if (Plugin.CURRENT.equals(newLoopName) &&
      newDescriptor.getNestingLevel() == loopDescriptor.getNestingLevel())
    {
      if (debug) trace("resuming current loop without transition");
    }
    else
    {
      if (debug) trace("transitioning to level " + newDescriptor.getNestingLevel());
      result = true;

      numberOfLoopsClosed = loopDescriptor.getNestingLevel() - newDescriptor.getNestingLevel();
      boolean resumeLoop = true;
      if (newLoopName.startsWith("/"))
        // Resume the current loop at the target level with
        // closing it.
        currentLoopName = newLoopName;
      else if (newLoopName.startsWith("."))
      {
        // Resume the current loop at the target level with
        // closing it.
        currentLoopName = newDescriptor.getNestingLevel() == 0 ? "/" : ".";
      }
      else
      {
        // Close the current loop at the target level so that we
        // can initiate a new one.
        numberOfLoopsClosed++;
        currentLoopName = newLoopName;
        resumeLoop = false;
      }

      if (debug) trace("closing " + numberOfLoopsClosed + " loops");

      if ((numberOfLoopsClosed < 0)
        || (numberOfLoopsClosed > loopDescriptor.getNestingLevel()))
        throw new EDISyntaxException(
          "Improper sequencing noted with segment " + segmentName, tokenizer);
      else if (numberOfLoopsClosed > 0)
      {
        // Pop that many off the tack
        for (int i = 0; i < numberOfLoopsClosed; i++)
        {
          LoopContext completedLoop = loopStack.pop();
          validateCompletedLoop(completedLoop);
          if (debug) trace("popped " + completedLoop + " off the stack");
        }
      }
      loopDescriptor = newDescriptor;
      if (resumeLoop)
      {
        if (debug)
          trace("resuming loop at level "
            + loopDescriptor.getNestingLevel()
            + " with name " + loopDescriptor.getName());
        if (loopDescriptor.getNestingLevel() == 0
          && loopDescriptor.getName().length() > 1
          && loopDescriptor.getName().startsWith("/"))
        {
          if (debug) trace("special legacy case: " + loopDescriptor);
          loopStack.setBottom(new LoopContext(loopDescriptor.getName().substring(1)));
        }
      }
      else
      {
        loopStack.push(createLoopContext(loopDescriptor.getName(), plugin, loopStack.toString()));
        if (debug) trace("pushed " + loopDescriptor.getName() + " onto the stack");
      }
    }

    return validateSegment(newDescriptor, loopStack, tokenizer) && result;
  }

  /**
   * Used only within the internal implementation of this class and its subclasses.
   *
   * @param name
   * @param plugin
   * @param s
   * @return
   */
  protected LoopContext createLoopContext(String name, Plugin plugin, String s)
  {
    return new LoopContext(name);
  }

  /**
   * Performs validation logic with respect to a candidate LoopDescriptor.
   * <p/>
   * The default implementation provide by this class returns true, indicating that the
   * LoopDescriptor is valid, for any non-null LoopDescriptor.
   * The primary purpose of this method is to provide a "hook" so that a subclass of
   * PluginController can override this method and apply custom logic.
   *
   * @param descriptor
   * @param segmentName
   * @param tokenizer
   * @return true if the LoopDescriptor is considered valid.
   * @throws EDISyntaxException
   */
  protected boolean validateDescriptor(LoopDescriptor descriptor,
                                       String segmentName,
                                       Tokenizer tokenizer) throws EDISyntaxException
  {
    return descriptor != null;
  }

  /**
   * Performs validation logic appropriate for the end of a segment loop.
   * <p/>
   * The default implementation is empty. A PluginController subclass can override
   * this method in order to apply its own validation policies.
   *
   * @param completedLoop
   * @throws EDISyntaxException
   */
  protected void validateCompletedLoop(LoopContext completedLoop) throws EDISyntaxException
  {
  }

  /**
   * Validates the appearance of a segment, given the selected LoopDescriptor and the current LoopStack.
   * <p/>
   * The default implementation always returns true.
   * The primary purpose of this method is to provide a "hook" for subclasses to perform
   * a particular type of validation.
   *
   * @param descriptor
   * @param loopStack
   * @param tokenizer
   * @return
   * @throws EDISyntaxException
   */
  protected boolean validateSegment(LoopDescriptor descriptor, LoopStack loopStack, Tokenizer tokenizer) throws EDISyntaxException
  {
    return true;
  }

  /**
   * Returns the LoopStack.
   *
   * @return
   */
  protected LoopStack getLoopStack()
  {
    return loopStack;
  }

  /**
   * Return the name of a loop that was entered as the result of the most
   * recent transition.
   *
   * @return name of the entered loop, or null if no loop was entered
   */
  @Override
  public String getLoopEntered()
  {
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
  public int closedCount()
  {
    return numberOfLoopsClosed;
  }

  /**
   * Get the nesting level of the current loop.
   *
   * @return Description of the Return Value
   */
  @Override
  public int getNestingLevel()
  {
    return loopDescriptor.getNestingLevel();
  }

  /**
   * Returns true if this controller is currently enabled.
   *
   * @return
   */
  @Override
  public boolean isEnabled()
  {
    return enabled;
  }

  /**
   * Returns the document name associated with the plugin.
   *
   * @return
   */
  @Override
  public String getDocumentName()
  {
    return enabled ? plugin.getDocumentName() : null;
  }

  /**
   * Returns the Plugin used by this PluginController.
   *
   * @return Plugin
   */
  @Override
  public Plugin getPlugin()
  {
    return plugin;
  }

  /**
   * Shorthand for EDIReader.trace(String)
   *
   * @param text message to appear in trace
   */
  protected static void trace(String text)
  {
    EDIAbstractReader.trace(text);
  }

  /**
   * Returns true if the most recent loop transition was to resume an outer loop.
   *
   * @return
   */
  @Override
  public boolean isResumed()
  {
    String s = getLoopEntered();
    return s.startsWith("/") || ".".equals(s);
  }

  /**
   * Used within the implementation of PluginController to find a plugin for a given standard, document type,
   * version, and release.
   * If no matching plugin is found, it returns null.
   * Plugins are cached so that once a plugin is loaded it can be quickly found again without using the
   * class loader.
   *
   * @param standard
   * @param docType
   * @param docVersion
   * @param docRelease
   * @return
   */
  protected static Plugin loadPlugin(String standard, String docType, String docVersion, String docRelease)
  {
    Plugin result = null;
    String key = standard + "_" + docType + "_" + docVersion + "_" + docRelease;
    if (pluginCache.containsKey(key))
    {
      if (debug)
        trace("plugin for " + key + " found in cache");
      lastPluginLoaded = key;
      result = pluginCache.get(key);

    }
    else
    {
      String suffix = System.getProperty("EDIREADER_PLUGIN_SUFFIX");
      if (docVersion != null && docVersion.length() > 0 && docRelease != null && docRelease.length() > 0)
      {
        if (suffix != null && suffix.length() > 0)
          result = lookForSpecificPlugin(standard, docVersion + "_" + docRelease + "." + standard + "_" + docType + "_" + suffix);

        if (result == null)
          result = lookForSpecificPlugin(standard, docVersion + "_" + docRelease + "." + standard + "_" + docType);

        if (suffix != null && suffix.length() > 0)
          result = lookForSpecificPlugin(standard, docType + "_" + docVersion + "_" + docRelease + "_" + suffix);

        if (result == null)
          result = lookForSpecificPlugin(standard, docType + "_" + docVersion + "_" + docRelease);
      }
      if (result == null && suffix != null && suffix.length() > 0)
        result = lookForSpecificPlugin(standard, docType + "_" + suffix);

      if (result == null)
        result = lookForSpecificPlugin(standard, docType);
      pluginCache.put(key, result);
    }

    return result;
  }

  @Override
  public String lastPluginLoaded()
  {
    return lastPluginLoaded;
  }

  /**
   * Used only within the internal implementation of this class and its subclasses.
   *
   * @param standard
   * @param docType
   * @return
   */
  protected static Plugin lookForSpecificPlugin(String standard, String docType)
  {
    Plugin pluginFound = null;
    try
    {
      pluginFound = getInstance(standard, docType);
      if (debug)
      {
        pluginFound.debug(true);
        trace("plugin found for document type " + docType + ": "
          + pluginFound.getDocumentName());
      }
    } catch (ClassNotFoundException e)
    {
      if (debug)
        trace("plugin for " + docType + " not available");
    } catch (InstantiationException e)
    {
      if (debug)
        trace("plugin for " + docType + " could not be instantiated");
    } catch (IllegalAccessException e)
    {
      if (debug)
        trace("plugin for " + docType + " caused IllegalAccessException" + e);
    }

    return pluginFound;
  }

  /**
   * Used only within the internal implementation of this class and its subclasses.
   *
   * @param standard
   * @param docType
   * @return
   * @throws ClassNotFoundException
   * @throws InstantiationException
   * @throws IllegalAccessException
   */
  protected static Plugin getInstance(String standard, String docType) throws ClassNotFoundException,
    InstantiationException, IllegalAccessException
  {
    Plugin instance;
    String pluginName = pluginPackage() + "." + standard + "_" + docType;
    if (debug)
      trace("attempting to load a plugin named " + pluginName);
    Class pluginClass = Class.forName(pluginName);
    if (debug)
      trace("plugin loaded");
    instance = (Plugin) pluginClass.newInstance();
    instance.prepare();
    lastPluginLoaded = pluginName;
    return instance;
  }

  /**
   * Returns the name of the package in which plugins are expected to appear.
   * <p/>
   * The default value is "com.berryworks.edireader.plugin" but this can be changed by setting
   * the system property EDIREADER_PLUGIN_PACKAGE. In this way, a user can develop their own plugins and using
   * a package naming scheme of their choice.
   *
   * @return
   */
  protected static String pluginPackage()
  {
    String packageName = System.getProperty("EDIREADER_PLUGIN_PACKAGE");
    if (packageName == null)
    {
      packageName = DEFAULT_EDIREADER_PLUGIN_PACKAGE;
      if (debug)
        trace("Plugin package defaults to " + packageName);
    }
    else
    {
      if (debug)
        trace("Plugin package set by property to " + packageName);
    }
    return packageName;
  }
}
