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

package me.dinosparkour.commands.impl

import me.dinosparkour.DatabaseColumn
import me.dinosparkour.SQLTables
import me.dinosparkour.commands.Command
import me.dinosparkour.managers.ConfigManager
import me.dinosparkour.managers.DatabaseManager
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.events.message.MessageReceivedEvent

class PrefixCmd : Command(name = "prefix",
        description = "Modifies the current guild's prefix",
        allowPrivate = false,
        requiredPermissions = arrayOf(Permission.ADMINISTRATOR),
        botRequiresPermissions = false) {

    override fun execute(args: List<String>, e: MessageReceivedEvent) {
        val dbManager = DatabaseManager(e.guild)
        if (args.isEmpty()) {
            e.reply("Current prefix: **${dbManager.getPrefixFormatted(e.jda)}**")
            return
        }

        var newPrefix = args.joinToString(" ")
                .replace("\\$$".toRegex(), "")

        if (newPrefix.matches("^<@!?${e.jda.selfUser.id}>$".toRegex())) {
            newPrefix = "%mention%"
        } else if (newPrefix.length > 32) {
            e.reply("That prefix is too long! Max length: `32`")
            return
        }

        val noEntry = dbManager.getData() == null
        val defaultPrefix = ConfigManager.getDefaultPrefix()
        val table = "${SQLTables.GUILDS}${if (noEntry) " (id, prefix)" else ""}"

        if (newPrefix in arrayOf("reset", defaultPrefix)) {
            if (!noEntry) {
                dbManager.deleteData(table, DatabaseColumn.PREFIX)
            }
            e.reply("Reset the prefix to **$defaultPrefix**")
        } else {
            if (newPrefix != dbManager.getPrefix()) {
                dbManager.saveData(table, DatabaseColumn.PREFIX, newPrefix)
            }
            e.reply("Set the prefix to **${dbManager.getPrefixFormatted(e.jda)}**")
        }
    }
}
