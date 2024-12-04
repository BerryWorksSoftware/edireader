package com.berryworks.edireader.plugin;

import com.berryworks.edireader.Plugin;

import java.util.HashMap;
import java.util.Map;

public abstract class CompositeAwarePlugin extends Plugin {

    protected final Map<String, String> composites = new HashMap<>();

    public CompositeAwarePlugin(String documentType, String documentName) {
        super(documentType, documentName);
    }

    public boolean isComposite(String segmentName, int position) {
        return composites.get(segmentName + '-' + position) != null;
    }

    public Map<String, String> getComposites() {
        return composites;
    }

    /**
     * Returns the name of an EDI element that provides a common reference value
     * for business purposes, based on the specific transaction type.
     * For example, an 850 Purchase Order would likely use "BEG03", the element
     * containing the purchase order number.
     *
     * @return String identifies the EDI element
     */
    public String getReferenceElement() {
        return null;
    }

}
