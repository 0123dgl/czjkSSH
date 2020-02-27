package text;


import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.impl.SetmealServiceImpl;
import org.junit.Test;

public class text {
    @Test
    public void fun() {
        SetmealServiceImpl setmealService = new SetmealServiceImpl();
        Setmeal byId = setmealService.findById(38);
        System.out.println(byId);

    }
}
