package com.example.proyectofinal

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinal.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ConfigReader.init(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        // Accedemos al header del NavigationView
        val headerView = navView.getHeaderView(0)
        val tvNombreUsuario = headerView.findViewById<TextView>(R.id.tvNombreUsuario)

// Leemos el nombre del usuario desde SharedPreferences
        val prefs = getSharedPreferences("my_prefs", MODE_PRIVATE)
        val nombreUsuario = prefs.getString("nombre_usuario", "Invitado")

// Lo mostramos en el TextView del header
        tvNombreUsuario.text = nombreUsuario?.uppercase() ?: ""

        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_gallery,
                R.id.nav_slideshow,
                R.id.nav_registrar,
                R.id.nav_escanear,
                R.id.nav_consultar_incidencias,
                R.id.nav_lista_equipos_aulas,
                R.id.nav_lista_equipos_equipo,
                R.id.nav_informacion_equipo,
                R.id.nav_detalles_incidencia
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_crear_incidencias -> {
                    mostrarDialogoSoloTexto()
                    drawerLayout.closeDrawers()
                    true
                }

                else -> {
                    navController.navigate(menuItem.itemId)
                    drawerLayout.closeDrawers()
                    true
                }
            }
        }
    }

    private fun mostrarDialogoSoloTexto() {
        AlertDialog.Builder(this)
            .setMessage(
                "Para crear una nueva incidencia, seleccione primero el equipo a reparar desde la lista de equipos" +
                        " y pulse el botón para añadirla."
            )
            .setPositiveButton("ENTENDIDO") { dialog, _ ->
                dialog.dismiss() // Cierra el diálogo cuando se presiona el botón "Cerrar"
            }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                cerrarSesion()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun cerrarSesion() {
        // Limpia SharedPreferences (por ejemplo, las credenciales guardadas)
        val prefs = getSharedPreferences("my_prefs", MODE_PRIVATE)
        prefs.edit().clear().apply()

        // Navega a la actividad de Login y cierra MainActivity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}