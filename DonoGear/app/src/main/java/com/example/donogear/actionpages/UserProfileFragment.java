package com.example.donogear.actionpages;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.donogear.registeration.LauncherActivity;
import com.example.donogear.registeration.LoginPageActivity;
import com.facebook.login.LoginManager;
import com.parse.ParseUser;

import com.example.donogear.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment {

    private Button logout;
    private View view;
    private MainActivity activity;

    public UserProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
        initializeLayout();
        return view;
    }

    private void initializeLayout() {
//        Toolbar toolbar = view.findViewById(R.id.toolbar);
//        activity.setSupportActionBar(toolbar);
        logout = view.findViewById(R.id.userlogout_btn);

        logout.setOnClickListener(v -> {
            System.out.println("LOGOUT");
            LoginManager.getInstance().logOut();
            ParseUser.logOut();
//                alertDisplayer("So, you're going...", "Ok...Bye-bye then");
            Intent intent = new Intent(activity, LoginPageActivity.class);
//                intent.putExtra("finish", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        });
    }

    public void logoutBtnClicked() {


    }

//    private void alertDisplayer(String title,String message){
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
//                .setTitle(title)
//                .setMessage(message)
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
////                        Intent intent = new Intent(LoginPageActivity.this, MainActivity.class);
////                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
////                        startActivity(intent);
//                    }
//                });
//        AlertDialog ok = builder.create();
//        ok.show();
//    }
}
