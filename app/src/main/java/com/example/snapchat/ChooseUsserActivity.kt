package com.example.snapchat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_choose_usser.*

class ChooseUsserActivity : AppCompatActivity() {

    var chooseUserListView : ListView? = null
    var emails : ArrayList<String> = ArrayList()
    var keys : ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_usser)

        chooseUserListView = findViewById(R.id.chooseUserListView)

        val adapter = ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1,emails)

        chooseUserListView?.adapter = adapter

        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)

        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, emails)
        autoCompleteTextView.setAdapter(arrayAdapter)
        autoCompleteTextView.threshold = 1

        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(object :
            ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val email = p0.child("email").value as String
                emails.add(email)
                p0.key?.let { keys.add(it) }
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(p0: DataSnapshot) {}

        })

        chooseUserListView?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val snapMap : Map<String,String> = mapOf("from" to FirebaseAuth.getInstance().currentUser!!.email!! ,
                                                    "imageName" to intent.getStringExtra("imageName") ,
                                                    "imageURL" to intent.getStringExtra("imageURL") ,
                                                    "message" to intent.getStringExtra("message"))

            FirebaseDatabase.getInstance().getReference().child("users").child(keys.get(position)).child("snaps").push().setValue(snapMap)

            val intent = Intent(this,SnapsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

    }

    fun sendClicked(view : View) {
        val snapMap : Map<String,String> = mapOf("from" to FirebaseAuth.getInstance().currentUser!!.email!! ,
            "imageName" to intent.getStringExtra("imageName") ,
            "imageURL" to intent.getStringExtra("imageURL") ,
            "message" to intent.getStringExtra("message"))

        FirebaseDatabase.getInstance().getReference().child("users").child(keys.get(emails.indexOf(autoCompleteTextView.text.toString()))).child("snaps").push().setValue(snapMap)
        val intent = Intent(this,SnapsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

}
