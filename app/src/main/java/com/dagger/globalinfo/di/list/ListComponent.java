package com.dagger.globalinfo.di.list;

import com.dagger.globalinfo.di.application.ApplicationComponent;
import com.dagger.globalinfo.fragment.MainActivityFragment;

import dagger.Component;

/**
 * Created by saurabh on 16/1/17.
 */
@PerList
@Component(modules = ListModule.class, dependencies = ApplicationComponent.class)
public interface ListComponent {

    void inject(MainActivityFragment mainActivityFragment);

}
