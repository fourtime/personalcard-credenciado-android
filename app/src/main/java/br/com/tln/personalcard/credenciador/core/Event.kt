package br.com.tln.personalcard.credenciador.core

class Event<T>(val data: T, var handled: Boolean = false)