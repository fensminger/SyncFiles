package org.fer.lombok;

import lombok.extern.log4j.Log4j;


@Log4j
public class LogExample {

	public static void main(String... args) {
		log.error("Something's wrong here");
	}
}

