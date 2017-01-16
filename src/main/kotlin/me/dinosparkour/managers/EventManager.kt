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

import me.dinosparkour.commands.Registry
import me.dinosparkour.utils.ChatUtil
import net.dv8tion.jda.core.entities.ChannelType
import net.dv8tion.jda.core.events.ReadyEvent
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class EventManager : ListenerAdapter() {

    override fun onReady(e: ReadyEvent) {
        val selfUser = e.jda.selfUser
        println("""
        ||-========================================================
        || Account Info: ${selfUser.name}#${selfUser.discriminator} (ID: ${selfUser.id})
        || Connected to ${e.jda.guilds.size} guilds, ${e.jda.textChannels.size} text channels
        || Prefix: ${ConfigManager.getDefaultPrefix()}
        ||-========================================================
        """.trimMargin("|"))
    }

    override fun onMessageReceived(e: MessageReceivedEvent) {
        val content = e.message.rawContent
        val selfId = e.jda.selfUser.id

        val db = DatabaseManager(e.guild)
        var prefix = db.getPrefix()
        if (prefix == "%mention%") {
            prefix = e.jda.selfUser.asMention
        }

        if (content.matches("^<@!?$selfId>$".toRegex())) {
            ChatUtil(e).reply("My prefix for this guild is: **${db.getPrefixFormatted(e.jda)}**")
            return
        }

        val isMentionPrefix = content.matches("^<@!?$selfId>\\s.*".toRegex())
        if (!isMentionPrefix && !content.startsWith(prefix, true))
            return

        prefix = if (isMentionPrefix) content.substring(0, content.indexOf('>') + 1) else prefix
        val index = if (isMentionPrefix) prefix.length + 1 else prefix.length

        val allArgs = content.substring(index).split("\\s+".toRegex())
        val command = Registry.getCommandByName(allArgs[0])
        val args = allArgs.drop(1)

        if (e.isFromType(ChannelType.PRIVATE) && command?.allowPrivate?.not() ?: return)
            return

        command?.execute(args, e)
    }
}
