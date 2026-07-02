package com.example.data

import kotlinx.coroutines.flow.Flow

class GameRepository(private val gameDao: GameDao) {
    val allGames: Flow<List<Game>> = gameDao.getAllGames()

    suspend fun insert(game: Game): Long {
        return gameDao.insertGame(game)
    }

    suspend fun update(game: Game) {
        gameDao.updateGame(game)
    }

    suspend fun deleteById(id: Int) {
        gameDao.deleteGameById(id)
    }

    suspend fun incrementBoost(id: Int, timestamp: Long) {
        gameDao.incrementBoostCount(id, timestamp)
    }
}
