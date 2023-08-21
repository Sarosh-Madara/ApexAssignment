package com.apex.codeassesment.data.local

import com.apex.codeassesment.data.model.Name
import com.apex.codeassesment.data.model.User
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class LocalDataSourceTest {

    @Mock
    private lateinit var preferencesManager: PreferencesManager

    @Mock
    private lateinit var moshi: Moshi

    @Mock
    private lateinit var jsonAdapter: JsonAdapter<User>

    private lateinit var localDataSource: LocalDataSource

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        localDataSource = LocalDataSource(preferencesManager)
        `when`(moshi.adapter(User::class.java)).thenReturn(jsonAdapter)
    }

    @Test
    fun loadUser_whenSerializedUserIsNull_returnsRandomUser() {
        `when`(preferencesManager.loadUser()).thenReturn(null)

        val result = localDataSource.loadUser()

        verify(preferencesManager).loadUser()
        verify(jsonAdapter).fromJson(anyString())
        assert(result != null)
    }

    @Test
    fun loadUser_whenSerializedUserIsNotNull_returnsDeserializedUser() {
        val serializedUser = "{\"name\":\"John\",\"email\":\"john@example.com\"}"
        val user = User(gender= "Male", name = Name(first = "Alice", last = "Burger", title = "Alice"))
        `when`(preferencesManager.loadUser()).thenReturn(serializedUser)
        `when`(jsonAdapter.fromJson(serializedUser)).thenReturn(user)

        val result = localDataSource.loadUser()

        verify(preferencesManager).loadUser()
        verify(jsonAdapter).fromJson(serializedUser)
        assert(result == user)
    }

    @Test
    fun saveUser_savesSerializedUser() {
        val user = User(gender = "Male",name = Name(first = "Alice", last = "Burger", title = "Alice"))
        val serializedUser = "{\"name\":\"Alice\",\"email\":\"alice@example.com\"}"
        `when`(jsonAdapter.toJson(user)).thenReturn(serializedUser)

        localDataSource.saveUser(user)

        verify(jsonAdapter).toJson(user)
        verify(preferencesManager).saveUser(serializedUser)
    }
}