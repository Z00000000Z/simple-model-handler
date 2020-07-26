package ru.gnylka.smh.model.converter.cli

abstract class SessionsExecutor(
        val sessions: List<SessionData>
) {

    abstract fun execute(): Map<SessionData, Exception?>

}
