package manyosoft.guinyote.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import manyosoft.guinyote.R;
import manyosoft.guinyote.util.GuinyoteClienteJWT;

public class Registro extends AppCompatActivity {

    private EditText userName;
    private EditText email;
    private EditText location;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        userName = findViewById(R.id.registro_usuario);
        email = findViewById(R.id.registro_email);
        location = findViewById(R.id.registro_ubicacion);
        password = findViewById(R.id.registro_password);
        
        FloatingActionButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Button accept = findViewById(R.id.registro_aceptar);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });
    }

    private void createUser(){
        if(userName.getText().toString() == "" || email.getText().toString() == "" || location.getText().toString() == "" || password.getText().toString() == ""){
            //hay algun campo sin rellenar
            CharSequence text = "RELLENE TODOS LOS CAMPOS POR FAVOR.";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        }
        else {// Todos los campos rellenados
            GuinyoteClienteJWT guinyoteClienteJWT = GuinyoteClienteJWT.getInstance();
            boolean error = guinyoteClienteJWT.crearUsuario(this, location.getText().toString(), userName.getText().toString()
                    , email.getText().toString(), password.getText().toString());
            if (!error) {//registro correcto
                Intent i = new Intent(this, MainMenuFragment.class);
                startActivityForResult(i,0);
            } else {//registro incorrecto
                CharSequence text = "ERROR AL CREAR EL USUARIO.";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(this, text, duration);
                toast.show();
            }
        }
    }
}