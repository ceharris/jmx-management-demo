package demo.service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.vt.cns.kestrel.common.util.CountdownTimer;
import edu.vt.cns.kestrel.common.util.CountdownTimer.Units;
import edu.vt.cns.kestrel.common.util.DefaultCountdownTimer;

public class MyService implements Runnable, ServiceManager {

  private static final Logger logger = LoggerFactory.getLogger(MyService.class);
  
  protected final Lock threadLock = new ReentrantLock();
  private final Lock pokeLock = new ReentrantLock();
  private final Condition pokedCondition = pokeLock.newCondition();
  
  private final CountdownTimer timer = new DefaultCountdownTimer();
  
  private Thread thread;
  private int idleTimeout = 1000;
  private int serialNumber;
  private int pokeCounter;
  private boolean poked;
  
  /**
   * Gets the {@code serialNumber} property.
   */
  public int getSerialNumber() {
    return serialNumber;
  }

  /**
   * Gets the {@code pokeCounter} property.
   */
  public int getPokeCounter() {
    return pokeCounter;
  }

  /**
   * Sets the {@code pokeCounter} property.
   */
  public void setPokeCounter(int pokeCounter) {
    pokeLock.lock();
    try {
      this.pokeCounter = pokeCounter;
    }
    finally {
      pokeLock.unlock();
    }
  }

  /**
   * Gets the {@code idleTimeout} property.
   */
  public int getIdleTimeout() {
    threadLock.lock();
    try {
      return idleTimeout;
    }
    finally {
      threadLock.unlock();
    }
  }

  /**
   * Sets the {@code idleTimeout} property.
   */
  public void setIdleTimeout(int idleTimeout) {
    threadLock.lock();
    try {
      this.idleTimeout = idleTimeout;
    }
    finally {
      threadLock.unlock();
    }
  }

  public void start() throws Exception {
    threadLock.lockInterruptibly();
    try {
      if (thread == null) {
        thread = new Thread(this, "MyServiceBean." + serialNumber++);
        thread.start();
        logger.info("service thread started");
      }
    }
    finally {
      threadLock.unlock();
    }
  }
  
  public void stop() throws Exception {
    threadLock.lockInterruptibly();
    try {
      if (thread != null) {
        thread.interrupt();
        try {
          thread.join(5000);
          if (thread.isAlive()) {
            logger.warn("service thread did not stop");
          }
          else {
            logger.info("service thread stopped");
            thread = null;
          }
        }
        catch (InterruptedException ex) {
          logger.warn("interrupted while stopping service thread");
        }
      }
    }
    finally {
      threadLock.unlock();
    }
  }

  public void poke() {
    pokeLock.lock();
    try {
      poked = true;
      pokedCondition.signalAll();
    }
    finally {
      pokeLock.unlock();
    }
  }
  
  protected boolean isRunning() {
    threadLock.lock();
    try {
      return thread != null;
    }
    finally {
      threadLock.unlock();
    }
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        timer.start(getIdleTimeout(), Units.MILLISECONDS);
        while (!timer.isExpired()) {
          pokeLock.lockInterruptibly();
          try {
            while (!poked && !timer.isExpired()) {
              pokedCondition.await(timer.timeRemaining(), 
                  TimeUnit.MILLISECONDS);
            }
            if (poked) {
              poked = false;
              pokeCounter++;
              logger.info("service thread poked");
            }
            else {
              logger.debug("service thread idle");
            }
          }
          finally {
            pokeLock.unlock();
          }
        }
      }
      catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
      }
    }
    logger.info("service thread interrupted");
  }

}
