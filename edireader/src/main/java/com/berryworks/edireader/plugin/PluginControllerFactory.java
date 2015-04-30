/*
 * Copyright 2005-2011 by BerryWorks Software, LLC. All rights reserved.
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
import com.berryworks.edireader.Plugin;
import com.berryworks.edireader.PluginController;
import com.berryworks.edireader.tokenizer.Tokenizer;

import java.util.HashMap;
import java.util.Map;

import static com.berryworks.edireader.util.FixedLength.isPresent;

public class PluginControllerFactory {

    protected static boolean debug;
    public static final String DEFAULT_EDIREADER_PLUGIN_PACKAGE = "com.berryworks.edireader.plugin";

    protected static final Map<String, Plugin> pluginCache = new HashMap<>();
    protected static String lastPluginLoaded = null;

    protected Plugin plugin;

    /**
     * Creates a new instance of a PluginController, selecting a plugin based on the standard and type of document.
     *
     * @param standard  - name of EDI standard (for example: "EDIFACT" or "ANSI")
     * @param docType   - type of document (for example: "837" or "INVOIC")
     * @param tokenizer - reference to the Tokenizer to provide context for syntax exceptions
     * @return instance of a PluginController
     */
    public PluginController create(String standard, String docType, Tokenizer tokenizer) {
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
     * @param standard   - name of EDI standard (for example: "EDIFACT" or "ANSI")
     * @param docType    - type of document (for example: "837" or "INVOIC")
     * @param docVersion - a particular version of the standard (for example: "X" in ANSI or "04A" in EDIFACT)
     * @param docRelease - a particular release of the standard (for example: "D" in EDIFACT or "004010" in ANSI)
     * @param tokenizer  - reference to the Tokenizer to provide context for syntax exceptions
     * @return instance of a PluginController
     */
    public PluginController create(String standard, String docType, String docVersion, String docRelease, Tokenizer tokenizer) {
        PluginControllerImpl result;
        Plugin plugin = PluginControllerFactory.loadPlugin(standard, docType, docVersion, docRelease);

        if (plugin == null) {
            result = new PluginControllerImpl(standard, tokenizer);
            result.setEnabled(false);
        } else {
            plugin.init();
            result = plugin.createController(standard, tokenizer);
            result.setEnabled(true);
        }

        result.setDocumentType(docType);
        result.setPlugin(plugin);
        return result;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Find a plugin for a given standard, document type, version, and release.
     * If no matching plugin is found, return null.
     * Plugins are cached so that once a plugin is loaded it can be quickly found again without using the
     * class loader.
     *
     * @param standard   - name of EDI standard (for example: "EDIFACT" or "ANSI")
     * @param docType    - type of document (for example: "837" or "INVOIC")
     * @param docVersion - a particular version of the standard (for example: "X" in ANSI or "04A" in EDIFACT)
     * @param docRelease - a particular release of the standard (for example: "D" in EDIFACT or "004010" in ANSI)
     * @return Plugin that was found, or null if no suitable plugin was found
     */
    protected static Plugin loadPlugin(String standard, String docType, String docVersion, String docRelease) {
        Plugin result = null;
        String key = standard + "_" + docType + "_" + docVersion + "_" + docRelease;
        if (pluginCache.containsKey(key)) {
            if (debug)
                trace("plugin for " + key + " found in cache");
            lastPluginLoaded = key;
            result = pluginCache.get(key);

        } else {
            String suffix = System.getProperty("EDIREADER_PLUGIN_SUFFIX");
            if (docVersion != null && docVersion.length() > 0 && docRelease != null && docRelease.length() > 0) {
                if (isPresent(suffix))
                    result = lookForSpecificPlugin(standard, docVersion + "_" + docRelease + "." + standard + "_" + docType + "_" + suffix);

                if (result == null)
                    result = lookForSpecificPlugin(standard, docVersion + "_" + docRelease + "." + standard + "_" + docType);

                if (isPresent(suffix))
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

    /**
     * Used only within the internal implementation of this class and its subclasses.
     *
     * @param standard - name of standard
     * @param docType  - type of document
     * @return Plugin if search was satisfied, or null if not
     */
    protected static Plugin lookForSpecificPlugin(String standard, String docType) {
        Plugin pluginFound = null;
        try {
            pluginFound = getInstance(standard, docType);
            if (debug) {
                pluginFound.debug(true);
                trace("plugin found for document type " + docType + ": "
                        + pluginFound.getDocumentName());
            }
        } catch (ClassNotFoundException e) {
            if (debug)
                trace("plugin for " + docType + " not available");
        } catch (InstantiationException e) {
            if (debug)
                trace("plugin for " + docType + " could not be instantiated");
        } catch (IllegalAccessException e) {
            if (debug)
                trace("plugin for " + docType + " caused IllegalAccessException" + e);
        }

        return pluginFound;
    }

    /**
     * Used only within the internal implementation of this class and its subclasses.
     *
     * @param standard - name of EDI standard (for example: "EDIFACT" or "ANSI")
     * @param docType  - type of document, possible enhanced with version and release (for example: "203" or "203_X_004010")
     * @return Plugin that was loaded
     * @throws ClassNotFoundException if the class is not present in the classpath
     * @throws InstantiationException for problems when attempting to load the class
     * @throws IllegalAccessException for problems when attempting to load the class
     */
    protected static Plugin getInstance(String standard, String docType) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException {
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
     * @return package name
     */
    protected static String pluginPackage() {
        String packageName = System.getProperty("EDIREADER_PLUGIN_PACKAGE");
        if (packageName == null) {
            packageName = DEFAULT_EDIREADER_PLUGIN_PACKAGE;
            if (debug)
                trace("Plugin package defaults to " + packageName);
        } else {
            if (debug)
                trace("Plugin package set by property to " + packageName);
        }
        return packageName;
    }

    public static String getLastPluginLoaded() {
        return lastPluginLoaded;
    }

    /**
     * Shorthand for EDIReader.trace(String)
     *
     * @param text message to appear in trace
     */
    protected static void trace(String text) {
        EDIAbstractReader.trace(text);
    }


    public void clearCache() {
        pluginCache.clear();
    }
}
