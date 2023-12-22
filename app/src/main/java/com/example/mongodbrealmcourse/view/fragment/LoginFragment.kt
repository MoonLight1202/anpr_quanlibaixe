package com.example.mongodbrealmcourse.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mongodbrealmcourse.R
import com.example.mongodbrealmcourse.databinding.FragmentLoginBinding
import com.example.mongodbrealmcourse.viewmodel.callback.VoidCallback
import com.example.mongodbrealmcourse.viewmodel.utils.AnimationHelper
import com.example.mongodbrealmcourse.viewmodel.utils.PreferenceHelper
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import io.realm.Realm
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.RealmResultTask
import io.realm.mongodb.mongo.MongoClient
import io.realm.mongodb.mongo.MongoDatabase
import io.realm.mongodb.mongo.iterable.MongoCursor

import org.bson.Document
import java.util.Calendar

class LoginFragment : BaseFragment() {
    private var binding: FragmentLoginBinding? = null

    companion object {
        private const val TAG = "ServerAuthCodeActivity"
        private const val RC_GET_AUTH_CODE = 9003
    }

    private lateinit var mAuthCodeTextView: TextView

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null) {
            // Inflate the layout for this fragment
            binding = FragmentLoginBinding.inflate(inflater, container, false)
        } else {
            (binding!!.root.parent as? ViewGroup)?.removeView(binding!!.root)
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(preferenceHelper.remember_account == true){
            binding?.cbPolicyLogin?.isChecked = true
            binding?.itemEmailLogin?.setContent(preferenceHelper.current_account_email)
            binding?.itemPasswordLogin?.setContent(preferenceHelper.current_account_password)
        }
        binding?.cbPolicyLogin?.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) preferenceHelper.remember_account = true else preferenceHelper.remember_account = false
        }
        binding?.btnRegisterNow?.setOnClickListener {
            binding?.viewLogin?.visibility = View.GONE
            binding?.viewRegister?.visibility = View.VISIBLE
        }
        binding?.btnLoginNow?.setOnClickListener {
            binding?.viewLogin?.visibility = View.VISIBLE
            binding?.viewRegister?.visibility = View.GONE
        }

        Glide.with(this)
            .asGif()
            .load(R.drawable.logogiff) // Thay đổi thành đường dẫn hoặc resource của tệp GIF của bạn
            .into(binding?.ivLogin!!)

        Realm.init(this.requireContext())

        app = App(AppConfiguration.Builder(Appid).build())

        binding!!.itemPasswordLogin.getViewEditContent().setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding!!.itemPasswordLogin.hideWarning()
            }
        }
        binding!!.itemEmailLogin.getViewEditContent().setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding!!.itemEmailLogin.hideWarning()
            }
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (binding!!.itemEmailLogin.getContent() != "") {
                    binding!!.itemEmailLogin.hideWarning()
                }
                if (binding!!.itemPasswordLogin.getContent() != "") {
                    binding!!.itemPasswordLogin.hideWarning()
                }
            }
        }
        binding!!.itemEmailLogin.addTextChangedListener(textWatcher)
        binding!!.itemPasswordLogin.addTextChangedListener(textWatcher)

        binding!!.btnRegister.setOnClickListener {
            AnimationHelper.scaleAnimation(it, object : VoidCallback {
                override fun execute() {
                    if (binding!!.itemNameRegister.getContent() == "" || binding!!.itemNameRegister.getContent() == null) {
                        binding!!.itemNameRegister.showWarning(getString(R.string.incorrect_format))
                    }
                    if (binding!!.itemEmailRegister.getContent() == "" || binding!!.itemEmailRegister.getContent() == null) {
                        binding!!.itemEmailRegister.showWarning(getString(R.string.incorrect_format))
                    }
                    if (binding!!.itemPasswordRegister.getContent() == "" || binding!!.itemPasswordRegister.getContent() == null) {
                        binding!!.itemPasswordRegister.showWarning(getString(R.string.incorrect_format))
                    }
                    if (binding!!.itemPasswordRegister.getContent() != binding!!.itemPasswordSubmitRegister.getContent()) {
                        binding!!.itemPasswordSubmitRegister.showWarning(getString(R.string.password_confirmation_mismatch))
                    }
                    if (binding!!.itemEmailRegister.getContent() != "" && binding!!.itemPasswordRegister.getContent() != "" && binding!!.itemPasswordRegister.getContent() ==  binding!!.itemPasswordSubmitRegister.getContent() ) {
                        val user = app.currentUser()
                        Log.d("TAG_D", user?.accessToken.toString())
                        if (user != null) {
                            mongoClient = user.getMongoClient("mongodb-atlas")
                        }
                        mongoDatabase = mongoClient.getDatabase("number-plates-data")
                        val mongoCollection = mongoDatabase.getCollection("user")
                        val queryFilter = Document("email", binding!!.itemEmailRegister.getContent())
                        val findTask: RealmResultTask<MongoCursor<Document>> =
                            mongoCollection.find(queryFilter).iterator()

                        findTask.getAsync { task ->
                            if (task.isSuccess) {
                                val results: MongoCursor<Document> = task.get()
                                if (results.hasNext()) {
                                    binding!!.itemEmailRegister.showWarning(getString(R.string.account_already_exists))
                                } else {
                                    val document = Document()
                                    document.put("name", binding!!.itemNameRegister.getContent())
                                    document.put("email", binding!!.itemEmailRegister.getContent())
                                    document.put("password", binding!!.itemPasswordSubmitRegister.getContent())
                                    document.put("avatar", "")
                                    document.put("address", "")
                                    document.put("time_create", Calendar.getInstance().time.toString())

                                    val addObj = mongoCollection.insertOne(document)
                                    addObj.getAsync { task ->
                                        if (task.isSuccess) {
                                            Log.v("AddFunction", "Inserted Data")
                                            Toast.makeText(
                                                this@LoginFragment.requireContext(),
                                                getString(R.string.registration_success),
                                                Toast.LENGTH_SHORT,
                                            ).show()
                                            binding!!.itemEmailLogin.setContent(binding!!.itemEmailRegister.getContent())
                                            binding!!.viewRegister.visibility = View.GONE
                                            binding!!.viewLogin.visibility = View.VISIBLE
                                        } else {
                                            Log.v("AddFunction", "Error" + task.error.toString())
                                            Toast.makeText(
                                                this@LoginFragment.requireContext(),
                                                getString(R.string.error_occurred) + task.error.toString(),
                                                Toast.LENGTH_SHORT,
                                            ).show()
                                        }
                                    }
                                }
                            } else {
                                Log.v("Task Error", task.error.toString())
                            }
                        }
                    }
                }
            }, 0.98f)
        }
        var email = ""
        var password = ""
        binding!!.btnLogin.setOnClickListener {
            AnimationHelper.scaleAnimation(it, object : VoidCallback {
                override fun execute() {
                    if (binding!!.itemEmailLogin.getContent() != "") {
                        email = binding!!.itemEmailLogin.getContent()
                    } else binding!!.itemEmailLogin.showWarning(getString(R.string.incorrect_format))
                    if (binding!!.itemPasswordLogin.getContent() != "") {
                        password = binding!!.itemPasswordLogin.getContent()
                    } else binding!!.itemPasswordLogin.showWarning(getString(R.string.incorrect_format))

                    if (email != "" && password != "") {
                        val user = app.currentUser()
                        var dataEmail = ""
                        var dataPassword = ""

                        val queryFilter = Document("email", binding!!.itemEmailLogin.getContent())
                        if (user != null) {
                            mongoClient = user.getMongoClient("mongodb-atlas")
                        }
                        mongoDatabase = mongoClient.getDatabase("number-plates-data")
                        val mongoCollection = mongoDatabase.getCollection("user")

                        val findTask: RealmResultTask<MongoCursor<Document>> =
                            mongoCollection.find(queryFilter).iterator()

                        findTask.getAsync { task ->
                            if (task.isSuccess) {
                                val results: MongoCursor<Document> = task.get()
                                if (!results.hasNext()) {
                                    binding!!.itemEmailLogin.showWarning(getString(R.string.account_not_exists))
                                }
                                while (results.hasNext()) {
                                    val currentDoc: Document = results.next()
                                    var strings: String? = currentDoc["email"] as? String
                                    var stringsPass: String? = currentDoc["password"] as? String

                                    if (strings == null) {
                                        Log.v("FindTask", "Email has size 0")
                                    } else {
                                        for (i in 0 until strings.length) {
                                            if (dataEmail == null) {
                                                dataEmail = ""
                                                dataEmail = dataEmail + strings[i]
                                            } else {
                                                dataEmail = dataEmail + strings[i]
                                            }
                                        }
                                    }
                                    if (stringsPass == null) {
                                        Log.v("FindTask", "Password has size 0")
                                    } else {
                                        for (i in 0 until stringsPass.length) {
                                            if (dataPassword == null) {
                                                dataPassword = ""
                                                dataPassword = dataPassword + stringsPass[i]
                                            } else {
                                                dataPassword = dataPassword + stringsPass[i]
                                            }
                                        }
                                    }
                                    Log.d("TAG_F", dataEmail + dataPassword)
                                    if (dataEmail != binding!!.itemEmailLogin.getContent()) {
                                        binding!!.itemEmailLogin.showWarning(getString(R.string.incorrect_account))
                                    } else {
                                        binding!!.itemEmailLogin.hideWarning()
                                        if (dataPassword != binding!!.itemPasswordLogin.getContent()) {
                                            binding!!.itemPasswordLogin.showWarning(getString(R.string.incorrect_password))
                                        } else {
                                            binding!!.itemPasswordLogin.hideWarning()
                                            preferenceHelper.current_account_email =
                                                binding!!.itemEmailLogin.getContent()
                                            preferenceHelper.current_account_password =
                                                binding!!.itemPasswordLogin.getContent()
                                            findNavController().navigate(R.id.start_on_main_screen)
                                        }
                                    }
                                }
                            } else {
                                Log.v("Task Error", task.error.toString())
                            }
                        }
                    }
                }
            }, 0.98f)
        }
    }
}