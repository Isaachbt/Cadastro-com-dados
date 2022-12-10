package com.curso.cadastrocomdados.activity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.curso.cadastrocomdados.R;
import com.curso.cadastrocomdados.activity.config.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private CircleImageView img;
    private int clickDouble = 0;
    private ViewStub viewStub;
    private TextView txtConfig;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = findViewById(R.id.circleImagen);
        viewStub = findViewById(R.id.viewStub);
        txtConfig = findViewById(R.id.txtConfig);



        //config viewStub
        viewStub.inflate();
        viewStub.setVisibility(View.INVISIBLE);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //doublo click no button, pode ate mas
                switch (clickDouble){
                    case 0:
                        viewStub.setVisibility(View.VISIBLE);
                        clickDouble+=1;
                        break;
                    case 1:
                        viewStub.setVisibility(View.INVISIBLE);
                        clickDouble = 0;
                        break;
                }
            }
        });



    }

    public void minhaConta(View view){
        startActivity(new Intent(getApplicationContext(),MinhaContaActivity.class));
        viewStub.setVisibility(View.INVISIBLE);
        finish();
    }

    public void sairConta(View view){
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        try {
            autenticacao.signOut();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void informacao(View view){
        Toast.makeText(this, "Em desenvolvimento!", Toast.LENGTH_SHORT).show();
        viewStub.setVisibility(View.INVISIBLE);
    }
}