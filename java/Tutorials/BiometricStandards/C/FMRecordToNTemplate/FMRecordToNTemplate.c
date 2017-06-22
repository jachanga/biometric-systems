#include <TutorialUtils.h>

#ifdef N_MAC_OSX_FRAMEWORKS
	#include <NCore/NCore.h>
	#include <NBiometricClient/NBiometricClient.h>
	#include <NBiometrics/NBiometrics.h>
	#include <NMedia/NMedia.h>
	#include <NLicensing/NLicensing.h>
#else
	#include <NCore.h>
	#include <NBiometricClient.h>
	#include <NBiometrics.h>
	#include <NMedia.h>
	#include <NLicensing.h>
#endif

const NChar title[] = N_T("FMRecordToNTemplate");
const NChar description[] = N_T("Demonstrates creation of NTemplate from FMRecord");
const NChar version[] = N_T("9.0.0.0");
const NChar copyright[] = N_T("Copyright (C) 2008-2017 Neurotechnology");

int usage()
{
	printf(N_T("usage:\n"));
	printf(N_T("\t%s [FMRecord] [NTemplate] [Standard] [FlagUseNeurotecFields]\n"), title);
	printf(N_T("\n"));
	printf(N_T("\tFMRecord - filename with FMRecord\n"));
	printf(N_T("\tNTemplate - filename for NTemplate\n"));
	printf(N_T("\tStandard - standard for FMRecord\n"));
	printf(N_T("\t\tANSI for ANSI/INCITS 378-2004\n"));
	printf(N_T("\t\tISO for ISO/IEC 19794-2\n"));
	printf(N_T("\tFlagUseNeurotecFields - 1 if FMRFV_USE_NEUROTEC_FIELDS flag is used; otherwise, 0 flag was not used.\n"));
	printf(N_T("example:\n"));
	printf(N_T("\t%s fmrecord.FMRecord template.NTemplate ANSI 1\n"), title);

	return 1;
}

NResult StoredFMRecordToPackedNTemplate(
	HNBuffer hBuffer, // buffer that contains FMRecord
	HNBuffer * phBuffer,// buffer that contains packed NTemplate
	BdifStandard standard,
	NUInt flags
)
{
	HFMRecord hFMRecord = NULL; // handle to FMRecord object
	HNTemplate hNTemplate = NULL; // handle to NTemplate object
	NResult result;

	// Create FMRecord object from FMRecord stored in memory
	result = FMRecordCreateFromMemoryN(hBuffer, flags, standard, NULL, &hFMRecord);
	if(NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("error in FMRecordCreateFromMemory, error code: %d\n"), result);
		goto FINALLY;
	}

	result = FMRecordToNTemplate(hFMRecord, 0, &hNTemplate);
	if(NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("error in FMRecordToNTemplate, error code: %d\n"), result);
		goto FINALLY;
	}

	// Pack NTemplate object
	result = NObjectSaveToMemoryN(hNTemplate, 0, phBuffer);
	if(NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("error in NObjectSaveToMemoryN, error code: %d\n"), result);
		goto FINALLY;
	}

	result = N_OK;
FINALLY:
	{
		NResult result2 = NObjectSet(NULL, &hFMRecord);
		if (NFailed(result2)) PrintErrorMsg(N_T("NObjectSet() failed (result = %d)!"), result2);
		result2 = NObjectSet(NULL, &hNTemplate);
		if (NFailed(result2)) PrintErrorMsg(N_T("NObjectSet() failed (result = %d)!"), result2);
	}

	return result;
}

int main(int argc, NChar **argv)
{
	NResult result = N_OK;
	HNBuffer hBuffer = NULL;
	HNBuffer hBufferIn = NULL;
	BdifStandard standard;
	const NChar * components = N_T("Biometrics.Standards.FingerTemplates");
	NBool available = NFalse;
	NUInt flags = 0;

	OnStart(title, description, version, copyright, argc, argv);

	if(argc < 5)
	{
		OnExit();
		return usage();
	}

	// check the license first
	result = NLicenseObtainComponents(N_T("/local"), N_T("5000"), components, &available);
	if (NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("NLicenseObtainComponents() failed, result = %d\n"), result);
		goto FINALLY;
	}

	if (!available)
	{
		printf(N_T("Licenses for %s not available\n"), components);
		result = N_E_NOT_ACTIVATED;
		goto FINALLY;
	}

	if (!strcmp(argv[3], N_T("ANSI")))
	{
		standard = bsAnsi;
	}
	else if (!strcmp(argv[3], N_T("ISO")))
	{
		standard = bsIso;
	}
	else
	{
		printf(N_T("wrong standard!"));
		result = N_E_FAILED;
		goto FINALLY;
	}

	// check if Neurotec flags are used
	flags = !strcmp(argv[4], N_T("1")) ? FMRFV_USE_NEUROTEC_FIELDS : 0;

	result = NFileReadAllBytesCN(argv[1], &hBufferIn);
	if (NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("error in NFileReadAllBytesCN, error code: %d\n"), result);
		goto FINALLY;
	}

	result = StoredFMRecordToPackedNTemplate(hBufferIn, &hBuffer, standard, flags);
	if(NFailed(result))
	{
		goto FINALLY;
	}

	result = NFileWriteAllBytesCN(argv[2], hBuffer);
	if (NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("error in NFileWriteAllBytesCN, error code: %d\n"), result);
		goto FINALLY;
	}

	result = N_OK;
FINALLY:
	{
		NResult result2 = NObjectSet(NULL, &hBuffer);
		if (NFailed(result2)) PrintErrorMsg(N_T("NObjectSet() failed (result = %d)!"), result2);
		result2 = NObjectSet(NULL, &hBufferIn);
		if (NFailed(result2)) PrintErrorMsg(N_T("NObjectSet() failed (result = %d)!"), result2);
		result2 = NLicenseReleaseComponents(components);
		if (NFailed(result2)) PrintErrorMsg(N_T("NLicenseReleaseComponents() failed, result = %d\n"), result2);
	}

	OnExit();
	return result;
}
