package x.com.nubextalk.Module.FaFont;

import com.joanzapata.iconify.Icon;

public class ModuleFAB implements com.joanzapata.iconify.IconFontDescriptor{
    @Override
    public String ttfFileName() {
        return "font/fa-brands-400.ttf";
    }

    @Override
    public Icon[] characters() {
        return IconsFAB.values();
    }
}
