package com.example.app_lecturas2.Ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.app_lecturas2.Data.ApiService
import com.example.app_lecturas2.Data.LoginRequest
import com.example.app_lecturas2.Data.LoginResponse_
import com.example.app_lecturas2.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContentView(R.layout.activity_login)
            Log.d("LoginActivity", "Layout cargado correctamente")
        } catch (e: Exception) {
            Log.e("LoginActivity", "Error inflando el layout", e)
            Toast.makeText(this, "Error al cargar la aplicación", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            usernameEditText = findViewById(R.id.usernameEditText)
            passwordEditText = findViewById(R.id.passwordEditText)
            loginButton = findViewById(R.id.loginButton)
            Log.d("LoginActivity", "findViewById ejecutado correctamente")
        } catch (e: Exception) {
            Log.e("LoginActivity", "Error al encontrar las vistas", e)
            Toast.makeText(this, "Error al inicializar las vistas", Toast.LENGTH_SHORT).show()
        }

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            login(username, password)
        }
    }

    private fun login(username: String, password: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://54.94.89.152")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val loginRequest = LoginRequest(username, password)

        apiService.login(loginRequest).enqueue(object : Callback<LoginResponse_> {
            override fun onResponse(call: Call<LoginResponse_>, response: retrofit2.Response<LoginResponse_>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putString("token", loginResponse.token)
                            apply()
                        }


                        val idUsuario = loginResponse.id_usuario
                        Log.d("LoginActivity", "ID de usuario recibido: $idUsuario")

                        val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                            putExtra("idOperario", idUsuario)
                        }
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse_>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error en la conexión", Toast.LENGTH_SHORT).show()
                Log.e("LoginActivity", "Error: ${t.message}")
            }
        })

    }


    private fun guardarUsuarioEnPreferencias(userId: Int) {
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt("id_usuario", userId)
            Log.d("LoginActivity", "ID de usuario recibido para guardar: $userId")
            apply()
        }
        Log.d("LoginActivity", "ID de usuario guardado en SharedPreferences: $userId")
    }

}
