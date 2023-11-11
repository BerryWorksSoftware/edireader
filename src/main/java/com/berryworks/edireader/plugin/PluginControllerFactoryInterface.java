package com.berryworks.edireader.plugin;

import com.berryworks.edireader.PluginController;
import com.berryworks.edireader.tokenizer.Tokenizer;

public interface PluginControllerFactoryInterface {
    /**
     * Access the PluginControllerFactory singleton.
     *
     * @return most recently constructed PluginControllerFactory, or null
     */
    static PluginControllerFactoryInterface getInstance() {
        return AbstractPluginControllerFactory.instance;
    }

    PluginController create(String standard, String docType, Tokenizer tokenizer);

    PluginController create(String standard, String docType, String docVersion, String docRelease, Tokenizer tokenizer);

    PluginController getLastControllerCreated();
}
