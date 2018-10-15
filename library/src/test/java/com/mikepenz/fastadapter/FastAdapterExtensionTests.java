package com.mikepenz.fastadapter;

import com.mikepenz.fastadapter.extensions.ExtensionsFactories;
import com.mikepenz.fastadapter.select.SelectExtension;
import com.mikepenz.fastadapter.select.SelectExtensionFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLog;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class FastAdapterExtensionTests {
    private FastAdapter<TestItem> adapter;

    @Before
    public void setUp() throws Exception {
        adapter = new FastAdapter<>();
        ShadowLog.stream = System.out;
    }

    @Test
    public void createSelectExtension() {
        //TODO: fixme
        ExtensionsFactories.INSTANCE.register(new SelectExtensionFactory());
        SelectExtension<TestItem> selectExtension = adapter.getOrCreateExtension(SelectExtension.class);
        assertThat(selectExtension).isNotNull();
        selectExtension = adapter.getExtension(SelectExtension.class);
        assertThat(selectExtension).isNotNull();
    }
}
