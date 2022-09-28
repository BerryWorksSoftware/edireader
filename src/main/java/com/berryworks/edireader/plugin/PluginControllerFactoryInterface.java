package com.berryworks.edireader.plugin;

import com.berryworks.edireader.PluginController;
import com.berryworks.edireader.tokenizer.Tokenizer;

public interface PluginControllerFactoryInterface {
    PluginController create(String standard, String docType, Tokenizer tokenizer);

    PluginController create(String standard, String docType, String docVersion, String docRelease, Tokenizer tokenizer);

    PluginController getLastControllerCreated();
}
