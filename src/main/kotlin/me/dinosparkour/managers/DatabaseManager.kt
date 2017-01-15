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

import me.dinosparkour.DataType
import me.dinosparkour.ExitStatus
import me.dinosparkour.GuildData
import net.dv8tion.jda.core.entities.Guild
import org.mariadb.jdbc.internal.util.dao.QueryException
import java.sql.*
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

class DatabaseManager(guild: Guild) {

    private val id = guild.id.toLong()

    private fun Any?.addQuotes(): Any? = if (this is String) "'$this'" else this

    private fun insert(tableWithTypes: String, vararg values: Any?): Boolean {
        val v = values.map { it.addQuotes() }.joinToString()
        val statement = "INSERT INTO $tableWithTypes VALUES ($v)"
        return database.prepareStatement(statement).execute()
    }

    private fun update(table: String, key: Any, value: Any?): Boolean {
        val statement = "UPDATE $table SET $key = '$value' WHERE id = $id"
        return database.prepareStatement(statement).execute()
    }

    private fun delete(table: String): Boolean {
        val statement = "DELETE FROM $table WHERE id = $id"
        return database.prepareStatement(statement).execute()
    }

    private fun deleteRow(table: String): Boolean {
        guildData.remove(id)
        return delete(table)
    }

    fun deleteData(table: String, type: DataType) {
        when (type) {
            DataType.PREFIX -> getData()?.prefix = null
        }

        if (getData()?.isNull() ?: false) {
            deleteRow(table)
        }
    }

    fun saveData(table: String, type: DataType, value: Any): Boolean {
        if (id in guildData) {
            when (type) {
                DataType.PREFIX -> getData()?.prefix = value.toString()
            }
        } else {
            guildData.put(id, GuildData(value))
        }

        return if (' ' in table) {
            insert(table, id, value)
        } else {
            update(table, type.key, value)
        }
    }

    fun getData(): GuildData? = guildData[id]

    fun getPrefix(): String? = getData()?.prefix?.toString() ?: System.getProperty("prefix")

    companion object {
        private val URL = "jdbc:mariadb://%s/%s"
        private lateinit var database: Connection
        private val guildData = mutableMapOf<Long, GuildData>()

        private fun connect(host: String, name: String, username: String, password: String) {
            print("Connecting to the database... ")
            val milli = measureTimeMillis {
                database = DriverManager.getConnection(URL.format(host, name), username, password)
            }
            println("Done! (${milli}ms)")
        }

        private fun readGuildData() {
            print("Loading all guild data... ")
            val milli = measureTimeMillis {
                val read = "SELECT * FROM guilds"
                val statement = database.prepareStatement(read)
                val result = statement.executeQuery()

                while (result.next()) {
                    guildData.put(result.getLong("id"), GuildData(result.getString("prefix") ?: continue))
                }
            }
            println("Done! (${milli}ms)")
        }

        fun initialize(host: String, name: String, username: String, password: String) {
            try {
                connect(host, name, username, password)
                readGuildData()
            } catch (ex: QueryException) {
                System.err.println(ex.message)
                exitProcess(ExitStatus.SQL_ACCESS_DENIED.code)
            } catch (ex: SQLInvalidAuthorizationSpecException) {
                System.err.println(ex.message)
                exitProcess(ExitStatus.SQL_INVALID_PASSWORD.code)
            } catch (ex: SQLNonTransientConnectionException) {
                System.err.println(ex.message)
                exitProcess(ExitStatus.SQL_UNKNOWN_HOST.code)
            } catch (ex: SQLSyntaxErrorException) {
                System.err.println(ex.message)
                exitProcess(ExitStatus.SQL_UNKNOWN_DATABASE.code)
            }
        }
    }
}
