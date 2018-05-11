package common;

import com.alibaba.fastjson.JSON;
import config.DBUtils;
import config.SynInit;

import java.util.*;

public class SynTable {

    DBUtils DB = new DBUtils();
    SynInit synInit=new SynInit();
    enum Table {
        TEST(1, "test", true);
        int code;
        String tableName;
        boolean alive;

        Table(int code, String tableName, boolean alive) {
            this.code = code;
            this.tableName = tableName;
            this.alive = alive;
        }

        public static List getAllName() {
            List<String> tableNameList = new ArrayList<>();
            for (Table table : Table.values()) {
                tableNameList.add(table.name());

            }
            return tableNameList;
        }
    }

    public void synEnumTable() {

        Table.getAllName().forEach(tableName -> {
            List dataList=new ArrayList();
            DB.queryAll((String) tableName).forEach(data -> {
                Map map=new HashMap<>();
                map.put(data.get("id"),JSON.toJSONString(data));
                dataList.add(map);

            });
            synInit.beginInit((String)tableName,dataList);

        });



    }
}
//        mysqlData.put((String) tableName, (Map) new HashMap<>().put(data.get("id"), JSON.toJSON(data))
