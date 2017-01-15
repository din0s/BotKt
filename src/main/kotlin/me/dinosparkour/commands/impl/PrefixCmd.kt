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

import me.dinosparkour.DataType
import me.dinosparkour.SQLTables
import me.dinosparkour.commands.Command
import me.dinosparkour.managers.DatabaseManager
import me.dinosparkour.utils.ChatUtil
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.events.message.MessageReceivedEvent

class PrefixCmd : Command(name = "prefix",
        description = "Modifies the current guild's prefix",
        allowPrivate = false,
        requiredPermissions = arrayOf(Permission.ADMINISTRATOR),
        botRequiresPermissions = false) {

    override fun execute(args: List<String>, e: MessageReceivedEvent) {
        val dbManager = DatabaseManager(e.guild)
        val selfMention = e.jda.selfUser.asMention
        val prefix = dbManager.getPrefix() ?: selfMention

        if (args.isEmpty()) {
            e.reply("Current prefix: **$prefix**")
            return
        }

        val newPrefix = args.joinToString(" ").replace("\\$$".toRegex(), "")
        if (newPrefix.length > 32) {
            e.reply("That prefix is too long! Max length: `32`")
            return
        }

        val defaultPrefix: String? = System.getProperty("prefix")
        val noEntry = dbManager.getPrefix() == null
        val table = "${SQLTables.GUILDS}${if (noEntry) " (id, prefix)" else ""}"

        if (newPrefix in arrayOf("reset", defaultPrefix)) {
            if (!noEntry) {
                dbManager.deleteData(table, DataType.PREFIX)
            }
            e.reply("Reset the prefix to **${defaultPrefix ?: selfMention}**")
        } else {
            if (newPrefix != prefix) {
                dbManager.saveData(table, DataType.PREFIX, newPrefix)
            }
            e.reply("Set the prefix to **${ChatUtil.stripFormatting(newPrefix)}**")
        }
    }
}
