package com.featureflag.core;

import java.time.Instant;
import java.util.*;

public final class FlagDefinition {
	private final String key;
	private final boolean enabled;
	private final int rolloutPercent; // 0~100
	private final Targeting targeting;
	private final List<VariantAllocation> variants; // empty면 boolean flag처럼 사용
	private final String defaultVariant;
	private final Instant updatedAt;

	private FlagDefinition(Builder b) {
		this.key = Objects.requireNonNull(b.key, "key");
		this.enabled = b.enabled;
		this.rolloutPercent = clamp(b.rolloutPercent, 0, 100);
		this.targeting = b.targeting != null ? b.targeting : Targeting.allowAll();
		this.variants = Collections.unmodifiableList(new ArrayList<>(b.variants));
		this.defaultVariant = b.defaultVariant != null ? b.defaultVariant : "on";
		this.updatedAt = b.updatedAt != null ? b.updatedAt : Instant.now();
	}

	private static int clamp(int v, int min, int max) { return Math.max(min, Math.min(max, v)); }

	public String key() { return key; }
	public boolean enabled() { return enabled; }
	public int rolloutPercent() { return rolloutPercent; }
	public Targeting targeting() { return targeting; }
	public List<VariantAllocation> variants() { return variants; }
	public String defaultVariant() { return defaultVariant; }
	public Instant updatedAt() { return updatedAt; }

	public static Builder builder(String key) { return new Builder(key); }

	public static final class Builder {
		private final String key;
		private boolean enabled = true;
		private int rolloutPercent = 100;
		private Targeting targeting;
		private final List<VariantAllocation> variants = new ArrayList<>();
		private String defaultVariant;
		private Instant updatedAt;

		private Builder(String key) { this.key = key; }

		public Builder enabled(boolean v) { this.enabled = v; return this; }
		public Builder rolloutPercent(int v) { this.rolloutPercent = v; return this; }
		public Builder targeting(Targeting t) { this.targeting = t; return this; }
		public Builder variant(String name, int weight) { this.variants.add(new VariantAllocation(name, weight)); return this; }
		public Builder defaultVariant(String v) { this.defaultVariant = v; return this; }
		public Builder updatedAt(Instant t) { this.updatedAt = t; return this; }
		public FlagDefinition build() { return new FlagDefinition(this); }
	}

	public static final class VariantAllocation {
		private final String name;
		private final int weight; // 합계가 100일 필요는 없음(내부에서 합산)

		public VariantAllocation(String name, int weight) {
			this.name = Objects.requireNonNull(name, "name");
			this.weight = Math.max(0, weight);
		}
		public String name() { return name; }
		public int weight() { return weight; }
	}
}
