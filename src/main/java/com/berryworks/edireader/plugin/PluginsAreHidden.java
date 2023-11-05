package com.berryworks.edireader.plugin;

import com.berryworks.edireader.PluginController;
import com.berryworks.edireader.tokenizer.Tokenizer;

/**
 * An implementation of a PluginControllerFactor that ignores the availability of any plugins
 * so that EDI parsing proceeds with no loop detection or other details that are provided by a plugin.
 * This does not remove any plugins from the cache or otherwise interfere with plugin handling.
 * It is intended for use with a specific StandardReader instance (AnsiReader, EdifactReader, etc.)
 * to instruct it to parse EDI without recognizing any segment loops.
 */
public class PluginsAreHidden implements PluginControllerFactoryInterface {
    @Override
    public PluginController create(String standard, String docType, Tokenizer tokenizer) {
        // A default PluginController does not react to any segments and therefore is just what we need.
        return new PluginController();
    }

    @Override
    public PluginController create(String standard, String docType, String docVersion, String docRelease, Tokenizer tokenizer) {
        return create(standard, docType, tokenizer);
    }

    @Override
    public PluginController getLastControllerCreated() {
        return null;
    }
}
