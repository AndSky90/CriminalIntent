package com.example.i550.criminalintent;

//import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.format.DateFormat;
import java.util.List;


public class CrimeListFragment extends Fragment {

    private RecyclerView mCrimeRecycleView;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;
    private static final String SAVED_SUB_VIS = "subtitle";
    int adPos;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) { //вся эта хуйня для
        super.onCreate(savedInstanceState); //того чтобы фрагментМанагер знал
        setHasOptionsMenu(true);        //что этот фрагмент может принимать коллбэки меню
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecycleView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (savedInstanceState != null)
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUB_VIS);
        updateUI();
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUB_VIS, mSubtitleVisible);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {//переопределяем меню фрагмента(+инфлатор)
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);      //ресурс меню вливаем в Меню
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if(mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_st);} else {
            subtitleItem.setTitle(R.string.show_st);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {       //реагирует на выбор пункта меню
        switch (item.getItemId()){  //получаем ИД итема
            case R.id.new_crime:
                Crime crime=new Crime();    //создаем новый крайм, добавляем его в бд
                CrimeLab.get(getActivity()).addCrime(crime);    //чтобы можно было обращаться к нестатик функции класса(там она создает экземпляр)
                Intent intent = CrimePagerActivity.newIntent(getActivity(),crime.getID());  //интентом вызываем краймпейджер
                startActivity(intent); //(меняем уже созданный крайм -это другой кусок кода работает)
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible=!mSubtitleVisible;
                getActivity().invalidateOptionsMenu();//перерендерить меню
                updateSubtitle();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void updateSubtitle(){
    CrimeLab crimeLab = CrimeLab.get(getActivity());
    int crimeCount = crimeLab.getCrimes().size();
    String subtitle = getResources().getQuantityString(R.plurals.subs_plural,crimeCount,crimeCount);//getString(R.string.st_format, crimeCount);
    if(!mSubtitleVisible) subtitle=null;
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI(){
    CrimeLab crimeLab = CrimeLab.get(getActivity());
    List<Crime> crimes = crimeLab.getCrimes();
        if(mAdapter==null){
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecycleView.setAdapter(mAdapter);}
            else{
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }
//-------------------------------------------------------------------------------------------------
    private class CrimeHolder extends RecyclerView.ViewHolder //тут связывание списка с даными, вьюхолдер - механизм заполнения пиздатый
            implements View.OnClickListener{ //имплемент для того чтобы обр нажатия
        private TextView mTitleTextView, mDateTextView;
        private ImageView mSolvedImageView;
        private Crime mCrime;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_crime,parent,false));
            itemView.setOnClickListener(this);  //-для представления строки назначается онклик-листенер краймхолдера
        mTitleTextView=itemView.findViewById(R.id.crime_title);     //извлекаем в конструкторе элты списка
        mDateTextView=itemView.findViewById(R.id.crime_date);
        mSolvedImageView=itemView.findViewById(R.id.image_crime_solved);
        }

        public void bind(Crime crime){
            mCrime=crime;
            mTitleTextView.setText(mCrime.getTitle());
            DateFormat dateFormat = new DateFormat();
            mDateTextView.setText(dateFormat.format("EE, dd MMMM yyyy, HH:mm:ss",mCrime.getDate()));
            mSolvedImageView.setVisibility(crime.isSolved()?View.VISIBLE:View.GONE);
        }

        @Override public void onClick(View v) {
            adPos = getAdapterPosition();
            Intent intent = CrimePagerActivity.newIntent(getActivity(),mCrime.getID()); //гетактивити берет хост этого фрагмента( как нужный контекст)
            startActivity(intent);
/*Toast.makeText(getActivity(),mCrime.getTitle()+ " clicked!", Toast.LENGTH_SHORT).show();*/
        }
}

    //-------------------------------------------------------------------------------------------------
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{
        private List<Crime> mCrimes;
        public CrimeAdapter(List<Crime> crimes){mCrimes=crimes;}

        @Override public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater=LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater,parent);
        }

        @Override public void onBindViewHolder(CrimeHolder holder, int position) {
        Crime crime = mCrimes.get(position);
        holder.bind(crime);
        }

        @Override public int getItemCount() {
            return mCrimes.size();
        }
        public void setCrimes(List<Crime> crimes){mCrimes=crimes;}

    }
}
