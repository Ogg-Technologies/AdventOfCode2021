package prob2

import java.io.File

private data class State(val aim: Int, val depth: Int, val horizontalX: Int) {
    fun applyOperations(stateOperations: List<StateOperation>) =
        stateOperations.fold(this) { state, operation -> operation(state) }

    companion object {
        fun allZero(): State = State(0, 0, 0)
    }
}

private typealias StateOperation = (State) -> State
private typealias AmountBasedStateOperation = (amount: Int, state: State) -> State

private data class CommandFunction(val commandName: String, val amountBasedStateOperation: AmountBasedStateOperation)

private infix fun String.does(amountBasedStateOperation: AmountBasedStateOperation) =
    CommandFunction(this, amountBasedStateOperation)


fun main() {
    val lines = File("src/prob2Input.txt").readLines()

    val operations = parseOperations(lines, *TaskOperationFunctions.task2CommandFunctions)

    val startingState = State.allZero()
    val finalState = startingState.applyOperations(operations)

    print(finalState.horizontalX * finalState.depth)
}

private fun parseOperations(
    lines: List<String>,
    vararg commandFunctions: CommandFunction
): List<StateOperation> = lines.map {
    val amount = it.split(" ")[1].toInt()

    for (operationFunction in commandFunctions) {
        if (it.startsWith(operationFunction.commandName)) {
            return@map { state ->
                operationFunction.amountBasedStateOperation(amount, state)
            }
        }
    }

    throw IllegalArgumentException("Unknown operation")
}

private object TaskOperationFunctions {
    val task1CommandFunctions: Array<CommandFunction> = arrayOf(
        "forward" does { amount, state ->
            state.copy(horizontalX = state.horizontalX + amount)
        },
        "down" does { amount, state ->
            state.copy(depth = state.depth + amount)
        },
        "up" does { amount, state ->
            state.copy(depth = state.depth - amount)
        }
    )

    val task2CommandFunctions: Array<CommandFunction> = arrayOf(
        "forward" does { amount, state ->
            state.copy(
                horizontalX = state.horizontalX + amount,
                depth = state.depth + state.aim * amount
            )
        },
        "down" does { amount, state ->
            state.copy(aim = state.aim + amount)
        },
        "up" does { amount, state ->
            state.copy(aim = state.aim - amount)
        }
    )
}