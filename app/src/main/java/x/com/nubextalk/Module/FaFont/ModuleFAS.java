package x.com.nubextalk.Module.FaFont;

import com.joanzapata.iconify.Icon;

public class ModuleFAS implements com.joanzapata.iconify.IconFontDescriptor{
    @Override
    public String ttfFileName() {
        return "font/fa-solid-900.ttf";
    }

    @Override
    public Icon[] characters() {
        return IconsFAS.values();
    }
}
