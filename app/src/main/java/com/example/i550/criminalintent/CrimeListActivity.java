package com.example.i550.criminalintent;

import android.support.v4.app.Fragment;

//хост для CrimeListFragment

public class CrimeListActivity extends SingleFragmentActiviy {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

}