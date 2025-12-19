package com.featureflag.core;

import java.util.*;

public final class FlagContext {
	private final String userId;              // deterministic rollout/AB의 기준
	private final Set<String> groups;         // "beta", "staff" 등
	private final Map<String, String> attrs;  // "region"="KR", "plan"="PRO" 등

	private FlagContext(Builder b) {
		this.userId = b.userId;
		this.groups = Collections.unmodifiableSet(new HashSet<>(b.groups));
		this.attrs = Collections.unmodifiableMap(new HashMap<>(b.attrs));
	}

	public String userId() { return userId; }
	public Set<String> groups() { return groups; }
	public Map<String, String> attrs() { return attrs; }

	public static Builder builder() { return new Builder(); }

	public static final class Builder {
		private String userId;
		private final Set<String> groups = new HashSet<>();
		private final Map<String, String> attrs = new HashMap<>();

		public Builder userId(String userId) { this.userId = userId; return this; }
		public Builder group(String g) { if (g != null) this.groups.add(g); return this; }
		public Builder groups(Collection<String> gs) { if (gs != null) this.groups.addAll(gs); return this; }
		public Builder attr(String k, String v) { if (k != null && v != null) this.attrs.put(k, v); return this; }
		public Builder attrs(Map<String,String> m) { if (m != null) this.attrs.putAll(m); return this; }
		public FlagContext build() { return new FlagContext(this); }
	}
}
