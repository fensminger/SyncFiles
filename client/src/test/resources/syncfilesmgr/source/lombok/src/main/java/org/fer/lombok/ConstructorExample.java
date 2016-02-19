package org.fer.lombok;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ConstructorExample<T> {
	protected int x, y;
	@NonNull
	protected T description;

	@NoArgsConstructor
	public static class NoArgsExample {
		@NonNull
		protected String field;
	}
}