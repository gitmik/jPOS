package org.jpos.apps.qsp.config;

import java.util.Properties;

import org.jpos.util.Logger;
import org.jpos.util.LogEvent;
import org.jpos.util.LogSource;
import org.jpos.util.Loggeable;
import org.jpos.util.NameRegistrar;
import org.jpos.core.SimpleConfiguration;
import org.jpos.core.Configurable;
import org.jpos.core.ConfigurationException;
import org.jpos.core.CardAgent;
import org.jpos.core.CardAgentLookup;

import org.jpos.apps.qsp.QSP;
import org.jpos.apps.qsp.QSPConfigurator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Configure Card Agent
 * @author <a href="mailto:apr@cs.com.uy">Alejandro P. Revilla</a>
 * @version $Revision$ $Date$
 */
public class ConfigCardAgent implements QSPConfigurator {
    public void config (QSP qsp, Node node) throws ConfigurationException
    {
	String className = 
	    node.getAttributes().getNamedItem ("class").getNodeValue();
	LogEvent evt = new LogEvent (qsp, "config-card-agent", className);
        try {
            Class c = Class.forName(className);
	    CardAgent agent = (CardAgent) c.newInstance();
	    if (agent instanceof LogSource) {
		((LogSource)agent).setLogger (
		    ConfigLogger.getLogger (node),
		    ConfigLogger.getRealm (node)
		);
	    }
	    if (agent instanceof Configurable)
		configureAgent ((Configurable) agent, node, evt);

	    if (agent instanceof Loggeable)
		evt.addMessage (agent);

	    CardAgentLookup.add (agent);
        } catch (ClassNotFoundException e) {
	    throw new ConfigurationException ("config-task:"+className, e);
        } catch (InstantiationException e) {
	    throw new ConfigurationException ("config-task:"+className, e);
        } catch (IllegalAccessException e) {
	    throw new ConfigurationException ("config-task:"+className, e);
	}
	Logger.log (evt);
    }
    private void configureAgent (Configurable agent, Node node, LogEvent evt)
	throws ConfigurationException
    {
	agent.setConfiguration (new SimpleConfiguration (
		ConfigUtil.addProperties (node, null, evt)
	    )
	);
    }
}

