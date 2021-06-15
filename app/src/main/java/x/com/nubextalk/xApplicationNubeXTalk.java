/*
 * Created By JONGHO, LEE, Researcher of Institute of Biomedical Engineering, TechHeim Co.,Ltd.
 * on  2021.
 * This software is not stable health-care system and not certificated any health-care authority(like CE, FDA)
 * Prohibit redistribution, modification, commercial use.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.joanzapata.iconify.Iconify;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.regex.Pattern;

import io.realm.Realm;
import x.com.nubextalk.Module.FaFont.ModuleFAB;
import x.com.nubextalk.Module.FaFont.ModuleFAR;
import x.com.nubextalk.Module.FaFont.ModuleFAS;
import x.com.nubextalk.Module.FaFont.ModuleWIR;

public class xApplicationNubeXTalk extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Iconify.with(new ModuleFAB())
                .with(new ModuleFAR())
                .with(new ModuleFAS())
                .with(new ModuleWIR());

        Realm.init(this);

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).withDeleteIfMigrationNeeded(true).build())
                        .build());

        RealmInspectorModulesProvider.builder(this)
                .withMetaTables()
                .withDescendingOrder()
                .withLimit(1000)
                .databaseNamePattern(Pattern.compile(".+\\.realm"))
                .build();
    }
}
