/**
 * Copyright (C) 2018-2019 toop.eu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.toop.tooppackagetracker;

import java.util.Collection;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class TrackerInitListener implements ServletContextListener
{
  private static final Logger LOGGER = LoggerFactory.getLogger (TrackerInitListener.class);

  public void contextInitialized (final ServletContextEvent aSce)
  {}

  public void contextDestroyed (final ServletContextEvent aSce)
  {
    // Try a clean shutdown
    final Collection <Receiver> aAll = PackageTrackerUI.getKafkaConsumers ().values ();
    for (final Receiver aReceiver : aAll)
      aReceiver.stop ();

    LOGGER.info ("Finished " + aAll.size () + " Kafka consumers");
  }
}
