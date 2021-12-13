package com.inscription.ui.home;

import static com.inscription.library.util.AppUtils.getApplicationName;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.inscription.R;
import com.inscription.databinding.FragmentHomeBinding;
import com.inscription.library.ChangeLogDialog;
import com.inscription.library.CreditsDialog;
import com.inscription.library.LicenseDialog;
import com.inscription.library.ThanksDialog;
import com.inscription.library.util.AppUtils;

import java.io.IOException;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getButton();
    }

    public void getButton(){
        final Button button1 = getActivity().findViewById(R.id.button1); //Boton View ChangeLog
        final Button button2 = getActivity().findViewById(R.id.button2); //Boton View Credits
        final Button button3 = getActivity().findViewById(R.id.button3); //Boton View Tranks
        final Button button4 = getActivity().findViewById(R.id.button4); //Boton View Tranks
        final Button button5 = getActivity().findViewById(R.id.button5); //Boton View Tranks

        /* Display Commands Buttons Application */
        View.OnClickListener listener = new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button1: //Boton View ChangeLog
                        ChangeLogDialog changeLogDialog = new ChangeLogDialog(getContext());
                        changeLogDialog.show();
                        break;
                    case R.id.button2: //Boton View Credits
                        try {
                            // Then launch the activity to send that file via gmail.
                            AppUtils.sendEmail(getContext());

                        }
                        // Catch if Gmail is not available on this device
                        catch (ActivityNotFoundException e) {
                            Toast.makeText(getActivity(), "Gmail is not available on this device.", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case R.id.button3: //Boton View Credits
                        CreditsDialog creditsDialog = new CreditsDialog(getContext());
                        creditsDialog.show();
                        break;
                    case R.id.button4: //Boton View Thanks
                        ThanksDialog thanksDialog = new ThanksDialog(getContext());
                        thanksDialog.show();
                        break;
                    case R.id.button5: //Boton View Licenses
                        LicenseDialog licenseDialog = new LicenseDialog(getContext());
                        licenseDialog.show();
                        break;
                }
            }
        };
        button1.setOnClickListener(listener);
        button2.setOnClickListener(listener);
        button3.setOnClickListener(listener);
        button4.setOnClickListener(listener);
        button5.setOnClickListener(listener);
    }
}