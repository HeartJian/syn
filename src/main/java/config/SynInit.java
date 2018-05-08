package config;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SynInit {
    private final Logger logger = LoggerFactory.getLogger(SynInit.class);
//mysqlData:key 表名，Value->key 主键 value json;
    public SynInit(List<String> tableNameList,Map<String,Map> mysqlData){
        logger.info("开始同步");
        Jedis jedis=RedisClient.getInstance().jedisPool.getResource();
        HashMap<String,Map> jedisData=new HashMap<>();

        tableNameList.forEach(tableName->{
            Map dataMap=jedis.hgetAll(tableName);
            if(MapUtils.isNotEmpty(dataMap)){
                jedisData.put(tableName,dataMap);
            }
        });

           for(String redisKey:jedisData.keySet()){
               for(String mysqlTableName:mysqlData.keySet()){
                   if(redisKey.equalsIgnoreCase(mysqlTableName)){
                       for(String primaryKey:mysqlData.keySet()){
                           //不等就覆盖
                           if(!jedisData.get(redisKey).get(primaryKey).equals(mysqlData.get(redisKey).get(primaryKey))){
                               jedis.hset(redisKey,primaryKey,(String)mysqlData.get(redisKey).get(primaryKey));
                           }
                       };
                   }

               }
           }


    }
}

