package com.featureflag.api;

import com.featureflag.core.FlagContext;

public interface FeatureFlagClient {
	boolean isEnabled(String key, FlagContext ctx);
	String variant(String key, FlagContext ctx, String fallbackVariant);
}
