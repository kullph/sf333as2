package com.example.tictactoe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class GameViewModel : ViewModel() {
    var state by mutableStateOf(GameState())
    private var playerStarts = true

    val boardItems: MutableMap<Int, BoardCellValue> = mutableMapOf(
        1 to BoardCellValue.NONE,
        2 to BoardCellValue.NONE,
        3 to BoardCellValue.NONE,
        4 to BoardCellValue.NONE,
        5 to BoardCellValue.NONE,
        6 to BoardCellValue.NONE,
        7 to BoardCellValue.NONE,
        8 to BoardCellValue.NONE,
        9 to BoardCellValue.NONE,
    )

    private fun computerPlay() {
        // ลองวางชิ้นหมายในช่องที่ชนะได้
        for (i in 1..9) {
            if (boardItems[i] == BoardCellValue.NONE) {
                boardItems[i] = BoardCellValue.CROSS
                if (checkForVictory(BoardCellValue.CROSS) and !checkForVictory(BoardCellValue.CIRCLE)) {
                    state = state.copy(
                        hintText = "Computer Won",
                        playerCrossCount = state.playerCrossCount + 1,
                        currentTurn = BoardCellValue.NONE,
                        hasWon = true
                    )
                    return
                }
                boardItems[i] = BoardCellValue.NONE
            }
        }

        // ลองบล็อกช่องที่ผู้เล่น O สามารถชนะได้
        for (i in 1..9) {
            if (boardItems[i] == BoardCellValue.NONE) {
                boardItems[i] = BoardCellValue.CIRCLE
                if (checkForVictory(BoardCellValue.CIRCLE)) {
                    boardItems[i] = BoardCellValue.CROSS
                    state = state.copy(
                        currentTurn = BoardCellValue.CIRCLE
                    )
                    return
                }
                boardItems[i] = BoardCellValue.NONE
            }
        }

        // วางในช่องกลางหากว่าง
        if (boardItems[5] == BoardCellValue.NONE) {
            boardItems[5] = BoardCellValue.CROSS

            state = state.copy(
                hintText = "Player turn",
                currentTurn = BoardCellValue.CIRCLE
            )
        } else {
            // สุ่มวางในช่องที่ว่าง
            val emptyCells = boardItems.filterValues { it == BoardCellValue.NONE }.keys.toList()
            if (emptyCells.isNotEmpty()) {
                val randomCell = emptyCells[Random.nextInt(emptyCells.size)]
                boardItems[randomCell] = BoardCellValue.CROSS

                state = state.copy(
                    hintText = "Player turn",
                    currentTurn = BoardCellValue.CIRCLE
                )
            }
        }
    }

    fun onAction(action: UserAction) {
        when (action) {
            is UserAction.BoardTapped -> {
                addValueToBoard(action.cellNo)
                computerPlay()
            }

            UserAction.PlayAgainButtonClicked -> {
                gameReset()
                playerStarts = !playerStarts
                if (!playerStarts) {
                    computerPlay()
                }
            }
        }
    }

    private fun gameReset() {
        boardItems.forEach { (i, _) ->
            boardItems[i] = BoardCellValue.NONE
        }
        state = state.copy(
            hintText = "Player turn",
            currentTurn = BoardCellValue.CIRCLE,
            victoryType = VictoryType.NONE,
            hasWon = false
        )
    }

    private fun addValueToBoard(cellNo: Int) {
        if (boardItems[cellNo] != BoardCellValue.NONE) {
            return
        }
        if (state.currentTurn == BoardCellValue.CIRCLE) {
            boardItems[cellNo] = BoardCellValue.CIRCLE
            state = if (checkForVictory(BoardCellValue.CIRCLE)) {
                state.copy(
                    hintText = "Player Won",
                    playerCircleCount = state.playerCircleCount + 1,
                    currentTurn = BoardCellValue.NONE,
                    hasWon = true
                )

            } else if (hasBoardFull()) {
                state.copy(
                    hintText = "Game Draw",
                    drawCount = state.drawCount + 1
                )
            } else {
                state.copy(
                    currentTurn = BoardCellValue.CROSS
                )
            }
        } else if (state.currentTurn == BoardCellValue.CROSS) {
            boardItems[cellNo] = BoardCellValue.CROSS
            state = if (checkForVictory(BoardCellValue.CROSS)) {
                state.copy(
                    hintText = "Computer Won",
                    playerCrossCount = state.playerCrossCount + 1,
                    currentTurn = BoardCellValue.NONE,
                    hasWon = true
                )
            } else if (hasBoardFull()) {
                state.copy(
                    hintText = "Game Draw",
                    drawCount = state.drawCount + 1
                )
            } else {
                state.copy(
                    hintText = "Player turn",
                    currentTurn = BoardCellValue.CIRCLE
                )

            }
        }
    }

    private fun checkForVictory(boardValue: BoardCellValue): Boolean {
        when {
            boardItems[1] == boardValue && boardItems[2] == boardValue && boardItems[3] == boardValue -> {
                state = state.copy(victoryType = VictoryType.HORIZONTAL1)
                return true
            }

            boardItems[4] == boardValue && boardItems[5] == boardValue && boardItems[6] == boardValue -> {
                state = state.copy(victoryType = VictoryType.HORIZONTAL2)
                return true
            }

            boardItems[7] == boardValue && boardItems[8] == boardValue && boardItems[9] == boardValue -> {
                state = state.copy(victoryType = VictoryType.HORIZONTAL3)
                return true
            }

            boardItems[1] == boardValue && boardItems[4] == boardValue && boardItems[7] == boardValue -> {
                state = state.copy(victoryType = VictoryType.VERTICAL1)
                return true
            }

            boardItems[2] == boardValue && boardItems[5] == boardValue && boardItems[8] == boardValue -> {
                state = state.copy(victoryType = VictoryType.VERTICAL2)
                return true
            }

            boardItems[3] == boardValue && boardItems[6] == boardValue && boardItems[9] == boardValue -> {
                state = state.copy(victoryType = VictoryType.VERTICAL3)
                return true
            }

            boardItems[1] == boardValue && boardItems[5] == boardValue && boardItems[9] == boardValue -> {
                state = state.copy(victoryType = VictoryType.DIAGONAL1)
                return true
            }

            boardItems[3] == boardValue && boardItems[5] == boardValue && boardItems[7] == boardValue -> {
                state = state.copy(victoryType = VictoryType.DIAGONAL2)
                return true
            }

            else -> return false
        }
    }

    private fun hasBoardFull(): Boolean {
        return !boardItems.containsValue(BoardCellValue.NONE)
    }

}