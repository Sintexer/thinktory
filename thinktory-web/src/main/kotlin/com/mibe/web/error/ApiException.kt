package com.mibe.web.error

sealed class ApiException(msg: String, val code: Int) : Exception(msg)
