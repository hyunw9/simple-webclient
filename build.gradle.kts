plugins {
    id("java")
}
val springCloudVersion by extra("2024.0.0")
group = "hyunw9"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter:3.4.7")// 최신 안정화 버전 사용 권장
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.2.0")// 최신 안정화 버전 사용 권장
    // Reactor Netty (WebClient의 HTTP 클라이언트)
    implementation("io.projectreactor.netty:reactor-netty-http")// spring-boot-starter-webflux 버전에 맞춰 사용
    // Jackson (JSON 직렬화/역직렬화)
    implementation("com.fasterxml.jackson.core:jackson-databind")// 최신 안정화 버전 사용 권장
    // Typesafe Config (설정 파일 로딩)
//    implementation("com.typesafe")// 최신 안정화 버전 사용 권장
    // SLF4J (로깅 인터페이스)
    implementation("org.slf4j:slf4j-api")
    // Logback (SLF4J 구현체, 선택 사항)
    implementation("ch.qos.logback:logback-classic:1.4.14")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    platform("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server:4.3.0")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.3.0")
}

tasks.test {
    useJUnitPlatform()
}

