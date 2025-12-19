package com.featureflag.core;

import java.util.*;

public interface FlagStore {
	Optional<FlagDefinition> find(String key);
	default Map<String, FlagDefinition> findAll() { return Map.of(); }
}
