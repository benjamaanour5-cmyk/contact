
package com.example.contacts

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // 1. Liste modifiable des contacts
    private val contacts = mutableListOf(
        "Ahmed Ben Ali",
        "Sarra Trabelsi",
        "Mohamed Gharbi",
        "Ines Jaziri",
        "Youssef Mansouri",
        "Amira Chaabane",
        "Karim Bouazizi",
        "Nour Hammami"
    )

    // 2. Adaptateur
    private lateinit var adapter: ArrayAdapter<String>

    // 3. Composants
    private lateinit var etContact: EditText
    private lateinit var btnModifier: Button
    private lateinit var btnAjouter: Button
    private lateinit var lvContacts: ListView

    // Position du contact sélectionné
    private var positionSelectionnee = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Récupérer les composants XML
        etContact = findViewById(R.id.etContact)
        btnModifier = findViewById(R.id.btnModifier)
        btnAjouter = findViewById(R.id.btnAjouter)
        lvContacts = findViewById(R.id.lvContacts)

        // 4. Créer l'adaptateur
        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_activated_1,
            contacts
        )

        lvContacts.adapter = adapter

        // 5. Sélectionner un contact
        lvContacts.setOnItemClickListener { _, _, position, _ ->

            positionSelectionnee = position

            lvContacts.setItemChecked(position, true)

            etContact.setText(contacts[position])

            etContact.setSelection(etContact.text.length)
            etContact.requestFocus()
        }

        // 6. Bouton Modifier
        btnModifier.setOnClickListener {
            modifierContact()
        }

        // 7. Bouton Ajouter
        btnAjouter.setOnClickListener {
            ajouterContact()
        }

        // 8. Supprimer avec un appui long
        lvContacts.setOnItemLongClickListener { _, _, position, _ ->

            val nomContact = contacts[position]

            AlertDialog.Builder(this)
                .setTitle("Supprimer le contact")
                .setMessage("Voulez-vous supprimer $nomContact ?")
                .setPositiveButton("Oui") { _, _ ->

                    contacts.removeAt(position)

                    adapter.notifyDataSetChanged()

                    positionSelectionnee = -1
                    lvContacts.clearChoices()
                    etContact.text.clear()

                    Toast.makeText(
                        this,
                        "Contact supprimé",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setNegativeButton("Non", null)
                .show()

            true
        }

        // 9. Touche Terminé du clavier
        etContact.setOnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                modifierContact()
                true
            } else {
                false
            }
        }
    }

    // EXERCICE 1 : Ajouter un contact
    private fun ajouterContact() {

        val nouveauNom = etContact.text.toString().trim()

        if (nouveauNom.isEmpty()) {
            etContact.error = "Le nom ne peut pas être vide"
            return
        }

        // Ajouter le contact
        contacts.add(nouveauNom)

        // Trier par ordre alphabétique
        contacts.sort()

        // Actualiser la ListView
        adapter.notifyDataSetChanged()

        // Réinitialiser la sélection
        positionSelectionnee = -1
        lvContacts.clearChoices()

        // Vider le champ
        etContact.text.clear()

        Toast.makeText(
            this,
            "Contact ajouté : $nouveauNom",
            Toast.LENGTH_SHORT
        ).show()
    }

    // EXERCICES 3 et 4 : Modifier, vérifier les doublons et trier
    private fun modifierContact() {

        // Vérifier la sélection
        if (positionSelectionnee == -1) {
            Toast.makeText(
                this,
                R.string.msg_aucune_selection,
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Récupérer le nouveau nom
        val nouveauNom = etContact.text.toString().trim()

        // Vérifier si le nom est vide
        if (nouveauNom.isEmpty()) {
            etContact.error = getString(R.string.msg_nom_vide)
            return
        }

        // Vérifier les doublons
        val existeDeja = contacts.withIndex().any { (index, nom) ->
            index != positionSelectionnee &&
                    nom.equals(nouveauNom, ignoreCase = true)
        }

        if (existeDeja) {
            etContact.error = "Ce contact existe déjà"
            return
        }

        // Modifier le contact
        contacts[positionSelectionnee] = nouveauNom

        // Trier la liste
        contacts.sort()

        // Actualiser la ListView
        adapter.notifyDataSetChanged()

        // Retrouver la nouvelle position après le tri
        positionSelectionnee = contacts.indexOf(nouveauNom)

        lvContacts.clearChoices()
        lvContacts.setItemChecked(positionSelectionnee, true)

        Toast.makeText(
            this,
            "Contact modifié : $nouveauNom",
            Toast.LENGTH_SHORT
        ).show()
    }
}
