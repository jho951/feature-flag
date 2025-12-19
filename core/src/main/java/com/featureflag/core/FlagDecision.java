package com.featureflag.core;

import java.util.Collections;
import java.util.Map;

public final class FlagDecision {
	private final boolean enabled;
	private final String variant;
	private final String reason;
	private final Map<String, Object> meta;

	public FlagDecision(boolean enabled, String variant, String reason, Map<String, Object> meta) {
		this.enabled = enabled;
		this.variant = variant;
		this.reason = reason;
		this.meta = meta == null ? Map.of() : Collections.unmodifiableMap(meta);
	}

	public boolean enabled() { return enabled; }
	public String variant() { return variant; }
	public String reason() { return reason; }
	public Map<String, Object> meta() { return meta; }
}
