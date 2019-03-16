package com.example.carbin2;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;


public class frag_settings extends Fragment {

    private SeekBar bar;
    private TextView text,email;
    private Spinner spinner_speed,spinner_distance,spinner_fuel;
    private ArrayAdapter<CharSequence> adapter_speed,adapter_distance,adapter_fuel;


    public frag_settings() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        bar = view.findViewById(R.id.seek);
        text = view.findViewById(R.id.text_freq);
        spinner_speed = view.findViewById(R.id.spinner_speed);
        spinner_distance = view.findViewById(R.id.spinner_distance);
        spinner_fuel = view.findViewById(R.id.spinner_fuel);
        email = view.findViewById(R.id.email);

        adapter_speed = ArrayAdapter.createFromResource(getContext(),R.array.speed,android.R.layout.simple_spinner_item);
        adapter_speed.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_speed.setAdapter(adapter_speed);

        adapter_distance = ArrayAdapter.createFromResource(getContext(),R.array.distance,android.R.layout.simple_spinner_item);
        adapter_distance.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_distance.setAdapter(adapter_distance);

        adapter_fuel = ArrayAdapter.createFromResource(getContext(),R.array.fuel,android.R.layout.simple_spinner_item);
        adapter_fuel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_fuel.setAdapter(adapter_fuel);

        setSelections();

        chooseFrequency();
        chooseSpeed();
        chooseDistance();
        chooseFuel();
        sendEmail();

        return view;
    }

    private void setSelections() {
        text.setText("Set frequency: "+ String.valueOf(((MainActivity) Objects.requireNonNull(getActivity())).frequency) +" Hz");
        bar.setProgress(((MainActivity) Objects.requireNonNull(getActivity())).frequency);
        spinner_speed.setSelection(((MainActivity) Objects.requireNonNull(getActivity())).speed);
        spinner_distance.setSelection(((MainActivity) Objects.requireNonNull(getActivity())).distance);
        spinner_fuel.setSelection(((MainActivity) Objects.requireNonNull(getActivity())).fuel);
    }

    private void chooseFrequency() {
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                text.setText("Set Frequency: " + Integer.toString(i) + " Hz");
                ((MainActivity) Objects.requireNonNull(getActivity())).frequency = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void chooseSpeed() {
        spinner_speed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((MainActivity) Objects.requireNonNull(getActivity())).speed = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void chooseDistance() {
        spinner_distance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((MainActivity) Objects.requireNonNull(getActivity())).distance = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void chooseFuel() {
        spinner_fuel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((MainActivity) Objects.requireNonNull(getActivity())).fuel = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void sendEmail() {
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] receipients = {"carbin-team@gmail.com"};

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, receipients);

                intent.setType("message/rfc822");
                startActivity(Intent.createChooser(intent, "Choose an email client"));
            }
        });
    }

    private void toast(String s) {
        Toast.makeText(getActivity(),s, Toast.LENGTH_LONG).show();
    }

}
