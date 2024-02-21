package cn.wolfcode.wolf2w.article.event;

import org.springframework.context.ApplicationListener;

///
public class StrategyCRUDListener implements ApplicationListener<StrategyCURDEevent> {
    @Override
    public void onApplicationEvent(StrategyCURDEevent event) {

        String save = (String) event.getSource();
        Long id = event.getId();

        //---es  save---

    }
}
