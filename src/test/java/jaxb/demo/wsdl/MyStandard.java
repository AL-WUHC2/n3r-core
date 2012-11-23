package jaxb.demo.wsdl;

//import com.sun.tools.internal.xjc.XJCFacade;
import com.sun.xml.internal.bind.api.impl.NameConverter.Standard;

/*
C:\apache-cxf-2.6.2\bin>wsdl2java.bat -frontend jaxws21 -xjc-camelcase-always D:\transferstation\wsdl\wsdl\UsrForNorthSer.wsdl
 */
public class MyStandard extends Standard {

    public static void main(String[] args) throws Throwable {
        // xjc -wsdl UsrForNorthSer.wsdl -d . -camelcase-always
//        XJCFacade.main(new String[] { "-camelcase-always", "-wsdl", "D:\\中转站\\wsdl\\wsdl\\UsrForNorthSer.wsdl", "-d",
//                "c:\\test"
//        });
    }
}
