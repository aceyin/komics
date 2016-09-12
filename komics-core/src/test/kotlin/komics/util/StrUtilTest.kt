package komics.util

import io.kotlintest.specs.StringSpec

class StrUtilTest : StringSpec() {
    init {
        "should return true when there is a null or empty string in a array" {
            StrUtil.hasNull(*arrayOf("a", "", "b")) shouldBe true
            StrUtil.hasNull(*arrayOf("a", null, "b")) shouldBe true
        }

        "should return false when all items in a string array are not null nor empty" {
            StrUtil.hasNull(*arrayOf("a", "1", "b")) shouldBe false
        }

        "should return true when all items in a string array are not null nor empty" {
            StrUtil.notNull(*arrayOf("a", "1", "b")) shouldBe true
        }

        "should return false when there is an empty or null string in a string array" {
            StrUtil.hasNull(*arrayOf("a", "", "b")) shouldBe true
            StrUtil.hasNull(*arrayOf("a", null, "b")) shouldBe true
        }
    }
}