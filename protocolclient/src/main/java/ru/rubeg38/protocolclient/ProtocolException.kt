package ru.rubeg38.protocolclient

abstract class ProtocolException : Exception {

    constructor(message: String) : super(message)

    constructor(innerException: Exception) : super(innerException)

}

class TimeoutException(message: String) : ProtocolException(message)

class ServerException(message: String) : ProtocolException(message)
