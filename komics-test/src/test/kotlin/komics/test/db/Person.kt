package komics.test.db

/**
 * Created by ace on 16/9/19.
 */
class Person(
        val name: String,
        val age: Int
) {
    fun hello() {
        print("$name,$age")
    }
}

object main {
    @JvmStatic fun main(args: Array<String>) {
        val p = Person("aa", 12)
        p.hello()
    }
}
