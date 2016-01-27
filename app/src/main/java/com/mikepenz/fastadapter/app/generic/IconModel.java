package com.mikepenz.fastadapter.app.generic;

import com.mikepenz.iconics.typeface.IIcon;

/**
 * Created by mikepenz on 18.01.16.
 */
public class IconModel {
    public IconModel(IIcon icon) {
        this.icon = icon;
    }

    public IconModel(IIcon icon, boolean normal) {
        this.icon = icon;
        this.normal = normal;
    }

    public IIcon icon;

    public boolean normal = true;
}
