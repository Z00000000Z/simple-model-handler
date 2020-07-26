package ru.gnylka.smh.model.converter.cli

import java.util.concurrent.Callable
import java.util.concurrent.ForkJoinPool

class ConcurrentSessionsExecutor(sessions: List<SessionData>) : SessionsExecutor(sessions) {

    override fun execute() =
            ForkJoinPool.commonPool()
                    .invokeAll(sessions.map(::asCallable))
                    .mapIndexed { i, future -> sessions[i] to future.get() }
                    .toMap()

    private fun asCallable(sessionData: SessionData) =
            Callable { operateOn(sessionData) }

}
