package demo.service;

public interface ServiceManager {

  void start() throws Exception;
  
  void stop() throws Exception;
  
  void poke();

  int getSerialNumber();
  
  int getPokeCounter();
  
  void setPokeCounter(int pokeCounter);
  
  int getIdleTimeout();
  
  void setIdleTimeout(int idleTimeout);

}
