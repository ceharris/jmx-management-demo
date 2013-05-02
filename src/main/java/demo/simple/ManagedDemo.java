package demo.simple;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class ManagedDemo {

  public static void main(String[] args) throws Exception {
    MyService service = new MyService();
    MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
    ObjectName objName = new ObjectName("MyDemo:type=MyService");
    mbeanServer.registerMBean(service, objName);
    Thread.sleep(Long.MAX_VALUE);
  }

}
