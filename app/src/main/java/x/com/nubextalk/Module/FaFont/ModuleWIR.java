/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.FaFont;

import com.joanzapata.iconify.Icon;

public class ModuleWIR implements com.joanzapata.iconify.IconFontDescriptor{
    @Override
    public String ttfFileName() {
        return "font/wi-regular.ttf";
    }

    @Override
    public Icon[] characters() {
        return IconsWIR.values();
    }
}
