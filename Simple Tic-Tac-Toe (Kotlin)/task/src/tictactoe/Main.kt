package tictactoe

import kotlin.math.abs

fun main() {
    val game = Game()
    game.start()
}

class Game {
    private val grid: Grid = Grid()
    private val playerOne = 'X'
    private val playerTwo = 'O'
    private var currentPlayer = playerOne

    init {
        println(grid)
    }

    fun start() {
        do {
            val (row, column) = safeGetUserInput()
            playRound(row, column)
        } while (isOn())
        println(grid.state.message)
    }

    private fun isOn() = !grid.state.isTerminal

    private fun playRound(row: Int, column: Int) {
        grid.update(row, column, currentPlayer)
        println(grid)
        currentPlayer = if (currentPlayer == playerOne) playerTwo else playerOne
    }

    private fun safeGetUserInput(): Pair<Int, Int> {
        try {
            val (rowRaw, columnRaw) = askUserMove()
            validateUserMove(rowRaw, columnRaw)
            return Pair(rowRaw.toInt() - 1, columnRaw.toInt() - 1)
        } catch (exception: RuntimeException) {
            println(exception.message)
            return safeGetUserInput()
        }
    }

    private fun askUserMove(): Pair<String, String> {
        val userMove = readln()
        val (row, column) = userMove.split(" ")
        return Pair(row, column)
    }

    private fun validateUserMove(rowRaw: String, columnRaw: String) {
        validateCoordinateIsNumeric(rowRaw)
        validateCoordinateIsNumeric(columnRaw)

        val row = rowRaw.toInt()
        val column = columnRaw.toInt()
        validateCoordinatesAreInRange(row, column)

        val actualRow = row - 1
        val actualColumn = column - 1
        validateCellIsEmpty(actualRow, actualColumn)
    }

    private fun validateCellIsEmpty(row: Int, column: Int) {
        if (grid.isEmptyAt(row, column)) return
        throw RuntimeException("This cell is occupied! Choose another one!")
    }

    private fun validateCoordinatesAreInRange(row: Int, column: Int) {
        if (row in 1..3 && column in 1..3) return
        throw RuntimeException("Coordinates should be from 1 to 3!")
    }

    private fun validateCoordinateIsNumeric(coordinateRaw: String) {
        try {
            coordinateRaw.toInt()
        } catch (exception: RuntimeException) {
            throw RuntimeException("You should enter numbers!")
        }
    }
}

class Grid {
    private val grid: List<MutableList<Char>> = List(3) { MutableList(3) { ' ' } }
    val state: State
        get() = analyzeState()

    enum class State(
        val message: String,
        val isTerminal: Boolean
    ) {
        GAME_NOT_FINISHED("Game not finished", isTerminal = false),
        IMPOSSIBLE("Impossible", isTerminal = true),
        X_WINS("X wins", isTerminal = true),
        O_WINS("O wins", isTerminal = true),
        DRAW("Draw", isTerminal = true)
    }

    override fun toString(): String {
        return buildString {
            appendLine("---------")
            grid.forEach {
                appendLine("| ${it.joinToString(" ")} |")
            }
            appendLine("---------")
        }
    }

    fun isEmptyAt(row: Int, column: Int) = grid[row][column] == ' '

    private fun analyzeState(): State {
        val hasThreeXInRow = hasThreeXInRow()
        val hasThreeOInRow = hasThreeOInRow()
        return if (hasThreeXInRow && hasThreeOInRow || calculateDifference() >= 2) State.IMPOSSIBLE
        else if (hasThreeXInRow) State.X_WINS
        else if (hasThreeOInRow) State.O_WINS
        else if (countEmpty() == 0) State.DRAW
        else State.GAME_NOT_FINISHED
    }

    private fun calculateDifference() = abs(countX() - countO())

    private fun countEmpty() = count(' ')

    private fun countX() = count('X')

    private fun countO() = count('O')

    private fun count(char: Char) = grid.flatten().count { it == char }

    private fun hasThreeXInRow() = hasThreeInRow('X')

    private fun hasThreeOInRow() = hasThreeInRow('O')

    private fun hasThreeInRow(char: Char) =
        hasThreeInHorizontalRow(char) || hasThreeInVerticalRow(char) || hasThreeInDiagonal(char)

    private fun hasThreeInHorizontalRow(char: Char) =
        grid.any { row -> row.all { it == char } }

    private fun hasThreeInVerticalRow(char: Char) =
        (0..2).any { i -> grid.map { row -> row[i] }.all { it == char } }

    private fun hasThreeInDiagonal(char: Char) =
        hasThreeInMainDiagonal(char) || hasThreeInAntiDiagonal(char)

    private fun hasThreeInMainDiagonal(char: Char) =
        (0..2).map { grid[it][it] }.all { it == char }

    private fun hasThreeInAntiDiagonal(char: Char) =
        (0..2).map { grid[it][2 - it] }.all { it == char }

    fun update(row: Int, column: Int, char: Char) {
        grid[row][column] = char
    }
}