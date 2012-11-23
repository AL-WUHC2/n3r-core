package ch.qos.logback.classic.sift;

import java.util.ArrayList;
import java.util.List;

import org.n3r.core.joor.Reflect;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.sift.AppenderFactoryBase;
import ch.qos.logback.core.sift.SiftingJoranConfiguratorBase;

public class MyAppenderFactory extends AppenderFactory {

    public MyAppenderFactory(AppenderFactoryBase<ILoggingEvent> appenderFactory) {
        super(modify(appenderFactory.getEventList()), Reflect.on(appenderFactory).<String> get("key"));
    }

    /*
     * 此处为了适配AppenderFactoryBase去除头尾两个节点部分，特地在头尾增加两个空节点。
     */
    private static List<SaxEvent> modify(List<SaxEvent> eventList) {
        ArrayList<SaxEvent> arrayList = new ArrayList<SaxEvent>(eventList.size() + 2);
        arrayList.add(null);
        arrayList.addAll(eventList);
        arrayList.add(null);

        return arrayList;
    }

    @Override
    public SiftingJoranConfiguratorBase<ILoggingEvent> getSiftingJoranConfigurator(String discriminatingValue) {
        return new MySiftingJoranConfigurator(key, discriminatingValue);
    }

}
