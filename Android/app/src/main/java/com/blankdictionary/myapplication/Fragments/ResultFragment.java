package com.blankdictionary.myapplication.Fragments;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.blankdictionary.myapplication.Adapters.DumbSpinnerAdapter;
import com.blankdictionary.myapplication.Dictionaries.Bhutia.BhutiaLayout;
import com.blankdictionary.myapplication.Dictionaries.English.EnglishLayout;
import com.blankdictionary.myapplication.HelperInterfaces.ILayoutSetter;
import com.blankdictionary.myapplication.R;

import static com.blankdictionary.myapplication.Constants.DictionaryData.QUERY_ID;
import static com.blankdictionary.myapplication.Constants.DictionaryData.TRANSLATION_STRING;
import static com.blankdictionary.myapplication.Constants.SupportedDictionaries.BHUTIA;
import static com.blankdictionary.myapplication.Constants.SupportedDictionaries.ENGLISH;
import static com.blankdictionary.myapplication.Constants.System.APP_PREFERENCES;
import static com.blankdictionary.myapplication.Constants.System.CURRENTLY_SELECTED_DICTIONARY;

public class ResultFragment extends Fragment {

    private String curDict;
    private String currentQuery;
    private Bundle args;
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();

            }
            return false;
        }
    };
    private View.OnClickListener myListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        }
    };


    public ResultFragment() {
        // Required empty public constructor
    }

    //    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment ResultFragment.
//     */
    // TODO: Rename and change types and number of parameters
    public static ResultFragment newInstance(Bundle args) {
        ResultFragment resultFragment = new ResultFragment();
        resultFragment.setArguments(args);
        return resultFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        args = getArguments();
        SharedPreferences pref = getContext().getSharedPreferences(APP_PREFERENCES, 0);
        curDict = pref.getString(CURRENTLY_SELECTED_DICTIONARY, null);
        currentQuery = args.getString(TRANSLATION_STRING, "");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_result, container, false);
        //initialize return listener
        rootView.findViewById(R.id.final_result_frag).setOnClickListener(myListener);
        //set spinner return listener
        Spinner spinner = rootView.findViewById(R.id.final_result_adv_trans_spinner);
        DumbSpinnerAdapter dumbSpinnerAdapter = new DumbSpinnerAdapter(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, myListener);
        dumbSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setOnTouchListener(touchListener);
        spinner.setAdapter(dumbSpinnerAdapter);


        //setting results
        ScrollView dictInfoContainer = rootView.findViewById(R.id.dict_info_container);

        ILayoutSetter layoutSetter = null;
        switch (curDict) {
            case (BHUTIA):
                layoutSetter = new BhutiaLayout(getContext(), inflater, args);
                break;
            case (ENGLISH):
                layoutSetter = new EnglishLayout(getContext(), inflater, args);
                break;

        }

        dictInfoContainer.addView(layoutSetter.getDictionaryLayout().returnView());
        return rootView;

    }


    @Override
    public void onDetach() {
        super.onDetach();

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
