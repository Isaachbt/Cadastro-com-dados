package com.curso.cadastrocomdados.activity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.curso.cadastrocomdados.R;
import com.curso.cadastrocomdados.activity.config.ConfiguracaoFirebase;
import com.curso.cadastrocomdados.activity.helper.UsuarioFirebase;
import com.curso.cadastrocomdados.activity.model.Dados;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class MinhaContaActivity extends AppCompatActivity {

    private TextView editNome, editIdade, editCep, editRua, editBairro, editCidade, editSexo, editEstadoCivil,editNumero;
    private DatabaseReference dadosUsuario;
    private String id = UsuarioFirebase.getIdentificadorUsuario();
    private ValueEventListener valueEventListener;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minha_conta);
        findview();

    }

    public void recuperarDadosUsuario() {
        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        dadosUsuario = firebase.child("usuario").child(id);
        valueEventListener = dadosUsuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Dados dados = snapshot.getValue(Dados.class);
                    editNome.setText(dados.getNome());
                    editIdade.setText(String.valueOf(dados.getIdade()));
                    editSexo.setText(dados.getSexo());
                    editRua.setText(dados.getRua());
                    editBairro.setText(dados.getBairro());
                    editCidade.setText(dados.getCidade());
                    editCep.setText(String.valueOf(dados.getCep()));
                    editEstadoCivil.setText(dados.getEstadoCivil());
                    editNumero.setText(String.valueOf(dados.getNumero()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "erro", Toast.LENGTH_SHORT);
            }
        });
  }




    @Override
    protected void onStart() {
        super.onStart();
        recuperarDadosUsuario();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dadosUsuario.removeEventListener(valueEventListener);
    }

    public void findview(){
        editBairro = findViewById(R.id.edicaoBairro);
        editNome = findViewById(R.id.edicaoNome);
        editIdade = findViewById(R.id.edicaoIdade);
        editCep = findViewById(R.id.edicaoCep);
        editRua = findViewById(R.id.edicaoRua);
        editCidade = findViewById(R.id.edicaoCidade);
        editSexo = findViewById(R.id.edicaoSexo);
        editEstadoCivil = findViewById(R.id.edicaoEstadoCivil);
        editNumero = findViewById(R.id.edicaoNumero);

    }

    public void editarDados(View view){
        startActivity(new Intent(getApplicationContext(),EditarDadosActivity.class));
        finish();
    }
}