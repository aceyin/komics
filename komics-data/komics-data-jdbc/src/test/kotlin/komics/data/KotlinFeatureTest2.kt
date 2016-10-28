package komics.data

import org.junit.Test
import javax.persistence.Column
import javax.persistence.Id

/**
 * Created by ace on 2016/10/28.
 */


class KotlinFeatureTest2 {

    @Test
    fun test_get_annotation() {

    }
}


data class BeanUseJPAAnnotation(
        @Column(name = "id") @Id val id: String,
        @Column(name = "user_name") val name: String)