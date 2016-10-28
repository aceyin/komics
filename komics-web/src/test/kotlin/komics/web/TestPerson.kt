package komics.web

import org.hibernate.validator.constraints.NotEmpty
import javax.validation.constraints.Max
import javax.validation.constraints.Min

/**
 * Created by ace on 2016/10/23.
 */

/**
 * This class is only for test
 */
data class TestPerson(
        @get:NotEmpty(message = "name can not be null")
        val name: String,
        @get:Min(value = 1, message = "age should not less than 1")
        @get:Max(value = 200, message = "age should not grater than 200")
        val age: Int) : Form