/*
 * Created By JONGHO, LEE, Researcher of Institute of Biomedical Engineering, TechHeim Co.,Ltd.
 * on  2021.
 * This software is not stable health-care system and not certificated any health-care authority(like CE, FDA)
 * Prohibit redistribution, modification, commercial use.
 * Copyright 테크하임(주). All rights reserved.
 */

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
