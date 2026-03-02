package com.featureflag.config.spring;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 기능 플래그 모듈의 Spring Boot 설정 프로퍼티입니다.
 */
@ConfigurationProperties(prefix = "featureflag")
public class FeatureFlagProperties {

	/** v1: MEMORY 또는 FILE */
	private Store store = Store.MEMORY;
	private String filePath;
	private Duration cacheTtl = Duration.ofSeconds(3);

	/**
	 * 지원하는 저장소 백엔드 종류입니다.
	 */
	public enum Store { MEMORY, FILE }

	/**
	 * @return 선택된 플래그 저장소 백엔드
	 */
	public Store getStore() { return store; }

	/**
	 * @param store 플래그 저장소 백엔드
	 */
	public void setStore(Store store) { this.store = store; }

	/**
	 * @return 저장소가 FILE일 때 사용할 JSON 파일 경로
	 */
	public String getFilePath() { return filePath; }

	/**
	 * @param filePath 저장소가 FILE일 때 사용할 JSON 파일 경로
	 */
	public void setFilePath(String filePath) { this.filePath = filePath; }

	/**
	 * @return 파일 기반 저장소의 캐시 TTL
	 */
	public Duration getCacheTtl() { return cacheTtl; }

	/**
	 * @param cacheTtl 파일 기반 저장소의 캐시 TTL
	 */
	public void setCacheTtl(Duration cacheTtl) { this.cacheTtl = cacheTtl; }
}
