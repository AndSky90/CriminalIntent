package com.example.i550.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;

import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import android.text.format.DateFormat;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class CrimeFragment extends Fragment {
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    public  Button mDialButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;  //код запроса к таргет фрагменту
    private static final int REQUEST_CONTACT = 1; //в интент стартАкт4резулт это код ИД типа
    private static final int PERMISSION_REQUEST_CODE = 2;
    private static final int REQUEST_PHOTO = 3;

    public static CrimeFragment newInstance(UUID crimeID) {      //arguments bundle
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeID);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);        //что этот фрагмент может принимать коллбэки меню
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast toast = Toast.makeText(getContext(),"Read Contacts получен!!", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast toast = Toast.makeText(getContext(),"ХУЙ", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
        mTitleField = v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());     //берем из крайма
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mDateButton = v.findViewById(R.id.crime_date);    //кнопка с датой
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() { //по нажатию кнопки
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                //DatePickerFragment dialog = new DatePickerFragment(); -вместо этой хуйни вон тот конструктор
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                //создаем новый фрагмент-с-инстанц типа (берем дату у крайма и расшифровываем в фрагменте
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);  //назначаем таргет-фрагмент
                dialog.show(manager, DIALOG_DATE);
                //добавляет фрагмент с датой(диалог) в фрагментМанагер(манагер) и выводит на экран
            }
        });
        mSolvedCheckBox = v.findViewById(R.id.solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mReportButton = v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareCompat.IntentBuilder i = ShareCompat.IntentBuilder.from(getActivity());
                        i.setType("text/plain");
                        i.setText(getCrimeReport());
                        i.setSubject(getString(R.string.cr_subject));
                        i.setChooserTitle(getString(R.string.send_report));
                        i.startChooser();
                /*
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.cr_subject));
                i = Intent.createChooser(i,getString(R.string.send_report));    //везде так делать
                startActivity(i);*/
            }
        });


        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        mSuspectButton = v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact,REQUEST_CONTACT);
            }
        });

        mDialButton = v.findViewById(R.id.dial_button);
        mDialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast toast = Toast.makeText(getContext(),"Нет Read Contacts", Toast.LENGTH_SHORT);
                    toast.show();
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},PERMISSION_REQUEST_CODE);
                    // Permission is not granted
                } else{

                String suspect = mCrime.getSuspect();
                Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "=?";
                String[] projection    = new String[] /*{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,*/
                        {ContactsContract.CommonDataKinds.Phone.NUMBER};

                Cursor cursor = getActivity().getContentResolver().query(uri, projection, selection, new String [] {suspect},null);
                try{
                    if(cursor.getCount()==0){return;}
                    cursor.moveToFirst(); //извлекаем 1столбец - имя
                    String number = "tel:" + cursor.getString(0);
                    mDialButton.setText(number);
                    Uri num = Uri.parse(number);
                    final Intent intentDial = new Intent(Intent.ACTION_DIAL, num);
                    startActivity(intentDial);
                } finally{cursor.close();}
            }}
        });

        if (mCrime.getSuspect()==null) {mDialButton.setEnabled(false);}

        //проверка на отсутствие телефонной книги для интента
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity (pickContact, PackageManager.MATCH_DEFAULT_ONLY)==null)
            {mSuspectButton.setEnabled(false);}

        mPhotoButton=v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile!=null&&captureImage.resolveActivity(packageManager)!=null;
        mPhotoButton.setEnabled(canTakePhoto);      //проверка можно ли
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //преобразует путь в УРИ
                Uri uri = FileProvider.getUriForFile(getActivity(),"com.example.i550.criminalintent.fileprovider",mPhotoFile);
                //ебучий код чтоб получить флаг разрешения записи FGWURIP для каждой активности что вылезет для конкретно этого УРИ
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                List<ResolveInfo> cameraActivities = getActivity().getPackageManager().queryIntentActivities(captureImage,PackageManager.MATCH_DEFAULT_ONLY);
                for(ResolveInfo activity : cameraActivities){
                    getActivity().grantUriPermission(activity.activityInfo.packageName,uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage,REQUEST_PHOTO);
            }
        });


        mPhotoView=v.findViewById(R.id.crime_photo);
        updatePhotoView();
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        }
        else if (requestCode==REQUEST_CONTACT && data!=null) {
            Uri contactUri = data.getData();        //определение полей, возвращаемых запросом  (ContactsContract.Contacts.CONTENT_URI);
            String[] queryFields = new String[] {ContactsContract.Contacts.DISPLAY_NAME};
            //выполнение запроса - контактУри это WHERE типа
            //uri,projection,selection,selectionArgs,SortOrder
            Cursor c = getActivity().getContentResolver().query(contactUri,queryFields,null,null,null);
         try{
             if(c.getCount()==0){return;}
             c.moveToFirst(); //извлекаем 1столбец - имя
             String suspect = c.getString(0);
             mCrime.setSuspect(suspect);
             mSuspectButton.setText(suspect);
         }
         finally{c.close();}
        }
        else if (requestCode==REQUEST_PHOTO){
            Uri uri = FileProvider.getUriForFile(getActivity(),"com.example.i550.criminalintent.fileprovider",mPhotoFile);
            getActivity().revokeUriPermission(uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION); //забираем права
            updatePhotoView();
        }
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private String getCrimeReport(){
        String solvedString = null;
        if (mCrime.isSolved()){
            solvedString=getString(R.string.cr_solved);
        } else {
            solvedString=getString(R.string.cr_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();//хитроебаный импорт тут
        String suspect = mCrime.getSuspect();
        if(suspect==null) {
            suspect=getString(R.string.cr_no_suspect);
        } else {
            suspect=getString(R.string.cr_suspect, suspect);
        }
        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }
    private void updatePhotoView(){
        if(mPhotoFile==null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        } else {    //вызываем наши говнокоды
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {//переопределяем меню фрагмента(+инфлатор)
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);      //ресурс меню вливаем в Меню
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {       //реагирует на выбор пункта меню
        switch (item.getItemId()) {  //получаем ИД итема
            case R.id.delete_crime:
                CrimeLab.get(getActivity()).delCrime(mCrime);
                getActivity().finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }
}