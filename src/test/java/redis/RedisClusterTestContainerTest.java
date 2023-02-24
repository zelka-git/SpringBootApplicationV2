package redis;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.SocketAddressResolver;
import java.net.SocketAddress;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultPropertiesPropertySource;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;

@Disabled
@ExtendWith(SpringExtension.class)
class RedisClusterTestContainerTest {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    void testWithClusterRedisTestContainer() {
        StringRedisTemplate stringRedisTemplate = this.stringRedisTemplate;

        stringRedisTemplate.opsForSet().add("redisCluster", "value2");

        stringRedisTemplate.executePipelined(
            (RedisCallback<Object>) connection -> {
                StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
                for (int i = 0; i < 10; i++) {
                    stringRedisConn.sAdd(String.valueOf(i), String.valueOf(i + 1), String.valueOf(i + 2));
                }
                return null;
            });

        Cursor<String> cursor = stringRedisTemplate.scan(ScanOptions.scanOptions().build());
        while (cursor.hasNext()) {
            System.out.println("redisTemplate: " + cursor.next());
        }
        System.out.println("End");
    }

    @TestConfiguration
    static class TestContextConfiguration {

        public static final Set<Integer> redisClusterPorts = Set.of(7000, 7001, 7002, 7003, 7004, 7005);

        private static final ConcurrentMap<Integer, Integer> redisClusterNatPortMapping = new ConcurrentHashMap<>();
        private static final ConcurrentMap<Integer, SocketAddress> redisClusterSocketAddresses =
            new ConcurrentHashMap<>();

        static GenericContainer<?> redisClusterContainer;

        static {
            redisClusterContainer = new GenericContainer<>("grokzen/redis-cluster:6.0.7")
                .withExposedPorts(redisClusterPorts.toArray(new Integer[0]));
            redisClusterContainer.start();

            final String redisClusterNodes = redisClusterPorts.stream()
                .map(port -> {
                    Integer mappedPort = redisClusterContainer.getMappedPort(port);
                    redisClusterNatPortMapping.put(port, mappedPort);
                    return redisClusterContainer.getHost() + ":" + mappedPort;
                })
                .collect(Collectors.joining(","));

            System.setProperty("spring.redis.cluster.nodes", redisClusterNodes);
        }

        @Bean
        LettuceConnectionFactory lettuceConnectionFactoryCluster(ClientResources lettuceClientResources) {

            var genericObjectPoolConfig = new GenericObjectPoolConfig();
            genericObjectPoolConfig.setMaxIdle(20);
            genericObjectPoolConfig.setMaxTotal(100);
            genericObjectPoolConfig.setMinIdle(10);

            ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                .enablePeriodicRefresh(Duration.ofMinutes(10))
                .enableAllAdaptiveRefreshTriggers()
                .build();

            var clientConfiguration = LettucePoolingClientConfiguration.builder()
                .clientResources(lettuceClientResources)
                .clientOptions(ClusterClientOptions.builder()
                    .topologyRefreshOptions(topologyRefreshOptions)
                    .disconnectedBehavior(ClientOptions.DisconnectedBehavior.DEFAULT)
                    .autoReconnect(false)
                    .socketOptions(SocketOptions.builder()
                        .connectTimeout(Duration.ofMillis(100))
                        .build())
                    .timeoutOptions(TimeoutOptions.builder()
                        .connectionTimeout()
                        .fixedTimeout(Duration.ofMillis(100))
                        .build())
                    .build())
                .poolConfig(genericObjectPoolConfig)
                .build();

            Map<String, Object> properties = Map.of(
                "spring.redis.cluster.nodes", System.getProperty("spring.redis.cluster.nodes"),
                "spring.redis.cluster.max-redirects", 4);
            var defaultPropertiesPropertySource = new DefaultPropertiesPropertySource(properties);
            var redisClusterConfiguration = new RedisClusterConfiguration(defaultPropertiesPropertySource);
            return new LettuceConnectionFactory(redisClusterConfiguration, clientConfiguration);
        }

        @Bean
        public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory lettuceConnectionFactoryCluster) {
            var stringRedisTemplate = new StringRedisTemplate();
            stringRedisTemplate.setConnectionFactory(lettuceConnectionFactoryCluster);
            return stringRedisTemplate;
        }

        @Bean(destroyMethod = "shutdown")
        public ClientResources lettuceClientResources() {
            final SocketAddressResolver socketAddressResolver = new SocketAddressResolver() {
                @Override
                public SocketAddress resolve(RedisURI redisURI) {
                    Integer mappedPort = redisClusterNatPortMapping.get(redisURI.getPort());
                    if (mappedPort != null) {
                        SocketAddress socketAddress = redisClusterSocketAddresses.get(mappedPort);
                        if (socketAddress != null) {
                            return socketAddress;
                        }
                        redisURI.setPort(mappedPort);
                    }

                    redisURI.setHost(DockerClientFactory.instance().dockerHostIpAddress());

                    SocketAddress socketAddress = super.resolve(redisURI);
                    redisClusterSocketAddresses.putIfAbsent(redisURI.getPort(), socketAddress);
                    return socketAddress;
                }
            };
            return ClientResources.builder()
                .socketAddressResolver(socketAddressResolver)
                .build();
        }
    }
}
