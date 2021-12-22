package org.gnit.cpsd

import java.math.BigInteger
import java.security.MessageDigest

inline fun String.sha1() :String {
    val digest = MessageDigest.getInstance("SHA-1")
    digest.reset()
    digest.update(this.toByteArray())
    return String.format("%040x", BigInteger(1, digest.digest()))
}