package com.neurotec.tutorials;

import java.io.IOException;

import com.neurotec.biometrics.standards.ANImageCompressionAlgorithm;
import com.neurotec.biometrics.standards.ANTemplate;
import com.neurotec.biometrics.standards.ANType1Record;
import com.neurotec.biometrics.standards.ANType4Record;
import com.neurotec.images.NImage;
import com.neurotec.licensing.NLicense;
import com.neurotec.tutorials.util.LibraryManager;
import com.neurotec.tutorials.util.Utils;

public final class ANTemplateType4FromNImage {

	private static final String DESCRIPTION = "Demonstrates creation of ANTemplate with type 4 record in it.";
	private static final String NAME = "antemplate-type4-from-nimage";
	private static final String VERSION = "9.0.0.0";

	private static void usage() {
		System.out.println("usage:");
		System.out.format("\t%s [NImage] [ANTemplate] [Tot] [Dai] [Ori] [Tcn]%n", NAME);
		System.out.println("");
		System.out.println("\t[NImage]     - filename with image file.");
		System.out.println("\t[ANTemplate] - filename for ANTemplate.");
		System.out.println("\t[Tot] - specifies type of transaction.");
		System.out.println("\t[Dai] - specifies destination agency identifier.");
		System.out.println("\t[Ori] - specifies originating agency identifier.");
		System.out.println("\t[Tcn] - specifies transaction control number.");
		System.out.println("");
	}

	public static void main(String[] args) {
		LibraryManager.initLibraryPath();
		final String components = "Biometrics.Standards.Fingers";

		Utils.printTutorialHeader(DESCRIPTION, NAME, VERSION, args);

		if (args.length != 6) {
			usage();
			System.exit(1);
		}

		ANTemplate template = null;
		NImage image = null;

		try {
			if (!NLicense.obtainComponents("/local", 5000, components)) {
				System.err.println("Could not obtain licenses for components: " + components);
				System.exit(-1);
			}

			String tot = args[2]; // type of transaction
			String dai = args[3]; // destination agency identifier
			String ori = args[4]; // originating agency identifier
			String tcn = args[5]; // transaction control number

			if ((tot.length() < 3) || (tot.length() > 4)) {
				System.out.println("Tot parameter should be 3 or 4 characters length.");
				System.exit(-1);
			}

			// Create empty ANTemplate object with only type 1 record in it
			template = new ANTemplate(ANTemplate.VERSION_CURRENT, tot, dai, ori, tcn, 0);

			// Open grayscale image
			image = NImage.fromFile(args[0]);
			image.setHorzResolution(500);
			image.setVertResolution(500);
			image.setResolutionIsAspectRatio(false);

			// Add Type 4 record to ANTemplate object
			ANType4Record record = new ANType4Record(ANTemplate.VERSION_CURRENT, 0, true, ANImageCompressionAlgorithm.NONE, image);
			template.getRecords().add(record);

			ANType1Record type1 = (ANType1Record)template.getRecords().get(0);
			type1.setNativeScanningResolution(ANType1Record.MIN_SCANNING_RESOLUTION);
			type1.setNominalTransmittingResolutionPpi(image.getHorzResolution());

			// Store ANTemplate object with type 4 record in file
			template.save(args[1]);
		} catch (Throwable th) {
			Utils.handleError(th);
		} finally {
			try {
				NLicense.releaseComponents(components);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (template != null) template.dispose();
			if (image != null) image.dispose();
		}
	}
}
