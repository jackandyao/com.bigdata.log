
package com.bigdata.storm.jdbc;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.storm.jdbc.common.Column;
import org.apache.storm.jdbc.common.ConnectionProvider;
import org.apache.storm.jdbc.common.HikariCPConnectionProvider;
import org.apache.storm.jdbc.common.JdbcClient;
import org.apache.storm.jdbc.mapper.JdbcMapper;
import org.apache.storm.jdbc.mapper.JdbcLookupMapper;
import org.apache.storm.jdbc.mapper.SimpleJdbcMapper;
import org.apache.storm.jdbc.mapper.SimpleJdbcLookupMapper;
import backtype.storm.LocalCluster;

import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * topolgy����
 * @author THink
 *
 */
public abstract class AbstractUserTopology {
	
    private static final List<String> setupSqls = Lists.newArrayList(
            "drop table if exists user",
            "drop table if exists department",
            "drop table if exists user_department",
            "create table if not exists user (user_id integer, user_name varchar(100), dept_name varchar(100), create_date date)",
            "create table if not exists department (dept_id integer, dept_name varchar(100))",
            "create table if not exists user_department (user_id integer, dept_id integer)",
            "insert into department values (1, 'R&D')",
            "insert into department values (2, 'Finance')",
            "insert into department values (3, 'HR')",
            "insert into department values (4, 'Sales')",
            "insert into user_department values (1, 1)",
            "insert into user_department values (2, 2)",
            "insert into user_department values (3, 3)",
            "insert into user_department values (4, 4)"
    );

    protected UserSpout userSpout;

    protected JdbcMapper jdbcMapper;

    protected JdbcLookupMapper jdbcLookupMapper;

    protected ConnectionProvider connectionProvider;

    protected static final String TABLE_NAME = "user";
    protected static final String JDBC_CONF = "jdbc.conf";
    protected static final String SELECT_QUERY = "select dept_name from department, user_department where department.dept_id = user_department.dept_id" +
            " and user_department.user_id = ?";

    public void execute(String[] args) throws Exception {
        if (args.length != 4 && args.length != 5) {
            System.out.println("Usage: " + this.getClass().getSimpleName() + " <dataSourceClassName> <dataSource.url> "
                    + "<user> <password> [topology name]");
            System.exit(-1);
        }
        Map map = Maps.newHashMap();
        map.put("dataSourceClassName", args[0]);//com.mysql.jdbc.jdbc2.optional.MysqlDataSource
        map.put("dataSource.url", args[1]);//jdbc:mysql://localhost/test
        map.put("dataSource.user", args[2]);//root

        if(args.length == 4) {
            map.put("dataSource.password", args[3]);//password
        }

        Config config = new Config();
        config.put(JDBC_CONF, map);

        ConnectionProvider connectionProvider = new HikariCPConnectionProvider(map);
        connectionProvider.prepare();
        int queryTimeoutSecs = 60;
        //ִ��jdbc���Ŀͻ���
        JdbcClient jdbcClient = new JdbcClient(connectionProvider, queryTimeoutSecs);
        for (String sql : setupSqls) {
            jdbcClient.executeSql(sql);
        }

        this.userSpout = new UserSpout();
        //�ӿ�ʵ��
        this.jdbcMapper = new SimpleJdbcMapper(TABLE_NAME, connectionProvider);
        connectionProvider.cleanup();
        Fields outputFields = new Fields("user_id", "user_name", "dept_name", "create_date");
        List<Column> queryParamColumns = Lists.newArrayList(new Column("user_id", Types.INTEGER));
        //�ӿ�ʵ��
        this.jdbcLookupMapper = new SimpleJdbcLookupMapper(outputFields, queryParamColumns);
        //�ӿ�ʵ��
        this.connectionProvider = new HikariCPConnectionProvider(map);
        if (args.length == 4) {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("test", config, getTopology());
            Thread.sleep(30000);
            cluster.killTopology("test");
            cluster.shutdown();
            System.exit(0);
        } else {
            StormSubmitter.submitTopology(args[4], config, getTopology());
        }
    }

    public abstract StormTopology getTopology();

}
