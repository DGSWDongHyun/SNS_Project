package com.project.sns.ui.fragments.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.project.sns.GlideApp
import com.project.sns.R
import com.project.sns.data.board.PostData
import com.project.sns.data.board.User
import com.project.sns.data.module.FirebaseDatabaseModule
import com.project.sns.data.module.SimpleIntentModule
import com.project.sns.databinding.FragmentProfileBinding
import com.project.sns.ui.activities.write.ReadActivity
import com.project.sns.ui.adapters.WriteAdapter
import com.project.sns.ui.adapters.listener.onClickItemListener
import com.project.sns.ui.viewmodel.MainViewModel
import kotlinx.coroutines.*

open class ProfileFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private var mainViewModel : MainViewModel?= null
    var profileBinding : FragmentProfileBinding ?= null
    var key : String ?= ""
    private var writeAdapter: WriteAdapter? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        profileBinding = FragmentProfileBinding.inflate(layoutInflater)

        return profileBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        database = Firebase.database.reference

        GlobalScope.launch {
            withContext(Dispatchers.Main)  {
                onWaitLoad()
            }
        }

        writeAdapter = WriteAdapter(requireContext(), object : onClickItemListener {
            override fun onClickItem(position: Int, postData: ArrayList<PostData>) {
                SimpleIntentModule.simplyActivity(requireActivity(), position, postData)
            }
        })

        profileBinding?.recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        profileBinding?.recyclerView?.adapter = writeAdapter

    }

    private suspend fun onWaitLoad() {
        val jobCompleted : Deferred<Boolean> = CoroutineScope(Dispatchers.IO).async {
            val job : Job = GlobalScope.async {
              getUserName()
            }

            job.join()

            true
        }

        jobCompleted.await()
    }

    private fun getUserName() {
        lateinit var array : ArrayList<PostData>
        val sharedPreference : SharedPreferences = requireContext().getSharedPreferences("Account", Context.MODE_PRIVATE)
        FirebaseDatabaseModule.database.child("user").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    Log.d("s", sharedPreference.getString("Email", null).equals(user!!.userEmail).toString())
                    Log.d("s", "${sharedPreference.getString("Email", null)} : ${user.userEmail}")
                    if (sharedPreference.getString("Email", null).equals(user.userEmail)) {

                        profileBinding?.nameText?.text = user.userName
                        profileBinding?.emailText?.text = user.userEmail

                        GlobalScope.launch {
                            withContext(Dispatchers.Main) {
                                val jobToCompleted : Deferred<Boolean> = CoroutineScope(Dispatchers.Main).async {
                                    val job  : Job = GlobalScope.async {
                                        Log.d("Result", "${FirebaseDatabaseModule.findBoard(user.userName!!, FirebaseDatabaseModule.NAME)}")
                                        array = FirebaseDatabaseModule.findBoard(user.userName!!, FirebaseDatabaseModule.NAME)
                                    }
                                    job.join()

                                    true
                                }
                                jobToCompleted.await()

                                if(jobToCompleted.isCompleted) {
                                    writeAdapter!!.setData(array)
                                }
                            }
                        }


//                        key = user.key.toString()

//                        val storage : FirebaseStorage = FirebaseStorage.getInstance()
//                        val storageRef: StorageReference = storage.reference.child("${user.userProfile}")
//                        GlideApp.with(requireContext()).load(storageRef).centerCrop().into(profileBinding!!.imageProfile)

//                        GlobalScope.launch {
//                            withContext(Dispatchers.Main) {
//                                val jobToCompleted : Deferred<Boolean> = CoroutineScope(Dispatchers.Main).async {
//                                    val job  : Job = GlobalScope.async {
//                                        mainViewModel?.userAccount?.value = user
//                                    }
//                                    job.join()
//
//                                    true
//                                }
//                                jobToCompleted.await()
//
//                                if(jobToCompleted.isCompleted) {
//                                    FirebaseDatabaseModule.findBoard(FirebaseDatabaseModule.users?.userName!!, writeAdapter!!, FirebaseDatabaseModule.NAME)
//                                }
//                            }
//                        }

                        Log.d("TAG", "${user}, ${mainViewModel?.userAccount?.value}, $mainViewModel")

                        break
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


//    private fun selectAlbum() {
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = MediaStore.Images.Media.CONTENT_TYPE
//        intent.type = "image/*"
//        requireActivity().startActivityForResult(intent, FROM_ALBUM)
//    }


    companion object{
        const val FROM_ALBUM = 2
    }
}