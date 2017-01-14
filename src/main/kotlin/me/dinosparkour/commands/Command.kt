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

package me.dinosparkour.commands

import me.dinosparkour.utils.ChatUtil
import net.dv8tion.jda.core.MessageBuilder
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageEmbed
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import java.util.*
import java.util.function.Consumer

abstract class Command(val name: String,
                       val description: String,
                       val alias: Array<String> = arrayOf(name),
                       val allowPrivate: Boolean = true,
                       val authorExclusive: Boolean = false,
                       val requiredPermissions: Array<Permission> = arrayOf(),
                       val userRequiresPermissions: Boolean = true,
                       val botRequiresPermissions: Boolean = true)
    : EventListener {

    abstract fun execute(args: List<String>, e: MessageReceivedEvent)

    fun register() = Registry.registerCommand(this)

    fun String.toMessage(): Message = MessageBuilder().append(this).build()

    fun MessageReceivedEvent.reply(msg: Message, success: Consumer<Message>? = null) {
        ChatUtil(this).reply(msg, success)
    }

    fun MessageReceivedEvent.reply(embed: MessageEmbed, success: Consumer<Message>? = null) {
        ChatUtil(this).reply(embed, success)
    }

    fun MessageReceivedEvent.reply(text: String, success: Consumer<Message>? = null) {
        ChatUtil(this).reply(text, success)
    }
}