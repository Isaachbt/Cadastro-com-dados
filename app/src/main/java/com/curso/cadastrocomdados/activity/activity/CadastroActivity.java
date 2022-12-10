package com.curso.cadastrocomdados.activity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.curso.cadastrocomdados.R;
import com.curso.cadastrocomdados.activity.api.CepService;
import com.curso.cadastrocomdados.activity.config.Base64Custon;
import com.curso.cadastrocomdados.activity.config.ConfiguracaoFirebase;
import com.curso.cadastrocomdados.activity.model.Cep;
import com.curso.cadastrocomdados.activity.model.Dados;
import com.curso.cadastrocomdados.activity.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CadastroActivity extends AppCompatActivity {
    private EditText editNome,editIdade,editCep,editRua,editBairro,editCidade,editEmail,editSenha,editRepetiSennha,editNumero;
    private Button btnFinalizarCadastor;
    private RadioButton sexoMasculino,sexoFemenino,sexoLgbt;
    private CheckBox aceitartermo,aceitarNotificao;
    private Spinner spinner;
    private ProgressBar progressBar;
    private String sexoEscolhido = null;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private String id;
    private String estadoCivil;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        findeViewByIDd();

        retrofit = new Retrofit
                .Builder()
                .baseUrl("https://viacep.com.br/ws/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        btnFinalizarCadastor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarCampos();
            }
        });

        spiner();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            estadoCivil = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void findeViewByIDd(){
        editBairro = findViewById(R.id.editBairro);
        editNome = findViewById(R.id.editNome);
        editIdade = findViewById(R.id.editIdade);
        editCep = findViewById(R.id.edtCep);
        editRua = findViewById(R.id.editRua);
        editCidade = findViewById(R.id.editCidade);
        editEmail = findViewById(R.id.editEmailCadastro);
        editSenha = findViewById(R.id.editSenhaCadastro);
        editRepetiSennha = findViewById(R.id.editSenhaNovamente);
        btnFinalizarCadastor = findViewById(R.id.btnCadastrar);
        sexoMasculino = findViewById(R.id.radioHomen);
        sexoFemenino = findViewById(R.id.radioMulher);
        sexoLgbt = findViewById(R.id.radioLgbt);
        spinner = findViewById(R.id.spinnerCadastro);
        progressBar = findViewById(R.id.progressBarCadastro);
        editNumero = findViewById(R.id.editNumero);
        aceitartermo = findViewById(R.id.aceitarTermos);
    }

    private void cadastrarUsuario(Usuario usuario) {
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            try {
                                id = Base64Custon.codificarBase64( usuario.getEmail());
                                salvarFirebase();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(),"Conta criada",Toast.LENGTH_SHORT).show();
                                finish();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else{
                            String execao = "";
                            try {
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e){
                                execao = "Digite uma senha mais forte!";
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                execao = "Por favor, digite um email mais forte!";
                            }catch (FirebaseAuthUserCollisionException e) {
                                execao = "Essa conta ja foi cadastrada!";
                            }catch (Exception e){
                                execao = "Erro ao cadastrar usuario" + e.getMessage();
                                e.printStackTrace();
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(),execao,Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void salvarFirebase() {
        Dados dados = new Dados();
        dados.setBairro(editBairro.getText().toString());
        dados.setRua(editRua.getText().toString());
        dados.setCidade(editCidade.getText().toString());
        dados.setCep(editCep.getText().toString());
        dados.setNome(editNome.getText().toString());
        dados.setIdade(editIdade.getText().toString());
        dados.setSexo(sexoEscolhido);
        dados.setEstadoCivil(estadoCivil);
        dados.setNumero(editNumero.getText().toString());
        dados.setId(id);
        dados.salvar();//aqui dentro da tem o metodo que salva os dados no firebase.
    }

    public void validarCampos(){
        radiobuttons();
      if (!editNumero.getText().toString().isEmpty()) {
          if (!editNome.getText().toString().isEmpty()) {
              if (!editIdade.getText().toString().isEmpty()) {
                  if (!editEmail.getText().toString().isEmpty()) {
                      if (!editSenha.getText().toString().isEmpty()) {
                          if (!editRepetiSennha.getText().toString().isEmpty()) {
                              if (!editCep.getText().toString().isEmpty()) {
                                  if (!editBairro.getText().toString().isEmpty()) {
                                      if (!editRua.getText().toString().isEmpty()) {
                                          if (!editCidade.getText().toString().isEmpty()) {
                                              if (radiobuttons()) {
                                                      if (editSenha.getText().toString().equals(editRepetiSennha.getText().toString())) {
                                                          if (aceitarTermos()) {
                                                              progressBar.setVisibility(View.VISIBLE);
                                                              Usuario usuario = new Usuario();
                                                              usuario.setEmail(editEmail.getText().toString());
                                                              usuario.setSenha(editSenha.getText().toString());
                                                              cadastrarUsuario(usuario);

                                                          }else{
                                                              Toast.makeText(this, "Aceite os termos para prosseguir!", Toast.LENGTH_SHORT).show();
                                                          }
                                                      } else {
                                                          Toast.makeText(getApplicationContext(), "A senha não e igual", Toast.LENGTH_SHORT).show();
                                                      }
                                                  } else {
                                                      Toast.makeText(getApplicationContext(), "Escolha seu sexo", Toast.LENGTH_SHORT).show();
                                              }
                                          }
                                      }
                                  }
                              }
                          }
                      }
                  } else {

                  }
              } else {

              }
          } else {

          }
      }

    }

    public boolean radiobuttons(){
        //boolean clicado = false;
        if (sexoMasculino.isChecked()){
            sexoEscolhido = "Masculino";
            return true;
        }else if (sexoFemenino.isChecked()){
            sexoEscolhido = "Femenino";
            return true;
        }else if (sexoLgbt.isChecked()){
            sexoEscolhido = "Lgbt";
            return true;
        }else{
            return false;
        }
        //return clicado;
    }
    
    public boolean aceitarTermos(){
        if (aceitartermo.isChecked()){
            return true;
        }else{
            return false;
        }
        
        
    }

    public void txtFazerLogin(View view){
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }

    public void spiner(){
        //dessa forma e criado um spinner
        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(this,R.array.estadoCivil, R.layout.spiner_layout);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.adapter_spinner);
        spinner.setAdapter(adapter);

    }

    public void buscarCep(View view){
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