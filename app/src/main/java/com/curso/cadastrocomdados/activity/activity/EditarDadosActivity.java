package com.curso.cadastrocomdados.activity.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.curso.cadastrocomdados.R;
import com.curso.cadastrocomdados.activity.api.CepService;
import com.curso.cadastrocomdados.activity.config.ConfiguracaoFirebase;
import com.curso.cadastrocomdados.activity.helper.UsuarioFirebase;
import com.curso.cadastrocomdados.activity.model.Cep;
import com.curso.cadastrocomdados.activity.model.Dados;
import com.curso.cadastrocomdados.activity.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditarDadosActivity extends AppCompatActivity {
    private EditText editNome, editIdade, editCep, editRua, editBairro, editCidade, editNumero;
    private RadioButton sexoMasculino, sexoFemenino, sexoLgbt;
    private Spinner spinner;
    private String estadoCivilEscolhido;
    private String sexoEscolhido;
    private DatabaseReference dadosUsuario;
    private String id = UsuarioFirebase.getIdentificadorUsuario();
    private ValueEventListener valueEventListener;
    private Retrofit retrofit;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),MinhaContaActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_dados);
        findeViewByIDd();

        retrofit = new Retrofit
                .Builder()
                .baseUrl("https://viacep.com.br/ws/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        spiner();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                estadoCivilEscolhido = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void findeViewByIDd() {
        editBairro = findViewById(R.id.editBairroEditarDados);
        editNome = findViewById(R.id.editNomeEditarDados);
        editIdade = findViewById(R.id.editIdadeEditarDados);
        editCep = findViewById(R.id.edtCepEditarDados);
        editRua = findViewById(R.id.editRuaEdicao);
        editCidade = findViewById(R.id.editCidadeEditarDados);
        sexoMasculino = findViewById(R.id.radioHomenEditarDados);
        sexoFemenino = findViewById(R.id.radioMulherEditarDados);
        sexoLgbt = findViewById(R.id.radioLgbtEditarDados);
        spinner = findViewById(R.id.spinnerEditarDados);
        //progressBar = findViewById(R.id.progressBarCadastro);
        editNumero = findViewById(R.id.editNumeroEdicao);
    }

    public void validarCampos() {
        radiobuttons();
        if (!editNumero.getText().toString().isEmpty()) {
            if (!editNome.getText().toString().isEmpty()) {
                if (!editIdade.getText().toString().isEmpty()) {
                    if (!editCep.getText().toString().isEmpty()) {
                        if (!editBairro.getText().toString().isEmpty()) {
                            if (!editRua.getText().toString().isEmpty()) {
                                if (!editCidade.getText().toString().isEmpty()) {
                                    if (radiobuttons()) {
                                        salvarFirebase();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Escolha seu sexo", Toast.LENGTH_SHORT).show();
                                    }
                                }}}}}}}}

    private void salvarFirebase() {
        Dados dados = new Dados();
        dados.setBairro(editBairro.getText().toString());
        dados.setRua(editRua.getText().toString());
        dados.setCidade(editCidade.getText().toString());
        dados.setCep(editCep.getText().toString());
        dados.setNome(editNome.getText().toString());
        dados.setIdade(editIdade.getText().toString());
        dados.setSexo(sexoEscolhido);
        dados.setEstadoCivil(estadoCivilEscolhido);
        dados.setNumero(editNumero.getText().toString());
        dados.setId(id);
        dados.salvar();//aqui dentro da tem o metodo que salva os dados no firebase.
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
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
                    sexoEscolhido = dados.getSexo();
                    editRua.setText(dados.getRua());
                    editBairro.setText(dados.getBairro());
                    editCidade.setText(dados.getCidade());
                    editCep.setText(String.valueOf(dados.getCep()));
                    editNumero.setText(String.valueOf(dados.getNumero()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "erro", Toast.LENGTH_SHORT);
            }
        });
    }

    public void btnSalvarAuteracaoes(View view){
        validarCampos();
    }

    public boolean radiobuttons(){
        boolean clicado = false;
        if (sexoMasculino.isChecked()){
            sexoEscolhido = "Masculino";
            clicado = true;
        }else if (sexoFemenino.isChecked()){
            sexoEscolhido = "Femenino";
            clicado = true;
        }else if (sexoLgbt.isChecked()){
            sexoEscolhido = "Lgbt";
            clicado = true;
        }else{
            clicado = false;
        }
        return clicado;
    }

    public void spiner(){
        //dessa forma e criado um spinner
        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(this,R.array.estadoCivil, R.layout.spiner_layout);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.adapter_spinner);
        spinner.setAdapter(adapter);

    }

    public void buscarCepEdicao(View view){
        CepService cepService = retrofit.create(CepService.class);
        Call<Cep> call = cepService.recuperarCep(editCep.getText().toString());

        call.enqueue(new Callback<Cep>() {
            @Override
            public void onResponse(Call<Cep> call, Response<Cep> response) {
                if (response.isSuccessful()){
                    Cep cep = response.body();
                    editCep.setText(cep.getCep());
                    editRua.setText(cep.getLogradouro());
                    editBairro.setText(cep.getBairro());
                    editCidade.setText(cep.getLocalidade()+" - "+cep.getUf());
                }
            }

            @Override
            public void onFailure(Call<Cep> call, Throwable t) {

            }
        });


    }
}