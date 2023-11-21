package com.mibe.thinktory.telegram.chat

import com.mibe.thinktory.telegram.topic.TopicRequest
import eu.vendeli.tgbot.TelegramBot
import org.springframework.stereotype.Service
import java.util.ArrayDeque

private const val DIALOG_STACK = "dialogStack"
private const val TOPIC_REQUEST = "topicRequest"

@Service
class ChatDataServiceImpl(
    private val bot: TelegramBot
) : ChatDataService {

    override fun getDialogStack(userId: Long): DialogStack {
        var stack = bot.userData.get<DialogStack>(userId, DIALOG_STACK)
        if (stack == null) {
            stack = ArrayDeque()
            bot.userData.set(userId, DIALOG_STACK, stack)
        }
        return stack
    }

    override fun pushFrame(userId: Long, frame: DialogStackFrame) {
        getDialogStack(userId).push(frame)
    }

    override fun hasFrame(userId: Long): Boolean {
        return getDialogStack(userId).isNotEmpty()
    }

    override fun peekFrame(userId: Long): DialogStackFrame? {
        val stack = getDialogStack(userId)
        return if (stack.isNotEmpty()) stack.peek()!! else null
    }

    override fun popFrame(userId: Long): DialogStackFrame? {
        val stack = getDialogStack(userId)
        return if (stack.isNotEmpty()) stack.pop()!! else null
    }

    override fun setTopicRequest(userId: Long, topicRequest: TopicRequest) {
        bot.userData.set(userId, TOPIC_REQUEST, topicRequest)
    }

    override fun getTopicRequest(userId: Long) = bot.userData.get<TopicRequest>(userId, TOPIC_REQUEST)
}