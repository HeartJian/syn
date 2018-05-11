package config;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;


import java.util.*;

//初始化类
public class SynInit {
    private final Logger logger = LoggerFactory.getLogger(SynInit.class);
    Jedis jedis = RedisClient.getInstance().jedisPool.getResource();

    //mysqlData:key 主键 value json;
    public void beginInit(String tableName, List<Map> mysqldataList) {
        logger.info("开始同步");


        Map jedisMap = jedis.hgetAll(tableName);
        if (MapUtils.isNotEmpty(jedisMap)) {
            //以数据库数据为准增加redis缺漏KEY
            for (Map mysqlData : mysqldataList) {
                mysqlData.keySet().forEach(key -> {
                    if (!jedisMap.keySet().contains(key)) {
                        jedis.hset(tableName, String.valueOf(key), (String) mysqlData.get(key));
                    }
                });

            }
            //删除redis多余key，更新原有数据
            for (Object jedisKey : jedisMap.keySet()) {
                List keyList = new ArrayList();
                Map mysqlDataMap = new HashMap<>();

                for (Map mysqlData : mysqldataList) {
                    mysqlData.keySet().forEach(dataKey -> {
                        keyList.add(dataKey);
                        mysqlDataMap.put(dataKey, mysqlData);
                    });
                }
                if (keyList.contains((Integer.valueOf((String) jedisKey)))) {
                    jedis.hset(tableName, String.valueOf(jedisKey),
                            (String) ((Map) mysqlDataMap.get(Integer.valueOf((String) jedisKey))).get(Integer.valueOf((String) jedisKey)));
                } else {
                    jedis.hdel(tableName, String.valueOf(jedisKey));

                }
                ;
            }
        } else {
            mysqldataList.forEach(mysqlData -> mysqlData.keySet().forEach(key -> {
                jedis.hset(tableName, String.valueOf(key), (String) mysqlData.get(key));
            }));
        }

    }

}


