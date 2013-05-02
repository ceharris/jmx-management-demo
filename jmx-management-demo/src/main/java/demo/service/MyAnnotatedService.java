package demo.service;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource(objectName = "MyDemo:type=MyService")
public class MyAnnotatedService extends MyService {

  @Override
  @ManagedAttribute
  public int getSerialNumber() {
    return super.getSerialNumber();
  }

  @Override
  @ManagedAttribute
  public int getPokeCounter() {
    return super.getPokeCounter();
  }

  @Override
  @ManagedAttribute
  public void setPokeCounter(int pokeCounter) {
    super.setPokeCounter(pokeCounter);
  }

  @Override
  @ManagedAttribute
  public int getIdleTimeout() {
    return super.getIdleTimeout();
  }

  @Override
  @ManagedAttribute
  public void setIdleTimeout(int idleTimeout) {
    super.setIdleTimeout(idleTimeout);
  }

  @Override
  @ManagedOperation
  public void start() throws Exception {
    super.start();
  }

  @Override
  @ManagedOperation
  public void stop() throws Exception {
    super.stop();
  }

  @Override
  @ManagedOperation
  public void poke() {
    super.poke();
  }

  @Override
  public void run() {
    super.run();
  }
  
}
