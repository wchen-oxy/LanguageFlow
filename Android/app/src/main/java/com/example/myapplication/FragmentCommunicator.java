package com.example.myapplication;

import android.os.Bundle;

//we use an interface rather than a view model because we are communicating from frag to activity
public interface FragmentCommunicator {
//    void textPass(String string);
    void bundPass(Bundle args, boolean isPause);
//    void bundPass(Bundle args, boolean isPause, ResultWrapper resultWrapper);
//    void resultWrapPass(ResultWrapper resultWrapper);

}
