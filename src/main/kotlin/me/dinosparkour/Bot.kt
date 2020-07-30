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
import me.dinosparkour.managers.ConfigManager
import me.dinosparkour.managers.DatabaseManager
import me.dinosparkour.managers.EventManager
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Game
import javax.security.auth.login.LoginException
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    var config: Map<Any, String>? = null

    if (args.size < ConfigManager.REQUIRED_ARGS) {
        if (System.getProperty("config").toBoolean()) {
            // Detected config property. Reading values from config.json
            config = ConfigManager.read()
        } else {
            println("""
            |Invalid argument amount!
            |Expected: [${ConfigManager.REQUIRED_ARGS}] | Received: [${args.size}]
            """.trimMargin())
            exitProcess(ExitStatus.INSUFFICIENT_ARGS.code)
        }
    }
    config = config ?: ConfigManager(args).get()

    DatabaseManager.initialize(config.filter { it.key in DatabaseAuth.keys() })
    Registry().loadCommands()
    connect(config["token"]!!)
}

private fun connect(token: String) {
    try {
        JDABuilder().createDefault(token)
                .addListener(EventManager())
                .setBulkDeleteSplittingEnabled(false)
                .setGame(Game.of("Thrones")) // pun xd
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
    NEW_CONFIG(13),

    // Error
    INVALID_TOKEN(20),
    CONFIG_MISSING(21),
    INSUFFICIENT_ARGS(22),

    // SQL
    SQL_ACCESS_DENIED(30),
    SQL_INVALID_PASSWORD(31),
    SQL_UNKNOWN_HOST(32),
    SQL_UNKNOWN_DATABASE(33)
}
