package komics.data.jdbc

import komics.data.Entity
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import javax.sql.DataSource
import kotlin.reflect.KClass

/**
 * Created by ace on 2016/9/29.
 */
class Db(val datasource: DataSource) {

    private val template = NamedParameterJdbcTemplate(datasource)

    /**
     * Insert a new entity into database table.
     */
    fun <E : Entity> insert(entity: E): Boolean {
        val sql = SQL.insert(entity.colstr()).into(entity.javaClass.kotlin).toString()

        val param = BeanPropertySqlParameterSource(entity)
        val e = template.update(sql, param)
        return e > 0
    }

    /**
     * Batch insert
     */
    fun <E : Entity> batchInsert(entities: List<E>): Boolean {
        TODO("to be implemented")
    }

    /**
     * Update by id
     */
    fun <E : Entity> updateById(id: String, entity: E): Boolean {
        TODO("to be implemented")
    }

    /**
     * Delete by id
     */
    fun <E : Entity> deleteById(clazz: KClass<E>, id: String): Boolean {
        TODO("to be implemented")
    }

    /**
     * Query by id
     */
    fun <E : Entity> queryById(clazz: KClass<E>, id: String): E? {
        TODO("to be implemented")
    }


}