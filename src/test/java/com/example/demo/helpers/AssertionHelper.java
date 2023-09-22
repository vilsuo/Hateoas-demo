
package com.example.demo.helpers;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssertionHelper {
	
	public static void assertEmployeeNotFoundMessage(
			final String message, final Long id) {
		
		assertTrue(message.contains("Employee"));
		assertTrue(message.contains("id"));
		assertTrue(message.contains(String.valueOf(id)));
	}
}
