package com.stardecimal.game.config

import ch.qos.logback.core.*
import ch.qos.logback.core.encoder.*
import ch.qos.logback.core.read.*
import ch.qos.logback.core.rolling.*
import ch.qos.logback.core.status.*
import ch.qos.logback.classic.net.*
import ch.qos.logback.classic.encoder.PatternLayoutEncoder

statusListener(OnConsoleStatusListener)

appender("FILE", FileAppender) {
	file = "../../log/main.log"
	append = true
	encoder(PatternLayoutEncoder) {
		pattern = "%d %level %logger{5} - %msg%n"
	}
	rollingPolicy(TimeBasedRollingPolicy) {
		FileNamePattern = "/log/main-%d{yyyy-MM}.zip"
	}

}


appender("STDOUT", ConsoleAppender) {
	encoder(PatternLayoutEncoder) {
		pattern = "%-4relative - %msg%n"
	}
}



root(DEBUG, ["FILE", "STDOUT"])
scan()