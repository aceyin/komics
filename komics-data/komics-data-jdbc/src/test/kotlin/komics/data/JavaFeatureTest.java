package komics.data;

import komics.test.db.DaoTestBase;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ace on 2016/10/12.
 */
public class JavaFeatureTest {
    public static final String INSERT = "insert into preparestatement_test (id,version,created,modified) values(:id,:version,:created,:modified)";
    public static final String UPDATE = "update preparestatement_test set version=:version,created=:created,modified=:modified where id=:id";
    public static final String SELECT = "select * from preparestatement_test where id=:id";
    String table = "preparestatement_test";
    static final String sql = "create table preparestatement_test(id varchar(32),version bigint(11),created bigint(11),modified bigint(11))";

    @BeforeClass
    public static void before_class() {
        DaoTestBase.Companion.createTable(sql);
    }

    @Test
    public void test_preparestatement() throws SQLException {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(DaoTestBase.Companion.getDatasource());

        Map<String, Object> map = new HashMap<>();
        map.put("id", "123");
        map.put("version", 1);
        map.put("created", 1);
        map.put("modified", 0);

        template.update(INSERT, map);

        map.put("version", "version+1");
        map.put("created", "created");
        map.put("modified", "1");
        template.update(UPDATE, map);

        Map<String, Object> m2 = new HashMap<>();
        m2.put("id", "123");
        Map<String, Object> res1 = template.queryForMap(SELECT, m2);
    }

    class PstTest {
        private String id;
        private Long version;
        private Long created;
        private Long modified;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Long getVersion() {
            return version;
        }

        public void setVersion(Long version) {
            this.version = version;
        }

        public Long getCreated() {
            return created;
        }

        public void setCreated(Long created) {
            this.created = created;
        }

        public Long getModified() {
            return modified;
        }

        public void setModified(Long modified) {
            this.modified = modified;
        }
    }

}
