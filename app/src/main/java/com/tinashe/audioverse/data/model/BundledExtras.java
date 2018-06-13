package com.tinashe.audioverse.data.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.StringDef;

@StringDef({BundledExtras.NAVIGATION, BundledExtras.PRESENTER})
@Retention(RetentionPolicy.RUNTIME)
public @interface BundledExtras {

    String NAVIGATION = "NAVIGATION";

    String PRESENTER = "PRESENTER";
}
