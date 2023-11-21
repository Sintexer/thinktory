package com.mibe.thinktory.telegram.chat

import com.mibe.thinktory.telegram.topic.TopicRequest
import java.util.Deque

typealias DialogStack = Deque<DialogStackFrame>

interface ChatDataService {

    fun getDialogStack(userId: Long) : DialogStack

    fun pushFrame(userId: Long, frame: DialogStackFrame)
    fun hasFrame(userId: Long): Boolean
    fun peekFrame(userId: Long): DialogStackFrame?
    fun popFrame(userId: Long): DialogStackFrame?

    fun setTopicRequest(userId: Long, topicRequest: TopicRequest)
    fun getTopicRequest(userId: Long): TopicRequest?
}