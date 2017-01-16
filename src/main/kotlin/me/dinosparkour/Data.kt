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

enum class DatabaseAuth(val key: String, val defaultValue: String = "") {
    HOST("dbHost", "localhost:3306"),
    NAME("dbName", "myDatabase"),
    USER("dbUser", "root"),
    PASSWORD("dbPassword", "letmein123");

    companion object {
        fun keys() = DatabaseAuth.values().map { it.key }
    }
}

enum class DatabaseColumn(val defaultValue: String) {
    PREFIX("~");

    override fun toString(): String {
        return super.toString().toLowerCase()
    }
}

data class GuildData(var prefix: Any?) {
    fun isNull(): Boolean = prefix == null
}

enum class SQLTables {
    GUILDS;

    override fun toString(): String {
        return super.toString().toLowerCase()
    }
}
