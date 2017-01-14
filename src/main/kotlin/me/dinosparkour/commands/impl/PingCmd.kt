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

import me.dinosparkour.commands.Command
import me.dinosparkour.utils.ChatUtil
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import java.util.function.Consumer

class PingCmd : Command("ping", "Returns an estimated ping to Discord's servers") {

    override fun execute(args: List<String>, e: MessageReceivedEvent) {
        var time = System.currentTimeMillis()
        e.reply("Pinging...", Consumer {
            time = (System.currentTimeMillis() - time) / 2
            ChatUtil.edit(it, "**Ping:** ${time}ms")
        })
    }
}
