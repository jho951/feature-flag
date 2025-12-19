package com.featureflag.core.store;

import com.featureflag.core.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryFlagStore implements FlagStore {
	private final Map<String, FlagDefinition> map = new ConcurrentHashMap<>();

	@Override
	public Optional<FlagDefinition> find(String key) {
		return Optional.ofNullable(map.get(key));
	}

	@Override
	public Map<String, FlagDefinition> findAll() {
		return Collections.unmodifiableMap(map);
	}

	public void put(FlagDefinition def) { map.put(def.key(), def); }
	public void remove(String key) { map.remove(key); }
}
