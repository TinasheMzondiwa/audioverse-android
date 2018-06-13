package com.tinashe.audioverse.ui.home;

import com.tinashe.audioverse.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

@IntDef({Navigation.LISTS, Navigation.PRESENTATIONS, Navigation.PRESENTERS})
@Retention(RetentionPolicy.RUNTIME)
public @interface Navigation {

    int LISTS = R.id.nav_lists;
    int PRESENTATIONS = R.id.nav_presentations;
    int PRESENTERS = R.id.nav_presenters;
}
