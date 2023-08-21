package com.apex.codeassesment.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.apex.codeassesment.data.UserRepository
import com.apex.codeassesment.data.model.User
import com.apex.codeassesment.databinding.ActivityMainBinding
import com.apex.codeassesment.di.MainComponent
import com.apex.codeassesment.ui.details.DetailsActivity
import javax.inject.Inject

// TODO (5 points): Move calls to repository to Presenter or ViewModel.
// TODO (5 points): Use combination of sealed/Dataclasses for exposing the data required by the view from viewModel .
// TODO (3 points): Add tests for viewmodel or presenter.
// TODO (1 point): Add content description to images
// TODO (3 points): Add tests
// TODO (Optional Bonus 10 points): Make a copy of this activity with different name and convert the current layout it is using in
//  Jetpack Compose.
class MainActivity : AppCompatActivity() {

  // TODO (2 points): Convert to view binding   (DONE)

  lateinit var bi : ActivityMainBinding

  @Inject lateinit var userRepository: UserRepository

  private var randomUser: User = User()
    set(value) {
      // TODO (1 point): Use Glide to load images after getting the data from endpoints mentioned in RemoteDataSource

//      Glide.with(this).load(userRepository.getSavedUser().picture!!.thumbnail).into(bi.mainImage)

      bi.mainName!!.text = value.name!!.first
      bi.mainEmail!!.text = value.email
      field = value
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    bi = ActivityMainBinding.inflate(layoutInflater)
    setContentView(bi.root)
    sharedContext = this

    (applicationContext as MainComponent.Injector).mainComponent.inject(this)

//    val arrayAdapter = ArrayAdapter<User>(this, android.R.layout.simple_list_item_1)
      val customAdapter = CustomAdapter()

//    bi.mainUserList?.setOnItemClickListener { parent, _, position, _ -> navigateDetails(parent.getItemAtPosition(position) as User) }

    randomUser = userRepository.getSavedUser()

    bi.mainSeeDetailsButton!!.setOnClickListener { navigateDetails(randomUser) }

    bi.mainRefreshButton!!.setOnClickListener { randomUser = userRepository.getUser(true) }

    bi.mainUserListButton!!.setOnClickListener {
      val users = userRepository.getUsers()
      customAdapter.updateData(users)
      bi.mainUserList!!.adapter = customAdapter
      customAdapter.setOnItemClickListener { position ->
        navigateDetails(users[position])
      }
    }
  }

  // TODO (2 points): Convert to extenstion function.
  fun Context.navigateDetails(user: User) {
    val putExtra = Intent(this, DetailsActivity::class.java).putExtra("saved-user-key", user)
    startActivity(putExtra)
  }

  companion object {
    var sharedContext: Context? = null
  }
}
