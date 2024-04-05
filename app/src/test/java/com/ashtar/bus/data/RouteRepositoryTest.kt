package com.ashtar.bus.data

import com.ashtar.bus.common.DataProvider
import com.ashtar.bus.data.dao.RouteDao
import com.ashtar.bus.data.database.AppDatabase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class RouteRepositoryTest {
    private lateinit var database: AppDatabase
    private lateinit var routeDao: RouteDao
    private lateinit var routeRepository: RouteRepository

    @Before
    fun init() {
        database = Mockito.mock(AppDatabase::class.java)
        routeDao = Mockito.mock(RouteDao::class.java)
        routeRepository = RouteRepositoryImpl(database, routeDao)
    }

    @Test
    fun searchRoute_searchByRed_returnCorrectList() {
        runBlocking {
            // Arrange
            val query = "紅"
            Mockito.`when`(routeDao.getList("$query%"))
                .thenReturn(DataProvider.RedRawRouteList)

            // Act
            val actual = routeRepository.searchRoute(query)

            // Assert
            assertEquals(DataProvider.RedExpectedRouteList, actual)
        }
    }

    @Test
    fun searchRoute_searchByNeihu_returnCorrectList() {
        runBlocking {
            // Arrange
            val query = "內科"
            Mockito.`when`(routeDao.getList("%$query%"))
                .thenReturn(DataProvider.NeihuRawRouteList)

            // Act
            val actual = routeRepository.searchRoute(query)

            // Assert
            assertEquals(DataProvider.NeihuExpectedRouteList, actual)
        }
    }

    @Test
    fun searchRoute_searchByCity_returnCorrectList() {
        runBlocking {
            // Arrange
            val query = "市民"
            Mockito.`when`(routeDao.getList("%$query%"))
                .thenReturn(DataProvider.CityRawRouteList)

            // Act
            val actual = routeRepository.searchRoute(query)

            // Assert
            assertEquals(DataProvider.CityExpectedRouteList, actual)
        }
    }

    @Test
    fun searchRoute_searchByNumber_returnCorrectList() {
        runBlocking {
            // Arrange
            val query = "20"
            Mockito.`when`(routeDao.getList("%$query%"))
                .thenReturn(DataProvider.NumberRawRouteList)

            // Act
            val actual = routeRepository.searchRoute(query)

            // Assert
            assertEquals(DataProvider.NumberExpectedRouteList, actual)
        }
    }
}