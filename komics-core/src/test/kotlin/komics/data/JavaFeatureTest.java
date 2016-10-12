package komics.data;

import org.junit.Test;

import javax.persistence.Column;
import java.lang.reflect.Method;

/**
 * Created by ace on 2016/10/12.
 */
public class JavaFeatureTest {

    @Test
    public void test() {
        Method[] methods = AI.class.getDeclaredMethods();
        for (Method m :
                methods) {
            System.out.println(m.getAnnotations());
        }
    }

    interface AI {
        @Column
        String getId();
    }
}
