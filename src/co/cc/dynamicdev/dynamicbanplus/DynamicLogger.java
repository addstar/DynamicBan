package co.cc.dynamicdev.dynamicbanplus;

import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;

public class DynamicLogger {

	private DynamicBan plugin;
	private static final Logger logger = Logger.getLogger("Minecraft");

	public DynamicLogger(DynamicBan instance) {
		this.plugin = instance;

		final Filter currentFilter = DynamicLogger.logger.getFilter();

		DynamicLogger.logger.setFilter(new Filter() {

			@Override
			public boolean isLoggable(LogRecord record) {
				if (currentFilter != null && currentFilter.isLoggable(record) == false) {
					return false;
				}

				if (record.getMessage() != null) {
					if (record.getMessage().contains("Fetching addPacket for removed entity")) {
						return false;
					}
				}

				return true;
			}
		});

	}

	private String buildString(String msg) {
		PluginDescriptionFile pdFile = plugin.getDescription();

		return pdFile.getName() + " " + pdFile.getVersion() + ": " + msg;
	}

	public void info(String msg) {
		DynamicLogger.logger.info(this.buildString(msg));
	}

	public void warn(String msg) {
		DynamicLogger.logger.warning(this.buildString(msg));
	}

	public void fatal(String msg) {
		DynamicLogger.logger.severe(this.buildString(msg));
	}
}