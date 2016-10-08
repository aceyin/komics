package komics.util

import io.kotlintest.specs.ShouldSpec

class StrUtilTest : ShouldSpec() {

    init {
        should("should_return_true_when_there_is_a_null_or_empty_string_in_a_array") {
            StrUtil.hasNull(*arrayOf("a", "", "b")) shouldBe true
            StrUtil.hasNull(*arrayOf("a", null, "b")) shouldBe true
        }

        should("should_return_false_when_all_items_in_a_string_array_are_not_null_nor_empty") {
            StrUtil.hasNull(*arrayOf("a", "1", "b")) shouldBe false
        }


        should("should_return_true_when_all_items_in_a_string_array_are_not_null_nor_empty") {
            StrUtil.notNull(*arrayOf("a", "1", "b")) shouldBe true
        }

        should("should_return_false_when_there_is_an_empty_or_null_string_in_a_string_array") {
            StrUtil.hasNull(*arrayOf("a", "", "b")) shouldBe true
            StrUtil.hasNull(*arrayOf("a", null, "b")) shouldBe true
        }
    }
}