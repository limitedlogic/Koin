package dev.apercorn.koin.ui.components.modal

import androidx.compose.runtime.mutableStateListOf

class SheetNavController(initialRoute: Any) {
	private val _stack = mutableStateListOf(initialRoute)

	val current: Any get() = _stack.last()
	val canPop: Boolean get() = _stack.size > 1

	fun push(route: Any) {
		_stack.add(route)
	}

	fun pop(): Boolean {
		if (_stack.size <= 1) return false
		_stack.removeLast()
		return true
	}

	fun onDismissRequest(onFullDismiss: () -> Unit) {
		if (!pop()) onFullDismiss()
	}
}