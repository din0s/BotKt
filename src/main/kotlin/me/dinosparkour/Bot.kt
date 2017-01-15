/*
 * (C) Copyright 2017 Dinos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.dinosparkour

import me.dinosparkour.commands.Registry
import me.dinosparkour.managers.DatabaseManager
import me.dinosparkour.managers.EventManager
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Game
import javax.security.auth.login.LoginException
import kotlin.system.exitProcess

// TODO: alternative config system
private val REQUIRED_ARGS = 5

fun main(args: Array<String>) {
    if (args.size < REQUIRED_ARGS) {
        println("""
        |Invalid argument amount!
        |Expected: [$REQUIRED_ARGS] | Received: [${args.size}]
        """.trimMargin())
        exitProcess(ExitStatus.INSUFFICIENT_LAUNCH_ARGS.code)
    }

    DatabaseManager.initialize(args[1], args[2], args[3], args[4])
    Registry().loadCommands()
    connect(args[0])
}

private fun connect(token: String) {
    try {
        JDABuilder(AccountType.BOT)
                .addListener(EventManager())
                .setBulkDeleteSplittingEnabled(false)
                .setGame(Game.of("Thrones")) // pun xd
                .setToken(token)
                .buildBlocking()
    } catch (ex: LoginException) {
        System.err.println(ex.message)
        exitProcess(ExitStatus.INVALID_TOKEN.code)
    }
}

enum class ExitStatus(val code: Int) {
    // Non error
    UPDATE(10),
    SHUTDOWN(11),
    RESTART(12),
    CONFIG_GENERATION(13),

    // Error
    INVALID_TOKEN(20),
    CONFIG_MISSING(21),
    INSUFFICIENT_LAUNCH_ARGS(22),

    // SQL
    SQL_ACCESS_DENIED(30),
    SQL_INVALID_PASSWORD(31),
    SQL_UNKNOWN_HOST(32),
    SQL_UNKNOWN_DATABASE(33)
}
