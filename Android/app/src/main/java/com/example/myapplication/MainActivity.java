package com.example.myapplication;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.myapplication.BroadcastRecievers.myAvailableDictionaryReciever;
import com.example.myapplication.BroadcastRecievers.myDictionaryDownloadReceiver;
import com.example.myapplication.BroadcastRecievers.myServerStatusReciever;
import com.example.myapplication.DataDownload.DictionaryClientUsage;
import com.example.myapplication.DataDownload.HttpBadRequestException;
import com.example.myapplication.Fragments.DictionarySelectionFragment;
import com.example.myapplication.Fragments.HomeFragment;
import com.example.myapplication.Fragments.LanguagePackFragment;
import com.example.myapplication.Fragments.ResultFragment;
import com.example.myapplication.Fragments.SearchFragment;
import com.example.myapplication.Fragments.SettingsFragment;
import com.example.myapplication.HelperInterfaces.IDelete;
import com.example.myapplication.HelperInterfaces.IFragmentCommunicator;
import com.example.myapplication.HelperInterfaces.IOnBackPressed;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.myapplication.Constants.Fragment.DICTIONARY_SELECTION_FRAGMENT;
import static com.example.myapplication.Constants.Fragment.HOME_FRAGMENT;
import static com.example.myapplication.Constants.Fragment.LANGUAGE_DOWNLOAD_FRAGMENT;
import static com.example.myapplication.Constants.Fragment.NEW_FRAGMENT;
import static com.example.myapplication.Constants.Fragment.RESULT_FRAGMENT;
import static com.example.myapplication.Constants.Fragment.SEARCH_FRAGMENT;
import static com.example.myapplication.Constants.Fragment.SETTINGS_FRAGMENT;
import static com.example.myapplication.Constants.IntentFilters.DICTIONARY_LIST_DOWNLOADED;
import static com.example.myapplication.Constants.IntentFilters.SERVER_REACHED;
import static com.example.myapplication.Constants.Network.LANG_DOWNLOAD_HANDLER_THREAD_NAME;
import static com.example.myapplication.Constants.Network.NO_INTERNET_ERROR;
import static com.example.myapplication.Constants.System.APP_NAME;
import static com.example.myapplication.Constants.System.APP_PREFERENCES;
import static com.example.myapplication.Constants.System.CURRENTLY_SELECTED_DICTIONARY;
import static com.example.myapplication.Constants.System.DOWNLOAD_ID;
import static com.example.myapplication.Constants.System.DOWNLOAD_TYPE;

public class MainActivity extends AppCompatActivity implements IFragmentCommunicator, IDelete {
    final Context context = this;
    public BroadcastReceiver myAvailableDictionaryReciever;
    public Boolean isAdvSearch = false;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    BottomNavigationView navViewBack;
    myServerStatusReciever myServerStatusReciever;
    LocalBroadcastManager localBroadcastManager;
    Bundle args;
    ArrayList<String> langToDelete;
    private String activeLang = "";
    private SharedPreferences pref;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (isAdvSearch && activeLang == pref.getString(CURRENTLY_SELECTED_DICTIONARY, null)) {
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frag_container, SearchFragment.newInstance(args), SEARCH_FRAGMENT).commit();
                        return true;
                    }
                    clearBackStack();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frag_container, HomeFragment.newInstance(), HOME_FRAGMENT).commit();
                    return true;


                case R.id.navigation_settings:

                    clearBackStack();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frag_container, SettingsFragment.newInstance(), SETTINGS_FRAGMENT).commit();

                    return true;
            }
            return false;
        }
    };

    //to create all fragments
    @Override
    public void bundPass(Bundle args, boolean isPause) {
        if (isPause) {
            this.args = args;
        } else {
            fragController(args);
        }
    }

    private void fragController(Bundle args) {
        switch (args.getString(NEW_FRAGMENT)) {
            case HOME_FRAGMENT:
                HomeFragment homeFragment = HomeFragment.newInstance();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.frag_container, homeFragment, HOME_FRAGMENT).commit();
                break;

            case SEARCH_FRAGMENT:
                SearchFragment searchFragment = SearchFragment.newInstance(args);
                activeLang = pref.getString(CURRENTLY_SELECTED_DICTIONARY, null);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, searchFragment, SEARCH_FRAGMENT).addToBackStack(SEARCH_FRAGMENT).commit();
                isAdvSearch = true;
                break;
            case RESULT_FRAGMENT:
                ResultFragment resultFragment = ResultFragment.newInstance(args);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, resultFragment).addToBackStack(RESULT_FRAGMENT).commit();
                break;
            case DICTIONARY_SELECTION_FRAGMENT:
                langToDelete = new ArrayList<>();
                DictionarySelectionFragment dictionarySelectionFragment = DictionarySelectionFragment.newInstance();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, dictionarySelectionFragment).addToBackStack(DICTIONARY_SELECTION_FRAGMENT).commit();
                break;

//            case LANGUAGE_DOWNLOAD_FRAGMENT:
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    // Permission is not granted
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                        // Show an explanation to the user *asynchronously* -- don't block
//                        // this thread waiting for the user's response! After the user
//                        // sees the explanation, try again to request the permission.
//                    } else {
//                        // No explanation needed; request the permission
//                        ActivityCompat.requestPermissions(this,
//                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
//                                , 101);
//
//                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                        // app-defined int constant. The callback method gets the
//                        // result of the request.
//                    }
//                } else {
//                    // Permission has already been granted
//                    LanguagePackFragment languagePackFragment = LanguagePackFragment.newInstance();
//                    fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.frag_container, languagePackFragment).addToBackStack(LANGUAGE_DOWNLOAD_FRAGMENT).commit();
//                }
//
//
//                break;

        }

    }

    private void clearBackStack() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
            fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frag_container);
        //if it is an instance, and if the instance returns true, do nothing
        if (fragment instanceof IOnBackPressed && ((IOnBackPressed) fragment).clearText()) return;
        //otherwise do something
        super.onBackPressed();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences(APP_PREFERENCES, 0);
        setContentView(R.layout.partial_main_frame);
        final BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navViewBack = navView;
        fragmentManager = getSupportFragmentManager();
        Bundle args = new Bundle();
        args.putString(NEW_FRAGMENT, HOME_FRAGMENT);
        fragController(args);


        //SECTION FOR GETTING AVAILABLE DICTIONARIES AHEAD OF TIME
        if (!(new File(Environment.getExternalStorageDirectory(), APP_NAME).isDirectory())) {
            new File(Environment.getExternalStorageDirectory(), APP_NAME).mkdir();
        }
        //check internet connection
        if (!isOnline()) Toast.makeText(this, NO_INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        //check if connection to server is possible
        myServerStatusReciever = new myServerStatusReciever();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(myServerStatusReciever, new IntentFilter(SERVER_REACHED));

        myAvailableDictionaryReciever = new myAvailableDictionaryReciever();
        localBroadcastManager.registerReceiver(myAvailableDictionaryReciever, new IntentFilter(DICTIONARY_LIST_DOWNLOADED));

        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    new DictionaryClientUsage().checkStatus(context);
                } catch (HttpBadRequestException e) {
                    e.printStackTrace();
                }

            }

        };
        mainHandler.post(myRunnable);


    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myServerStatusReciever);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myAvailableDictionaryReciever);

        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        System.out.println("Resume REACHED");
////
//        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
//        DownloadManager.Query downloadManagerQuery = new DownloadManager.Query();
//        downloadManager.query(downloadManagerQuery);
//        downloadManagerQuery.setFilterById(pref.getLong(DOWNLOAD_ID, -1)).setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
//        Cursor cursor = null;
////        int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
//        if (pref.getLong(DOWNLOAD_ID, -1) != -1)
//            cursor = downloadManager.query(new DownloadManager.Query().setFilterById(pref.getLong(DOWNLOAD_ID, -1)));
//
//        if (cursor != null && cursor.moveToNext()) {
//            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
//            cursor.close();
//            System.out.println("DOWNLOAD...");
//
//
//            if (status == DownloadManager.STATUS_FAILED) {
//                System.out.println("FAIL");
//                // do something when failed
//            }
//            else if (status == DownloadManager.STATUS_PENDING || status == DownloadManager.STATUS_PAUSED) {
//                System.out.println("PENDING/ PAUSED");
//
//                // do something pending or paused
//            }
//            else if (status == DownloadManager.STATUS_SUCCESSFUL) {
//                System.out.println("SUCCESS");
//
//                // do something when successful
//            }
//            else if (status == DownloadManager.STATUS_RUNNING) {
//                System.out.println("RUNNING");
//
//                // do something when running
//            }
//        }
//
//        if (pref.getLong(DOWNLOAD_ID, -4) != -4) {
//            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
//            switch (status) {
//                case DownloadManager.STATUS_PAUSED:
//                    System.out.println("Paused");
//
//                    break;
//                case DownloadManager.STATUS_PENDING:
//                    System.out.println("Pending");
//
//                    break;
//                case DownloadManager.STATUS_RUNNING:
//                    System.out.println("Running");
//
//                    break;
//                case DownloadManager.STATUS_SUCCESSFUL:
//                    System.out.println("Success");
//
//                    break;
//                case DownloadManager.STATUS_FAILED:
//                    System.out.println("failed");
//
//                    break;
//            }
//        }
//        String type = pref.getString(DOWNLOAD_TYPE, "");
//        if (cursor.moveToFirst() && !type.isEmpty()){
//            System.out.println("INNER REACHED");
//            BroadcastReceiver broadcastReceiver = new myDictionaryDownloadReceiver(type);
//            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
//            HandlerThread handlerThread = new HandlerThread(LANG_DOWNLOAD_HANDLER_THREAD_NAME);
//            handlerThread.start();
//            Looper looper = handlerThread.getLooper();
//            Handler handler = new Handler(looper);
//            context.registerReceiver(broadcastReceiver, filter, null, handler);
//        }
//
//        cursor.close();
    }

    //Checkbox related functions

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        if (checked) {langToDelete.add(view.getTag().toString());
        System.out.println(view.getTag().toString());}
        else {
            for (int i = 0; i < langToDelete.size(); i++) {
                if (langToDelete.get(i) == view.getTag().toString()) langToDelete.remove(i);
            }
        }
    }

    @Override
    public void delete() {
        System.out.println((langToDelete));
        new DatabaseClear(this).execute(langToDelete);
    }

    @Override
    public void clearLangToDeleteList() {
        langToDelete.clear();
    }
}
