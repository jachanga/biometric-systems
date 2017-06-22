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

const NChar title[] = N_T("CbeffRecordToNTemplate");
const NChar description[] = N_T("Converting CbeffRecord to NTemplate");
const NChar version[] = N_T("9.0.0.0");
const NChar copyright[] = N_T("Copyright (C) 2014-2017 Neurotechnology");

#define MAX_COMPONENTS 8

static const NChar * Components[MAX_COMPONENTS] =
{
	N_T("Biometrics.Standards.Base"),
	N_T("Biometrics.Standards.Irises"),
	N_T("Biometrics.Standards.Fingers"),
	N_T("Biometrics.Standards.Palms"),
	N_T("Biometrics.IrisExtraction"),
	N_T("Biometrics.FingerExtraction"),
	N_T("Biometrics.FaceExtraction"),
	N_T("Biometrics.PalmExtraction")
};

int usage()
{
	printf(N_T("usage:\n"));
	printf(N_T("\t%s [CbeffRecord] [PatronFormat] [NTemplate]\n"), title);
	printf(N_T("\n"));
	printf(N_T("\t[NTemplate] - filename of NTemplate\n"));
	printf(N_T("\t[CbeffRecord] - filename for CbeffRecord\n"));
	printf(N_T("\t[PatronFormat] - hex number identifying patron format (all supported values can be found in CbeffRecord class documentation)\n"));
	return 1;
}

int main(int argc, NChar **argv)
{
	NResult result = N_OK;
	HNBuffer hBuffer = NULL;
	HNBuffer hNTemplateBuffer = NULL;
	HCbeffRecord hCbeffRecord = NULL; // handle to CbeffRecord object
	HNSubject hSubject = NULL;
	HNBiometricEngine hBiometricEngine = NULL;
	HNString hBiometricStatus = NULL;
	const NChar * szBiometricStatus = NULL;
	const NChar * obtainedLicenses[MAX_COMPONENTS];
	NUInt patronFormat = 0;
	NBiometricStatus biometricStatus;
	NInt obtainedLicensesCount = 0, i = 0;

	OnStart(title, description, version, copyright, argc, argv);

	if(argc != 4)
	{
		OnExit();
		return usage();
	}

	// check the license first
	for (i = 0; i < MAX_COMPONENTS; i++)
	{
		NBool available;
		result = NLicenseObtainComponents(N_T("/local"), N_T("5000"), Components[i], &available);
		if (NFailed(result))
		{
			result = PrintErrorMsgWithLastError(N_T("NLicenseObtainComponents() failed, result = %d\n"), result);
			goto FINALLY;
		}
		if (available)
		{
			obtainedLicenses[obtainedLicensesCount++] = Components[i];
		}
	}

	if (obtainedLicensesCount == 0)
	{
		printf(N_T("Could not obtain any matching license\n"));
		result = N_E_NOT_ACTIVATED;
		goto FINALLY;
	}

	// read CbeffRecord
	result = NFileReadAllBytesCN(argv[1], &hBuffer);
	if (NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("NFileReadAllBytesN() failed, result = %d\n"), result);
		goto FINALLY;
	}

	// get CbeffRecord patron format
	result = NUInt32Parse(argv[2], N_T("X"), &patronFormat);
	if (NFailed(result))
	{ 
		result = PrintErrorMsgWithLastError(N_T("NUInt32Parse() failed, result = %d\n"), result);
		goto FINALLY;
	}

	// create CbeffRecord from buffer
	result = CbeffRecordCreateFromMemoryNEx(hBuffer, patronFormat, 0, NULL, &hCbeffRecord);
	if (NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("CbeffRecordCreateFromMemoryN() failed, result = %d\n"), result);
		goto FINALLY;
	}

	// create subject for CbeffRecord
	result = NSubjectCreate(&hSubject);
	if (NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("NSubjectCreate() failed, result = %d\n"), result);
		goto FINALLY;
	}

	// set CbeffRecord on subject
	result = NSubjectSetTemplateCbeff(hSubject, hCbeffRecord);
	if (NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("NSubjectSetTemplateCbeff() failed, result = %d\n"), result);
		goto FINALLY;
	}

	// create BiometricEngine
	result = NBiometricEngineCreate(&hBiometricEngine);
	if (NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("NBiometricEngineCreate() failed, result = %d\n"), result);
		goto FINALLY;
	}

	// create extract template information
	result = NBiometricEngineCreateTemplate(hBiometricEngine, hSubject, &biometricStatus);
	if (NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("NBiometricEngineCreateTemplate() failed, result = %d\n"), result);
		goto FINALLY;
	}

	// check if template was sucessfully extracted
	if (biometricStatus != nbsOk)
	{
		// Retrieve biometric status
		result = NEnumToStringP(N_TYPE_OF(NBiometricStatus), biometricStatus, NULL, &hBiometricStatus);
		if (NFailed(result))
		{
			result = PrintErrorMsgWithLastError(N_T("NEnumToStringP() failed (result = %d)!"), result);
			goto FINALLY;
		}

		result = NStringGetBuffer(hBiometricStatus, NULL, &szBiometricStatus);
		if (NFailed(result))
		{
			result = PrintErrorMsgWithLastError(N_T("NStringGetBuffer() failed (result = %d)!"), result);
			goto FINALLY;
		}

		printf(N_T("template extraction failed!\n"));
		printf(N_T("biometric status = %s.\n\n"), szBiometricStatus);

		result = N_E_FAILED;
		goto FINALLY;
	}

	// get NTemplate buffer
	result = NSubjectGetTemplateBuffer(hSubject, &hNTemplateBuffer);
	if (NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("NSubjectGetTemplateBuffer() failed, result = %d\n"), result);
		goto FINALLY;
	}

	// save CbeffRecord buffer to file
	result = NFileWriteAllBytesCN(argv[3], hNTemplateBuffer);
	if (NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("NFileWriteAllBytesCN() failed, result = %d\n"), result);
		goto FINALLY;
	}

	result = N_OK;
FINALLY:
	{
		NResult result2 = NObjectSet(NULL, &hBuffer);
		if NFailed((result2)) PrintErrorMsg(N_T("NObjectSet() failed (result = %d)!"), result2);
		result2 = NObjectSet(NULL, &hNTemplateBuffer);
		if NFailed((result2)) PrintErrorMsg(N_T("NObjectSet() failed (result = %d)!"), result2);
		result2 = NObjectSet(NULL, &hCbeffRecord);
		if NFailed((result2)) PrintErrorMsg(N_T("NObjectSet() failed (result = %d)!"), result2);
		result2 = NObjectSet(NULL, &hSubject);
		if NFailed((result2)) PrintErrorMsg(N_T("NObjectSet() failed (result = %d)!"), result2);
		result2 = NObjectSet(NULL, &hBiometricEngine);
		if NFailed((result2)) PrintErrorMsg(N_T("NObjectSet() failed (result = %d)!"), result2);
		result2 = NStringSet(NULL, &hBiometricStatus);
		if NFailed((result2)) PrintErrorMsg(N_T("NStringSet() failed (result = %d)!"), result2);
		for (i = 0; i < obtainedLicensesCount; i++)
		{
			result2 = NLicenseReleaseComponents(obtainedLicenses[i]);
			if (NFailed(result2)) PrintErrorMsg(N_T("NLicenseReleaseComponents() failed, result = %d\n"), result2);
		}
	}

	OnExit();
	return result;
}
