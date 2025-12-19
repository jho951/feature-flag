package com.featureflag.core;

import java.util.*;

public final class Targeting {
	private final Set<String> allowUserIds;
	private final Set<String> denyUserIds;
	private final Set<String> allowGroups;
	private final Set<String> denyGroups;
	private final Map<String, Set<String>> requireAttrsIn; // attrKey -> allowedValues

	private Targeting(Builder b) {
		this.allowUserIds = unmodSet(b.allowUserIds);
		this.denyUserIds = unmodSet(b.denyUserIds);
		this.allowGroups = unmodSet(b.allowGroups);
		this.denyGroups = unmodSet(b.denyGroups);
		this.requireAttrsIn = unmodMapSet(b.requireAttrsIn);
	}

	private static Set<String> unmodSet(Set<String> s) { return Collections.unmodifiableSet(new HashSet<>(s)); }
	private static Map<String, Set<String>> unmodMapSet(Map<String, Set<String>> m) {
		Map<String, Set<String>> out = new HashMap<>();
		for (var e : m.entrySet()) out.put(e.getKey(), Collections.unmodifiableSet(new HashSet<>(e.getValue())));
		return Collections.unmodifiableMap(out);
	}

	public static Targeting allowAll() { return builder().build(); }
	public static Builder builder() { return new Builder(); }

	public boolean isExplicitlyDenied(FlagContext ctx) {
		String uid = ctx.userId();
		if (uid != null && denyUserIds.contains(uid)) return true;
		for (String g : ctx.groups()) if (denyGroups.contains(g)) return true;
		return false;
	}

	public boolean isExplicitlyAllowed(FlagContext ctx) {
		String uid = ctx.userId();
		if (uid != null && allowUserIds.contains(uid)) return true;
		for (String g : ctx.groups()) if (allowGroups.contains(g)) return true;
		return false;
	}

	/** 타게팅 조건이 “있는 경우에만” eligibility 체크 */
	public boolean hasEligibilityRules() {
		return !allowUserIds.isEmpty() || !allowGroups.isEmpty() || !requireAttrsIn.isEmpty();
	}

	public boolean matchesEligibility(FlagContext ctx) {
		// allow-list/그룹/속성 조건 중 “하나라도” 충족하면 eligible로 두고 싶으면 여기서 OR로 바꾸면 됨.
		// v1은 운영에서 흔한 방식: "requireAttrsIn"은 AND, allowUserIds/allowGroups는 OR
		if (!requireAttrsIn.isEmpty()) {
			for (var e : requireAttrsIn.entrySet()) {
				String actual = ctx.attrs().get(e.getKey());
				if (actual == null || !e.getValue().contains(actual)) return false;
			}
		}
		if (!allowUserIds.isEmpty() || !allowGroups.isEmpty()) {
			return isExplicitlyAllowed(ctx);
		}
		return true;
	}

	public static final class Builder {
		private final Set<String> allowUserIds = new HashSet<>();
		private final Set<String> denyUserIds = new HashSet<>();
		private final Set<String> allowGroups = new HashSet<>();
		private final Set<String> denyGroups = new HashSet<>();
		private final Map<String, Set<String>> requireAttrsIn = new HashMap<>();

		public Builder allowUser(String id) { if (id != null) allowUserIds.add(id); return this; }
		public Builder denyUser(String id) { if (id != null) denyUserIds.add(id); return this; }
		public Builder allowGroup(String g) { if (g != null) allowGroups.add(g); return this; }
		public Builder denyGroup(String g) { if (g != null) denyGroups.add(g); return this; }
		public Builder requireAttrIn(String key, Set<String> values) {
			if (key != null && values != null) requireAttrsIn.put(key, new HashSet<>(values));
			return this;
		}
		public Targeting build() { return new Targeting(this); }
	}
}
