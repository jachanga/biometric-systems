package com.neurotec.samples.server.settings;

import java.io.File;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.neurotec.biometrics.NMFusionType;
import com.neurotec.biometrics.NMatchingSpeed;
import com.neurotec.biometrics.client.NBiometricClient;
import com.neurotec.samples.util.Utils;

public final class Settings implements Cloneable {

	// ===========================================================
	// Private static fields
	// ===========================================================

	private static Settings instance;
	private static Settings defaultInstance;
	private static final String PROJECT_NAME = "server-sample";
	private static final String PROJECT_DATA_FOLDER = Utils.getHomeDirectory() + Utils.FILE_SEPARATOR + ".neurotec" + Utils.FILE_SEPARATOR + PROJECT_NAME;
	private static final String SETTINGS_FILE_PATH = getSettingsFolder() + Utils.FILE_SEPARATOR + "user.xml";

	// ===========================================================
	// Private static methods
	// ===========================================================

	private static String getSettingsFolder() {
		File settingsFolder = new File(PROJECT_DATA_FOLDER);
		if (!settingsFolder.exists()) {
			settingsFolder.mkdirs();
		}
		return settingsFolder.getAbsolutePath();
	}

	// ===========================================================
	// Public static methods
	// ===========================================================

	public static synchronized Settings getDefaultInstance() {
		synchronized (Settings.class) {
			if (defaultInstance == null) {
				defaultInstance = new Settings();
				defaultInstance.loadDefault();
			}
			return defaultInstance;
		}
	}

	public static synchronized Settings getInstance() {
		synchronized (Settings.class) {
			if (instance == null) {
				instance = new Settings();
				instance.load();
			}
			return instance;
		}
	}

	// ===========================================================
	// Private fields
	// ===========================================================

	// ==============================================
	// Matching settings default values
	// ==============================================

	@Element
	private int matchingThreshold;
	@Element
	private NMFusionType fusionMode;

	@Element
	private NMatchingSpeed fingersMatchingSpeed;
	@Element
	private int fingersMatchingMode;
	@Element
	private int fingersMaximalRotation;
	@Element
	private int fingersMinMatchedFingerCount;
	@Element
	private int fingersMinMatchedFingerCountThreshold;

	@Element
	private NMatchingSpeed facesMatchingSpeed;
	@Element
	private int facesMatchingThreshold;

	@Element
	private NMatchingSpeed irisesMatchingSpeed;
	@Element
	private int irisesMatchingThreshold;
	@Element
	private int irisesMaximalRotation;
	@Element
	private int irisesMinMatchedIrisesCount;
	@Element
	private int irisesMinMatchedIrisesCountThreshold;

	@Element
	private NMatchingSpeed palmsMatchingSpeed;
	@Element
	private int palmsMaximalRotation;
	@Element
	private int palmsMinMatchedPalmsCount;
	@Element
	private int palmsMinMatchedPalmsCountThreshold;

	// ==============================================
	// Server connection settings default values
	// ==============================================

	@Element
	private String server;
	@Element
	private int clientPort;
	@Element
	private int adminPort;
	@Element
	private boolean isServerModeAccelerator;
	@Element
	private String mmaUsername;
	@Element
	private String mmaPassword;

	// ==============================================
	// Template loading settings default values
	// ==============================================

	@Element
	private boolean isTemplateSourceDb;
	@Element
	private String templateDirectory;
	@Element(required = false)
	private String dbServer;
	@Element(required = false)
	private boolean isDSNConnected = false;
	@Element(required = false)
	private String dsn;
	@Element(required = false)
	private String dbUsername;
	@Element(required = false)
	private String dbPassword;
	@Element
	private String table;
	@Element
	private String templateColumn;
	@Element
	private String idColumn;

	// ===========================================================
	// Private constructor
	// ===========================================================

	private Settings() {
	}

	// ===========================================================
	// Private methods
	// ===========================================================

	private void loadDefault() {
		loadDefaultMatchingSettings();
		loadDefaultConnectionSettings();
	}

	// ===========================================================
	// Public methods
	// ===========================================================

	public void load() {
		File file = new File(SETTINGS_FILE_PATH);
		Serializer serializer = new Persister();
		try {
			instance = serializer.read(Settings.class, file);
		} catch (Exception e) {
			try {
				instance = (Settings) getDefaultInstance().clone();
				instance.save();
			} catch (CloneNotSupportedException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void save() {
		Serializer serializer = new Persister();
		File file = new File(SETTINGS_FILE_PATH);
		try {
			serializer.write(this, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadDefaultMatchingSettings() {
		setMatchingThreshold(48);
		setFusionMode(NMFusionType.FUSE_ALWAYS);

		setFingersMatchingSpeed(NMatchingSpeed.LOW);
		setFingersMatchingMode(0);
		setFingersMaximalRotation(128);
		setFingersMinMatchedFingerCount(255);
		setFingersMinMatchedFingerCountThreshold(24);

		setFacesMatchingSpeed(NMatchingSpeed.LOW);
		setFacesMatchingThreshold(24);

		setIrisesMatchingSpeed(NMatchingSpeed.LOW);
		setIrisesMatchingThreshold(24);
		setIrisesMaximalRotation(11);
		setIrisesMinMatchedIrisesCount(255);
		setIrisesMinMatchedIrisesCountThreshold(24);

		setPalmsMatchingSpeed(NMatchingSpeed.LOW);
		setPalmsMaximalRotation(128);
		setPalmsMinMatchedPalmsCount(255);
		setPalmsMinMatchedPalmsCountThreshold(24);
	}

	public void loadDefaultConnectionSettings() {
		setServer("localhost");
		setClientPort(25452);
		setAdminPort(24932);
		setServerModeAccelerator(false);
		setMMAUser("Admin");
		setMMAPassword("Admin");

		setTemplateSourceDb(false);
		setTemplateDirectory(System.getProperty("user.dir"));

		setTable("templates");
		setTemplateColumn("template");
		setIdColumn("dbid");
	}

	public void loadDefaultDatabaseConnectionSettings() {

		setDSN("mysql_dsn");
		setDBUser("user");
		setDBPassword("pass");
		setDSNConnection(false);
	}

	public NBiometricClient createMatchingParameters(NBiometricClient biometricClient) {

		// General params
		biometricClient.setMatchingThreshold(getMatchingThreshold());

		// Finger params
		biometricClient.setFingersMatchingSpeed(getFingersMatchingSpeed());
		biometricClient.setFingersMaximalRotation(getFingersMaximalRotation());

		// Faces params
		biometricClient.setFacesMatchingSpeed(getFacesMatchingSpeed());

		// irises
		biometricClient.setIrisesMatchingSpeed(getIrisesMatchingSpeed());
		biometricClient.setIrisesMaximalRotation(getIrisesMaximalRotation());

		// palms
		biometricClient.setPalmsMatchingSpeed(getPalmsMatchingSpeed());
		biometricClient.setPalmsMaximalRotation(getPalmsMaximalRotation());

		// return params;
		return biometricClient;
	}

	public int getMatchingThreshold() {
		return matchingThreshold;
	}

	public void setMatchingThreshold(int matchingThreshold) {
		this.matchingThreshold = matchingThreshold;
	}

	public NMFusionType getFusionMode() {
		return fusionMode;
	}

	public void setFusionMode(NMFusionType fusionMode) {
		this.fusionMode = fusionMode;
	}

	public NMatchingSpeed getFingersMatchingSpeed() {
		return fingersMatchingSpeed;
	}

	public void setFingersMatchingSpeed(NMatchingSpeed fingersMatchingSpeed) {
		this.fingersMatchingSpeed = fingersMatchingSpeed;
	}

	public int getFingersMatchingMode() {
		return fingersMatchingMode;
	}

	public void setFingersMatchingMode(int fingersMatchingMode) {
		this.fingersMatchingMode = fingersMatchingMode;
	}

	public int getFingersMaximalRotation() {
		return fingersMaximalRotation;
	}

	public void setFingersMaximalRotation(int fingersMaximalRotation) {
		this.fingersMaximalRotation = fingersMaximalRotation;
	}

	public int getFingersMinMatchedFingerCount() {
		return fingersMinMatchedFingerCount;
	}

	public void setFingersMinMatchedFingerCount(int fingersMinMatchedFingerCount) {
		this.fingersMinMatchedFingerCount = fingersMinMatchedFingerCount;
	}

	public int getFingersMinMatchedFingerCountThreshold() {
		return fingersMinMatchedFingerCountThreshold;
	}

	public void setFingersMinMatchedFingerCountThreshold(int fingersMinMatchedFingerCountThreshold) {
		this.fingersMinMatchedFingerCountThreshold = fingersMinMatchedFingerCountThreshold;
	}

	public NMatchingSpeed getFacesMatchingSpeed() {
		return facesMatchingSpeed;
	}

	public void setFacesMatchingSpeed(NMatchingSpeed facesMatchingSpeed) {
		this.facesMatchingSpeed = facesMatchingSpeed;
	}

	public int getFacesMatchingThreshold() {
		return facesMatchingThreshold;
	}

	public void setFacesMatchingThreshold(int facesMatchingThreshold) {
		this.facesMatchingThreshold = facesMatchingThreshold;
	}

	public NMatchingSpeed getIrisesMatchingSpeed() {
		return irisesMatchingSpeed;
	}

	public void setIrisesMatchingSpeed(NMatchingSpeed irisesMatchingSpeed) {
		this.irisesMatchingSpeed = irisesMatchingSpeed;
	}

	public int getIrisesMatchingThreshold() {
		return irisesMatchingThreshold;
	}

	public void setIrisesMatchingThreshold(int irisesMatchingThreshold) {
		this.irisesMatchingThreshold = irisesMatchingThreshold;
	}

	public int getIrisesMaximalRotation() {
		return irisesMaximalRotation;
	}

	public void setIrisesMaximalRotation(int irisesMaximalRotation) {
		this.irisesMaximalRotation = irisesMaximalRotation;
	}

	public int getIrisesMinMatchedIrisesCount() {
		return irisesMinMatchedIrisesCount;
	}

	public void setIrisesMinMatchedIrisesCount(int irisesMinMatchedIrisesCount) {
		this.irisesMinMatchedIrisesCount = irisesMinMatchedIrisesCount;
	}

	public int getIrisesMinMatchedIrisesCountThreshold() {
		return irisesMinMatchedIrisesCountThreshold;
	}

	public void setIrisesMinMatchedIrisesCountThreshold(int irisesMinMatchedIrisesCountThreshold) {
		this.irisesMinMatchedIrisesCountThreshold = irisesMinMatchedIrisesCountThreshold;
	}

	public NMatchingSpeed getPalmsMatchingSpeed() {
		return palmsMatchingSpeed;
	}

	public void setPalmsMatchingSpeed(NMatchingSpeed palmsMatchingSpeed) {
		this.palmsMatchingSpeed = palmsMatchingSpeed;
	}

	public int getPalmsMaximalRotation() {
		return palmsMaximalRotation;
	}

	public void setPalmsMaximalRotation(int palmsMaximalRotation) {
		this.palmsMaximalRotation = palmsMaximalRotation;
	}

	public int getPalmsMinMatchedPalmsCount() {
		return palmsMinMatchedPalmsCount;
	}

	public void setPalmsMinMatchedPalmsCount(int palmsMinMatchedPalmsCount) {
		this.palmsMinMatchedPalmsCount = palmsMinMatchedPalmsCount;
	}

	public int getPalmsMinMatchedPalmsCountThreshold() {
		return palmsMinMatchedPalmsCountThreshold;
	}

	public void setPalmsMinMatchedPalmsCountThreshold(int palmsMinMatchedPalmsCountThreshold) {
		this.palmsMinMatchedPalmsCountThreshold = palmsMinMatchedPalmsCountThreshold;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public int getClientPort() {
		return clientPort;
	}

	public void setClientPort(int clientPort) {
		this.clientPort = clientPort;
	}

	public int getAdminPort() {
		return adminPort;
	}

	public void setAdminPort(int adminPort) {
		this.adminPort = adminPort;
	}

	public boolean isServerModeAccelerator() {
		return isServerModeAccelerator;
	}

	public void setServerModeAccelerator(boolean isServerModeAccelerator) {
		this.isServerModeAccelerator = isServerModeAccelerator;
	}

	public String getMMAUser() {
		return mmaUsername;
	}

	public void setMMAUser(String mmaUsername) {
		this.mmaUsername = mmaUsername;
	}

	public String getMMAPassword() {
		return mmaPassword;
	}

	public void setMMAPassword(String mmaPassword) {
		this.mmaPassword = mmaPassword;
	}

	public boolean isTemplateSourceDb() {
		return isTemplateSourceDb;
	}

	public void setTemplateSourceDb(boolean isTemplateSourceDb) {
		this.isTemplateSourceDb = isTemplateSourceDb;
	}

	public String getTemplateDirectory() {
		return templateDirectory;
	}

	public void setTemplateDirectory(String templateDirectory) {
		this.templateDirectory = templateDirectory;
	}

	public void setDSNConnection(boolean isDSNConnected) {
		this.isDSNConnected = isDSNConnected;
	}

	public boolean isDSNConnected() {
		return isDSNConnected;
	}

	public String getDSN() {
		return dsn;
	}

	public void setDSN(String value) {
		dsn = value;
	}

	public String getDBUser() {
		return dbUsername;
	}

	public void setDBUser(String value) {
		dbUsername = value;
	}

	public String getDBPassword() {
		return dbPassword;
	}

	public void setDBPassword(String value) {
		dbPassword = value;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getTemplateColumn() {
		return templateColumn;
	}

	public void setTemplateColumn(String templateColumn) {
		this.templateColumn = templateColumn;
	}

	public String getIdColumn() {
		return idColumn;
	}

	public void setIdColumn(String idColumn) {
		this.idColumn = idColumn;
	}
}
