package komics.core.spring

import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.TransactionManagementConfigurer

/**
 * Created by ace on 2016/10/13.
 */
//@MapperScan("net.myproject.db")
//@Configuration
//@EnableTransactionManagement
open class SpringTransactionConfigurer : TransactionManagementConfigurer {
    override fun annotationDrivenTransactionManager(): PlatformTransactionManager {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

//    @Bean
//    fun dataSourceStatdb(): DataSource {
//        val dataSource = BasicDataSource()
//        dataSource.setDriverClassName(statdbDriverClassName)
//        dataSource.setUrl(statdbUrl)
//        dataSource.setUsername(statdbId)
//        dataSource.setPassword(statdbPw)
//        dataSource.setValidationQuery(statdbValidationQuery)
//        return dataSource
//    }
//    @Bean
//    fun transactionManager(): PlatformTransactionManager {
//        return DataSourceTransactionManager(dataSourceStatdb())
//    }
//
//    @Bean
//    @Throws(Exception::class)
//    fun sqlSessionFactory(): SessionFactory {
//        val sessionFactory = SqlSessionFactoryBean()
//        sessionFactory.setDataSource(dataSourceStatdb())
//        sessionFactory.setMapperLocations(PathMatchingResourcePatternResolver().getResources("classpath:mybatis/query/*.xml"))
//        return sessionFactory.getObject()
//    }
//
//    @Bean
//    @Throws(Exception::class)
//    fun sqlSessionTemplate(): SqlSessionTemplate {
//        return SqlSessionTemplate(sqlSessionFactory())
//    }
}
