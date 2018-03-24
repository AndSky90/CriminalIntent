package com.example.i550.criminalintent;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {    //как и все фрагменты, эти под управлением экземпляра ФрМана хоста
    private static final String ARG_DATE = "date";
    public static final String EXTRA_DATE = "com.example.i550.criminalintent.date";
    private DatePicker mDatePicker;

    public static DatePickerFragment newInstance(Date date){            //создание аргментов фрагмента(типа конструктора)
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);       //в бандл помещаем дату
        DatePickerFragment fragment = new DatePickerFragment(); //этому фрагменту присваиваем этот аргумент
        fragment.setArguments(args);
        return fragment;
    }
    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = (Date)getArguments().getSerializable(ARG_DATE); //вытаскиваем дату
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);     //устанавливаем в экземпляре дату
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);  //вытаскиваем конкретно

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date,null);
        //вью v заполняем лэйаутом-DatePicker-виджетом

        mDatePicker = v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year,month,day,null);

        //при выводе на экран
        return new AlertDialog.Builder(getActivity())       // создается алертДиалог(контекст)
                .setView(v)         //устанавливается вью v с календарем
                .setTitle(R.string.date_picker_title)       //с таким заголовком и такой кнопкой
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = mDatePicker.getYear();   //строим дату для БД и отправляем
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();
                        Date date = new GregorianCalendar(year,month,day).getTime();
                        sendResult(Activity.RESULT_OK, date);
                    }
                })
                .create();
    }
    private void sendResult(int resultCode, Date date){
        if(getTargetFragment()==null){return;}
        Intent intent = new Intent();   //делаем интент с датой, вызываем onActivityResult
        intent.putExtra(EXTRA_DATE, date);      //(код с которым вызывался, результат-код, интент)
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
