package com.neurotec.tutorials;

import com.neurotec.biometrics.standards.CBEFFRecord;
import com.neurotec.io.NBuffer;
import com.neurotec.io.NFile;
import com.neurotec.licensing.NLicense;
import com.neurotec.tutorials.util.LibraryManager;
import com.neurotec.tutorials.util.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UnpackComplexCbeffRecord {

	private static final String DESCRIPTION = "Unpack Complex CbeffRecord";
	private static final String NAME = "unpack-complex-cbeff-record";
	private static final String VERSION = "9.0.0.0";

	private static final String LICENSES = "Biometrics.Standards.Base";

	private static final Map<Integer, BdbFormat> lookup = new HashMap<Integer, BdbFormat>();

	static {
		for (BdbFormat v : BdbFormat.values()) {
			lookup.put(v.value, v);
		}
	}

	private enum BdbFormat {
		AN_TEMPLATE(0x001B8019),
		FC_RECORD_ANSI(0x001B0501),
		FC_RECORD_ISO(0x01010008),
		FI_RECORD_ANSI(0x001B0401),
		FI_RECORD_ISO(0x01010007),
		FM_RECORD_ANSI(0x001B0202),
		FM_RECORD_ISO(0x01010002),
		II_RECORD_ANSI_POLAR(0x001B0602),
		II_RECORD_ISO_POLAR(0x0101000B),
		II_RECORD_ANSI_RECTILINEAR(0x001B0601),
		II_RECORD_ISO_RECTILINEAR(0x01010009);


		private int value;

		private BdbFormat(int value) {
			this.value = value;
		}

	}

	private static int index;

	private static void usage() {
		System.out.println("usage:");
		System.out.format("\t%s [ComplexCbeffRecord] [PatronFormat]", NAME);
		System.out.println();
		System.out.println("\t[ComplexCbeffRecord] - filename of CbeffRecord which will be created.");
		System.out.println("\t[PatronFormat] - number identifying patron format (all supported values can be found in CbeffRecord class documentation).");
		System.out.println();
	}

	public static void main(String[] args) {
		LibraryManager.initLibraryPath();
		Utils.printTutorialHeader(DESCRIPTION, NAME, VERSION, args);

		if (args.length != 2) {
			usage();
			System.exit(1);
		}

		try {
			if (!NLicense.obtainComponents("/local", 5000, LICENSES)) {
				System.err.println("Could not obtain licenses");
				System.exit(-1);
			}

			// Read CbeffRecord buffer
			NBuffer packedCBEFFRecord = NFile.readAllBytes(args[0]);

			// Get CbeffRecord patron format
			// all supported patron formats can be found in CbeffRecord class documentation
			int patronFormat = Integer.parseInt(args[1], 16);

			// Create CbeffRecord object
			CBEFFRecord cbeffRecord = new CBEFFRecord(packedCBEFFRecord, patronFormat);

			// Start unpacking the record
			unpackRecords(cbeffRecord);
			System.out.println("Records sucessfully saved");

		} catch (Throwable th) {
			Utils.handleError(th);
		} finally {
			try {
				NLicense.releaseComponents(LICENSES);
			} catch (IOException e) {
				Utils.handleError(e);
			}
		}
	}

	private static void unpackRecords(CBEFFRecord cbeffRecord) {
		for (CBEFFRecord record : cbeffRecord.getRecords()) {
			if (record.getRecords().size() == 0) {
				recordToFile(record);
			} else {
				unpackRecords(record);
			}
		}
	}

	private static void recordToFile(CBEFFRecord record) {
		String fileName;
		try {
			fileName = String.format("Record_%s_%d.dat", lookup.get(record.getBdbFormat()), ++index);
		} catch (Exception ex) {
			fileName = String.format("Record_unknownFormat_%d.dat", ++index);
		}
		try {
			NFile.writeAllBytes(fileName, record.getBdbBuffer());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Utils.handleError(e);
		}
	}

}
