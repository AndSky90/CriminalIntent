package com.example.i550.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;


public class CrimePagerActivity extends AppCompatActivity{

    private static final String EXTRA_CRIME_ID="com.example.i550.criminalintent.crime_id";

    private ViewPager mViewPager;
    private List<Crime> mCrimes;

    public static Intent newIntent(Context packageContext, UUID crimeId){
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        UUID crimeID = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        //return CrimeFragment.newInstance(crimeID);

        mViewPager=findViewById(R.id.crime_view_pager);     //находим ViewPager
        mCrimes=CrimeLab.get(this).getCrimes();             //получаем из БД преступления

        FragmentManager fragmentManager = getSupportFragmentManager();  //получаем фрманагер для активности
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager){   //нзначаем для ViewPager адаптер(безымянный экземпляр)агент
            @Override public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);    //берет нужный крайм, и его ид, возвращает правильный краймФрагмент
                return CrimeFragment.newInstance(crime.getID());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });
        mViewPager.setPageMargin(64);
        for (int i=0; i<mCrimes.size(); i++) {
            if(mCrimes.get(i).getID().equals(crimeID)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
