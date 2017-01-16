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

package me.dinosparkour.managers

import me.dinosparkour.DatabaseAuth
import me.dinosparkour.DatabaseColumn
import me.dinosparkour.ExitStatus
import org.json.JSONObject
import java.io.File
import kotlin.system.exitProcess

class ConfigManager(args: Array<String>) {

    init {
        createFake(args[0], args[1], args[2], args[3], args[4], args[5])
    }

    fun get(): Map<Any, String> = config!!

    companion object {
        private var config: Map<Any, String>? = null
        private val configFile = File("config.json")
        val REQUIRED_ARGS = 6

        private fun createFake(token: String, dbHost: String, dbName: String, dbUser: String, dbPassword: String, prefix: String) {
            config = mapOf(Pair("token", token),
                    Pair(DatabaseAuth.HOST.key, dbHost),
                    Pair(DatabaseAuth.NAME.key, dbName),
                    Pair(DatabaseAuth.USER.key, dbUser),
                    Pair(DatabaseAuth.PASSWORD.key, dbPassword),
                    Pair(DatabaseColumn.PREFIX, prefix))
        }

        private fun create() {
            configFile.createNewFile() // Make sure a file exists.
            val jsonToWrite = JSONObject().put("token", "https://discordapp.com/developers/applications/me")
            DatabaseAuth.values().forEach { jsonToWrite.put(it.key, it.defaultValue) }
            DatabaseColumn.values().forEach { jsonToWrite.put(it.toString(), it.defaultValue) }

            configFile.writeText(jsonToWrite.toString(4))
        }

        fun read(): Map<Any, String> {
            if (!configFile.exists()) {
                create()
                println("Generated a new config file! Please fill in your credentials.")
                exitProcess(ExitStatus.NEW_CONFIG.code)
            }

            val json = JSONObject(configFile.readLines().joinToString("\n"))
            val _config = mutableMapOf<Any, String>()

            if (DatabaseAuth.keys().any { !json.has(it) }
                    || DatabaseColumn.values().any { !json.has(it.toString()) }
                    || !json.has("token")) {
                System.err.println("Config file missing an argument. Regenerating!")
                create()
                exitProcess(ExitStatus.INSUFFICIENT_ARGS.code)
            }
            DatabaseAuth.keys().forEach { _config.put(it, json[it].toString()) }
            DatabaseColumn.values().forEach { _config.put(it.toString(), json[it.toString()].toString()) }
            _config.put("token", json["token"].toString())
            config = _config
            return _config
        }

        fun getDefaultPrefix(): String {
            return config!![DatabaseColumn.PREFIX.toString()]!!
        }
    }
}
