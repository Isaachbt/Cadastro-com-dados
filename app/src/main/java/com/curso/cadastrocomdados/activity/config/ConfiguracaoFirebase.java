package com.curso.cadastrocomdados.activity.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase {
    private static DatabaseReference database;
    private static FirebaseAuth auth;

    //Salva dados no firebase
    //Recupera dados do firebases
    public static DatabaseReference getFirebaseDatabase()
    {
        if (database == null){
            database = FirebaseDatabase.getInstance().getReference();
        }
        return database;
    }

    //Permite logar usuarios no firebase
    //Recupera usuarios logados no firebase
    public static FirebaseAuth getFirebaseAutenticacao()
    {
        if (auth == null){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }
}
