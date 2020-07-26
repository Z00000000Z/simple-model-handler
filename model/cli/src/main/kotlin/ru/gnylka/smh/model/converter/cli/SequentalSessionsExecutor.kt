package ru.gnylka.smh.model.converter.cli

class SequentalSessionsExecutor(sessions: List<SessionData>) : SessionsExecutor(sessions) {

    override fun execute() =
            sessions.map { it to operateOn(it) }.toMap()

}
