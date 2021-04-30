package manyosoft.guinyote.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import manyosoft.guinyote.R;
import manyosoft.guinyote.util.GuinyoteClienteJWT;
import manyosoft.guinyote.util.Usuario;

public class MainMenuFragment extends Fragment {

    private Button logout;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.main_menu, container, false);

    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.jugar_online_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(MainMenuFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });


        view.findViewById(R.id.jugar_solo_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getContext();
                CharSequence text = "Inicio de partida rápido vs IA";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });


        view.findViewById(R.id.perfil_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getContext();
                Intent i = new Intent(context, UserProfile.class);
                startActivity(i);
            }
        });

        logout = view.findViewById(R.id.button_logout);
        logout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        act_logOut();
                    }
                }
        );
    }

    private void act_logOut(){
        Usuario user = Usuario.getInstance();
        user.logOut();
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
    }
}