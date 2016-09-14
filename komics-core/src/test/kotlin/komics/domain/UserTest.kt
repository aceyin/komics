package komics.domain

import io.kotlintest.specs.ShouldSpec
import komics.core.Application

/**
 * Created by ace on 16/9/13.
 */
class UserTest : ShouldSpec() {

    override fun beforeAll() {
        Application.initialize(arrayOf(""), mapOf<String, String>())
    }

    override fun afterAll() {

    }

    init {
        should("save user success") {

        }
    }
}