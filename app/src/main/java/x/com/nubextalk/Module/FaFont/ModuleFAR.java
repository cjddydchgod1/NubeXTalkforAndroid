package x.com.nubextalk.Module.FaFont;

import com.joanzapata.iconify.Icon;

public class ModuleFAR implements com.joanzapata.iconify.IconFontDescriptor{
    @Override
    public String ttfFileName() {
        return "font/fa-regular-400.ttf";
    }

    @Override
    public Icon[] characters() {
        return IconsFAR.values();
    }
}
