package com.apex.codeassesment.data

import com.apex.codeassesment.data.local.LocalDataSource
import com.apex.codeassesment.data.model.User
import com.apex.codeassesment.data.remote.RemoteDataSource
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever
import java.util.concurrent.atomic.AtomicReference

class UserRepositoryTest {
    private lateinit var userRepository: UserRepository
    private val localDataSource: LocalDataSource = mock()
    private val remoteDataSource: RemoteDataSource = mock()

    @Before
    fun setup() {
        userRepository = UserRepository(localDataSource, remoteDataSource)
    }

    @Test
    fun `getSavedUser returns user from local data source`() {
        val user = User()
        whenever(localDataSource.loadUser()).thenReturn(user)

        val result = userRepository.getSavedUser()

        verify(localDataSource).loadUser()
        assert(result == user)
    }

    @Test
    fun `getUser with forceUpdate=true updates local data source and returns user`() {
        val user = User()
        whenever(remoteDataSource.loadUsers()).thenReturn(listOf(user))

        val result = userRepository.getUser(forceUpdate = true)

        verify(remoteDataSource).loadUsers()
        verify(localDataSource).saveUser(user)
        assert(result == user)
    }

    @Test
    fun `getUser with forceUpdate=false returns saved user`() {
        val user = User()
        userRepository.saveUserToAtomicReference(user)

        val result = userRepository.getUser(forceUpdate = false)

        assert(result == user)
    }

    @Test
    fun `getUsers returns list of users from remote data source`() {
        val userList = listOf(User(), User())
        whenever(remoteDataSource.loadUsers()).thenReturn(userList)

        val result = userRepository.getUsers()

        verify(remoteDataSource).loadUsers()
        assert(result == userList)
    }

    private fun UserRepository.saveUserToAtomicReference(user: User) {
        val field = UserRepository::class.java.getDeclaredField("savedUser")
        field.isAccessible = true
        field.set(this, AtomicReference(user))
    }
}