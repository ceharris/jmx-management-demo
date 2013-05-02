package demo.simple;

import demo.service.MyService;

public class UnmanagedDemo {

  public static void main(String[] args) throws Exception {
    MyService bean = new MyService();
    bean.start();
    while (true) {
      Thread.sleep(2000);
      bean.poke();
    }
  }

}
