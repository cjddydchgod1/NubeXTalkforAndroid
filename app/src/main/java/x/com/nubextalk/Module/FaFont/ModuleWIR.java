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
