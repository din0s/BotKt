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
        ||-========================================================
        """.trimMargin("|"))
    }

    override fun onMessageReceived(e: MessageReceivedEvent) {
        val prefix = "~" // TODO: Per guild prefix
        val content = e.message.rawContent
        if (!content.startsWith(prefix, true))
            return

        val allArgs = content.substring(prefix.length).split("\\s+".toRegex())
        val command = Registry.getCommandByName(allArgs[0])
        val args = allArgs.drop(1)

        if (command == null || (e.isFromType(ChannelType.PRIVATE) && !command.allowPrivate))
            return

        command.execute(args, e)
    }
}
