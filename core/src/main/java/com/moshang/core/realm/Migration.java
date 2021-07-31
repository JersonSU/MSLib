package com.moshang.core.realm;

import androidx.annotation.Nullable;
import io.realm.DynamicRealm;
import io.realm.RealmMigration;

/**
 * Created by:Jerson, on 2021/7/5.
 * Describe:
 **/
public class Migration implements RealmMigration {

    private int vcode=0;

    public Migration() {
    }

    public Migration(int vcode) {
        this.vcode = vcode;
    }

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        //TODO::
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Migration;
    }

    @Override
    public int hashCode() {
        return vcode;
    }
}
