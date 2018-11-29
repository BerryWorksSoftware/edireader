package com.berryworks.edireader.filter;

import com.berryworks.edireader.EDIReader;

public interface EdiReaderFilter {
    EDIReader filter(EDIReader ediReader);
}
