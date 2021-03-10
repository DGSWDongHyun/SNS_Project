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
import com.project.sns.databinding.FragmentProfileBinding
import com.project.sns.ui.activities.write.ReadActivity
import com.project.sns.ui.adapters.WriteAdapter
import com.project.sns.ui.adapters.listener.onClickItemListener
import com.project.sns.ui.viewmodel.MainViewModel

open class ProfileFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private var mainViewModel : MainViewModel?= null
    var profileBinding : FragmentProfileBinding ?= null
    var key : String ?= ""
    var users : User ?= null
    private var writeAdapter: WriteAdapter? = null
    private var postDataList: ArrayList<PostData> ?= ArrayList()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        profileBinding = FragmentProfileBinding.inflate(layoutInflater)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        database = Firebase.database.reference

        return profileBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUserName()


        writeAdapter = WriteAdapter(requireContext(), object : onClickItemListener {
            override fun onClickItem(position: Int, postData: ArrayList<PostData>) {
                val intent = Intent(requireActivity(), ReadActivity::class.java)
                intent.putExtra("title", postData[position].title)
                intent.putExtra("content", postData[position].content)
                intent.putExtra("genre", postData[position].genre)
                intent.putExtra("key", postData[position].key)
                intent.putExtra("commentCount", postData[position].commentCount)
                intent.putExtra("image", postData[position].image_url)
                intent.putExtra("userName", postData[position].UserName)

                startActivity(intent)
                requireActivity().overridePendingTransition(R.anim.pull_anim, R.anim.invisible)
            }
        })

        profileBinding?.recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        profileBinding?.recyclerView?.adapter = writeAdapter

        profileBinding?.imageProfile?.setOnClickListener {
            selectAlbum()
        }
    }

    private fun selectAlbum() {
        mainViewModel?.fragmentViewProfile?.value = profileBinding
        mainViewModel?.key?.value = key!!
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        intent.type = "image/*"
        requireActivity().startActivityForResult(intent, FROM_ALBUM)
    }

    private fun findBoard(name : String){
        // read bbs db
        postDataList?.clear()

        database.child("board").child("path").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val dataObject = snapshot.getValue(PostData::class.java)
                if(name == dataObject?.UserName){
                    postDataList!!.add(0, PostData(dataObject!!.title, dataObject.content, dataObject.image_url,
                            dataObject.dateTime, dataObject.genre, dataObject.viewType, dataObject.UserName, dataObject.commentCount,
                            dataObject.commentList, dataObject.key))
                }

                writeAdapter!!.setData(postDataList)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                postDataList?.clear()
                val dataObject = snapshot.getValue(PostData::class.java)
                if(name == dataObject?.UserName){
                    postDataList!!.add(0, PostData(dataObject!!.title, dataObject.content, dataObject.image_url,
                            dataObject.dateTime, dataObject.genre, dataObject.viewType, dataObject.UserName, dataObject.commentCount,
                            dataObject.commentList, dataObject.key))
                }
                writeAdapter!!.setData(postDataList)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                postDataList?.clear()
                val dataObject = snapshot.getValue(PostData::class.java)
                if(name == dataObject?.UserName){
                    postDataList!!.add(0, PostData(dataObject!!.title, dataObject.content, dataObject.image_url,
                            dataObject.dateTime, dataObject.genre, dataObject.viewType, dataObject.UserName, dataObject.commentCount,
                            dataObject.commentList, dataObject.key))
                }
                writeAdapter!!.setData(postDataList)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Error", databaseError.message)
            }
        })

        //end
    }
    private fun getUserName() {
        val sharedPreference : SharedPreferences = requireContext().getSharedPreferences("Account", Context.MODE_PRIVATE)
        database.child("user").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    Log.d("s", sharedPreference.getString("Email", null).equals(user!!.userEmail).toString())
                    Log.d("s", "${sharedPreference.getString("Email", null)} : ${user!!.userEmail}")
                    if (sharedPreference.getString("Email", null).equals(user!!.userEmail)) {
                        profileBinding!!.nameText.text = user.userName
                        profileBinding!!.emailText?.text = user.userEmail
                        key = user.key
                        users = user
                        val storage : FirebaseStorage?= FirebaseStorage.getInstance()
                        val storageRef: StorageReference = storage!!.reference.child("${user.userProfile}")
                        GlideApp.with(requireActivity()).load(storageRef).centerCrop().into(profileBinding?.imageProfile!!)
                        mainViewModel?.userAccount?.value = user
                        findBoard(users?.userName!!)
                        break
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    companion object{
        const val FROM_ALBUM = 2
    }
}