package com.curso.cadastrocomdados.activity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.curso.cadastrocomdados.R;
import com.curso.cadastrocomdados.activity.config.ConfiguracaoFirebase;
import com.curso.cadastrocomdados.activity.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private Button btnEntrar;
    private EditText editEmail,editSenha;
    private ProgressBar progressBar;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        btnEntrar = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBarLogin);

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editEmail.getText().toString().isEmpty()){
                    if (!editSenha.getText().toString().isEmpty()){
                        progressBar.setVisibility(View.VISIBLE);
                        Usuario usuario = new Usuario();
                        usuario.setEmail(editSenha.getText().toString());
                        usuario.setSenha(editSenha.getText().toString());
                        loginUsuario(usuario);
                    }else{
                        Toast.makeText(LoginActivity.this, "Digite a senha", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "Digite o email", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loginUsuario(Usuario usuario) {
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            progressBar.setVisibility(View.INVISIBLE);
                            finish();
                        } else {
                            String execao;
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                execao = "Usuário não cadastrado!";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                execao = "Email ou senha não correspondem a um usuário";
                            } catch (Exception e) {
                                execao = "Erro ao cadastrar usuario" + e.getMessage();
                                e.printStackTrace();
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), execao, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Verificando se o usuario esta logado e mandando ele diretamente pra tela principal sem necessitar de fazer login novamente
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if (usuarioAtual != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    public void txtCriarConta(View view){
        startActivity(new Intent(getApplicationContext(),CadastroActivity.class));
        finish();
    }
}