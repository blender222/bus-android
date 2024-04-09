package com.ashtar.bus.data

import com.ashtar.bus.common.FakeDataProvider
import com.ashtar.bus.data.dao.RouteDao
import com.ashtar.bus.data.database.AppDatabase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
    fun searchRoute_searchByRed_returnCorrectList() = runTest {
        // Arrange
        val query = "紅"
        Mockito.`when`(routeDao.getList("$query%"))
            .thenReturn(FakeDataProvider.RedRawRouteList)

        // Act
        val actual = routeRepository.searchRoute(query)

        // Assert
        assertEquals(FakeDataProvider.RedExpectedRouteList, actual)
    }

    @Test
    fun searchRoute_searchByNeihu_returnCorrectList() = runTest {
        // Arrange
        val query = "內科"
        Mockito.`when`(routeDao.getList("%$query%"))
            .thenReturn(FakeDataProvider.NeihuRawRouteList)

        // Act
        val actual = routeRepository.searchRoute(query)

        // Assert
        assertEquals(FakeDataProvider.NeihuExpectedRouteList, actual)
    }

    @Test
    fun searchRoute_searchByCity_returnCorrectList() = runTest {
        // Arrange
        val query = "市民"
        Mockito.`when`(routeDao.getList("%$query%"))
            .thenReturn(FakeDataProvider.CityRawRouteList)

        // Act
        val actual = routeRepository.searchRoute(query)

        // Assert
        assertEquals(FakeDataProvider.CityExpectedRouteList, actual)
    }

    @Test
    fun searchRoute_searchByNumber_returnCorrectList() = runTest {
        // Arrange
        val query = "20"
        Mockito.`when`(routeDao.getList("%$query%"))
            .thenReturn(FakeDataProvider.NumberRawRouteList)

        // Act
        val actual = routeRepository.searchRoute(query)

        // Assert
        assertEquals(FakeDataProvider.NumberExpectedRouteList, actual)
    }
}