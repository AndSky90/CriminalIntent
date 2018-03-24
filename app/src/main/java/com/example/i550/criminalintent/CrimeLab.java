package com.example.i550.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import database.CrimeBaseHelper;
import database.CrimeCursorWrapper;

import static database.CrimeDBSchema.CrimeTable;

//БД С ПРЕСТУПЛЕНИЯМИ

public class CrimeLab {
    private static CrimeLab sCrimeLab;      //экземпляр бд
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context) {       //синглтоним
        if (sCrimeLab==null){sCrimeLab=new CrimeLab(context);}
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase=new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    public List<Crime> getCrimes(){
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursorWrapper = queryCrimes(null,null);
        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                crimes.add(cursorWrapper.getCrime());
                cursorWrapper.moveToNext();
            }
        }finally {
            cursorWrapper.close();  //всегда сука закрывай курсор
        }
        return crimes;      //переборка курсора и заполнение списка краймов
    }

    public Crime getCrime(UUID id){
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[] {id.toString()}
        );
        try {
            if (cursor.getCount()==0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    public void addCrime(Crime c){
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeTable.NAME, null, values);        //1-имя таблицы, 2-хак 3-данные
    }

    public void delCrime(Crime c) {
        String uuidString = c.getID().toString();
        mDatabase.delete(CrimeTable.NAME, CrimeTable.Cols.UUID + "= ?", new String[] {uuidString});        //1-имя таблицы, 2-хак 3-данные
    }

private static ContentValues getContentValues(Crime crime){ //хранитель пар
        ContentValues values = new ContentValues();         //достает ключи из крайма в КВ
        values.put(CrimeTable.Cols.UUID, crime.getID().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved()? 1:0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());
        return values;
    }
    public void updateCrime(Crime crime){
    String uuidString = crime.getID().toString();
    ContentValues values = getContentValues(crime);
    mDatabase.update(
            CrimeTable.NAME,        //имя БД
            values,                 //КВ которые присвоятся всем обновляемым записям
            CrimeTable.Cols.UUID + " = ?",  //условие
            new String [] {uuidString});                //чё искать в условии
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(        //запрос
                CrimeTable.NAME,    //имя таблицы
                null,       //выбор столбцов
                whereClause,        //где искать /условие
                whereArgs,          //че искать в нем
                null,
                null,
                null
        );
        return new CrimeCursorWrapper(cursor);  //заем враппер чтоб заебись с БД преобразовывало
    }
    public File getPhotoFile(Crime crime){
        File filesDir = mContext.getFilesDir();
        return new File(filesDir,crime.getPhotoFilename());
     //   if(externalFilesDir==null){}
    }
}
