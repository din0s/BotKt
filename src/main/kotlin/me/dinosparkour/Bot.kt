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
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Game
import javax.security.auth.login.LoginException
import kotlin.system.exitProcess

private val REQUIRED_ARGS = 1

fun main(args: Array<String>) {
    if (args.size != REQUIRED_ARGS) {
        println("""
        |Invalid argument amount!
        |Expected: [$REQUIRED_ARGS] | Received: [${args.size}]
        """.trimMargin())
        exitProcess(ExitStatus.INCORRECT_LAUNCH_ARGS.code)
    }

    Registry().loadCommands()
    connect(args[0])
}

fun connect(token: String) {
    try {
        JDABuilder(AccountType.BOT)
                .addListener(EventManager())
                .setBulkDeleteSplittingEnabled(false)
                .setGame(Game.of("Thrones")) // pun xd
                .setToken(token)
                .buildBlocking()
    } catch (ex: LoginException) {
        ex.printStackTrace()
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
    INCORRECT_LAUNCH_ARGS(22)
}
