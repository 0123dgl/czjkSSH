package text;


import com.itheima.health.controller.SetmealMobileController;
import com.itheima.health.entity.Result;
import org.junit.Test;

public class text {
    @Test
    public void fun() {
        SetmealMobileController setmealMobileController = new SetmealMobileController();
        Result byId = setmealMobileController.findById(38);
        System.out.println(byId.toString());


    }

}
