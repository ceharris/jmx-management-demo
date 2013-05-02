package demo.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.management.Notification;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.notification.NotificationPublisher;
import org.springframework.jmx.export.notification.NotificationPublisherAware;

public class MyServiceWithNotifications extends MyAnnotatedService 
    implements NotificationPublisherAware, InitializingBean {

  private static final String MESSAGES_PROPERTIES = "messages.properties";
  
  private NotificationPublisher publisher;
  private Properties messages;
  
  @Override
  public void setNotificationPublisher(NotificationPublisher publisher) {
    this.publisher = publisher;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    InputStream inputStream = getClass().getClassLoader()
        .getResourceAsStream(MESSAGES_PROPERTIES);
    if (inputStream == null) {
      throw new FileNotFoundException(MESSAGES_PROPERTIES);
    }
    try {
      messages = new Properties();
      messages.load(inputStream);
    }
    finally {
      try {
        inputStream.close();
      }
      catch (IOException ex) {
        ex.printStackTrace(System.err);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @ManagedOperation
  public void start() throws Exception {
    threadLock.lock();
    try {
      if (!isRunning()) {
        super.start();
        if (publisher != null) {
          publisher.sendNotification(new Notification("start", this, getSerialNumber() - 1, "service started"));
        }
      }
    }
    finally {
      threadLock.unlock();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @ManagedOperation
  public void stop() throws Exception {
    threadLock.lock();
    try {
      if (isRunning()) {
        super.stop();
        if (publisher != null) {
          publisher.sendNotification(new Notification("stop", this, 
              getSerialNumber() - 1, "service stopped"));
        }
      }
    }
    finally {
      threadLock.unlock();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @ManagedOperation
  public void poke() {
    threadLock.lock();
    try {
      if (isRunning()) {
        super.poke();
        int i = (int) (messages.size()*Math.random());
        publisher.sendNotification(new Notification("poke", this, getPokeCounter(),
            messages.getProperty(Integer.toString(i))));
      }
    }
    finally {
      threadLock.unlock();
    }
  }

}
