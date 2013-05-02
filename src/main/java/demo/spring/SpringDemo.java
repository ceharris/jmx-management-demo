package demo.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringDemo {

  @SuppressWarnings("resource")
  public static void main(String[] args) throws Exception {
    new ClassPathXmlApplicationContext("spring/example-notifications.xml");
    Thread.sleep(Long.MAX_VALUE);
  }

}
