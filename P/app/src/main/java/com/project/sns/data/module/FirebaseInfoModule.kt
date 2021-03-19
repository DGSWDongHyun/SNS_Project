package com.project.sns.data.module

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.project.sns.data.board.User
import com.project.sns.data.module.FirebaseDatabaseModule.Companion.database
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext

    object FirebaseInfoModule{
        private val database = FirebaseDatabase.getInstance().reference
        suspend fun getUserInfo(email : String) : User?{

            val arrayListUser : ArrayList<User> = arrayListOf()

            val jobComplete : Deferred<Boolean> = CoroutineScope(Dispatchers.Main).async{
                val waitJob : Job = GlobalScope.async {
                    database.child("user").addChildEventListener(object : ChildEventListener {
                        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                            val firebaseInfo = snapshot.getValue(User::class.java)
                            arrayListUser.add(firebaseInfo!!)
                        }
                        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                        }

                        override fun onChildRemoved(snapshot: DataSnapshot) {

                        }

                        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
                }
                waitJob.join()

                true
            }

            jobComplete.await()

            return checkArray(arrayListUser, email)
        }

        private fun checkArray(array : ArrayList<User>, email : String) : User {
           var data : User = User()
            for(index in array.indices) {
                if(email == array[index].userEmail) {
                    data = array[index]
                }
            }
            return data
        }
    }
