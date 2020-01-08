package com.example.myapplication.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.example.myapplication.R;

public class DictionarySelectionFrag extends Fragment {

    public static DictionarySelectionFrag newInstance() {

        Bundle args = new Bundle();
        DictionarySelectionFrag fragment = new DictionarySelectionFrag();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dictionary_selection, container,false);
        Button Bhutia = rootView.findViewById(R.id.bhutia_selected);
        Button English = rootView.findViewById(R.id.english_selected);

        SharedPreferences pref = getActivity().getSharedPreferences("BlankDictPref", 0); // 0 - for private mode
        final SharedPreferences.Editor editor = pref.edit();


        Bhutia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Dict Changed to Bhu", Toast.LENGTH_SHORT).show();
                editor.putString("CurDict", "BHUTIA"); // Storing String
                editor.commit(); // commit changes
            }
        });

        English.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Dict Changed to Eng", Toast.LENGTH_SHORT).show();
                editor.putString("CurDict", "ENGLISH"); // Storing String
                editor.commit(); // commit changes

            }
        });
        return rootView;
    }


}
