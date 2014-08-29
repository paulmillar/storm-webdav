package org.italiangrid.storm.webdav.config;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SAConfigurationImpl implements StorageAreaConfiguration {

	private final ServiceConfiguration serviceConfig;

	private String PROPERTIES_FILENAME_SUFFIX = ".properties";

	private List<StorageAreaInfo> saInfos;

	public SAConfigurationImpl(ServiceConfiguration sc) {

		serviceConfig = sc;

		final Logger log = LoggerFactory
			.getLogger(SAConfigurationImpl.class);
		
		final String saConfDir = serviceConfig.getSAConfigDir();

		log.debug("Loading SA configuration from directory {}", saConfDir);

		File dir = new File(saConfDir);
		directorySanityChecks(dir);

		File[] saFiles = dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File file, String name) {

				return (name.endsWith(PROPERTIES_FILENAME_SUFFIX));
			}
		});

		if (saFiles.length == 0) {
			String msg = String
				.format(
					"No storage area configuration files found in directory '%s'. Was looking for files ending in '%s'",
					dir.getAbsolutePath(), PROPERTIES_FILENAME_SUFFIX);
			throw new RuntimeException(msg);
		}

		saInfos = new ArrayList<StorageAreaInfo>();

		for (File f : saFiles) {

			Properties p = new Properties();
			try {
				p.load(new FileReader(f));
			} catch (Exception e) {
				throw new RuntimeException("Error loading properties: "
					+ e.getMessage(), e);
			}

			OwnerStorageAreaInfo saInfo = ConfigFactory.create(
				OwnerStorageAreaInfo.class, p);
			saInfos.add(saInfo);

			log.debug("{} loaded: {}", f, saInfo);
		}
	}

	private void directorySanityChecks(File directory) {

		if (!directory.exists())
			throw new IllegalArgumentException(
				"Storage area configuration directory does not exists: "
					+ directory.getAbsolutePath());

		if (!directory.isDirectory())
			throw new IllegalArgumentException(
				"Storage area configuration directory is not a directory: "
					+ directory.getAbsolutePath());

		if (!directory.canRead())
			throw new IllegalArgumentException(
				"Storage area configuration directory is not readable: "
					+ directory.getAbsolutePath());

		if (!directory.canExecute())
			throw new IllegalArgumentException(
				"Storage area configuration directory is not traversable: "
					+ directory.getAbsolutePath());

	}

	@Override
	public List<StorageAreaInfo> getStorageAreaInfo() {
		return saInfos;
	}

}
