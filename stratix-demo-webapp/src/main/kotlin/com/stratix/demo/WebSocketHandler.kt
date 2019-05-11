package com.stratix.demo

import io.tekniq.web.sparklinMapper
import org.eclipse.jetty.websocket.api.RemoteEndpoint
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.*
import org.eclipse.jetty.websocket.api.extensions.Frame

@Suppress("unused")
abstract class WebSocketHandler {
    @OnWebSocketConnect
    open fun onWebSocketConnect(session: Session) {
    }

    @OnWebSocketMessage
    open fun onWebSocketMessage(session: Session, message: String) {
    }

    @OnWebSocketClose
    open fun onWebSocketClose(session: Session, statusCode: Int, reason: String) {
    }

    @OnWebSocketError
    open fun onWebSocketError(session: Session, error: Throwable) {
    }

    @OnWebSocketFrame
    open fun onWebSocketFrame(session: Session, frame: Frame) {
    }
}

fun <T> RemoteEndpoint.sendEventData(data: EventData<T>) =
        this.sendString(sparklinMapper.writeValueAsString(data))

data class EventData<T>(val event: String, val data: T)

