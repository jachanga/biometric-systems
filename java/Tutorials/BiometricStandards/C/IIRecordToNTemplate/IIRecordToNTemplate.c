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

const NChar title[] = N_T("IIRecordToNTemplate");
const NChar description[] = N_T("Demonstrates creation of NTemplate from IIRecord");
const NChar version[] = N_T("9.0.0.0");
const NChar copyright[] = N_T("Copyright (C) 2008-2017 Neurotechnology");

int usage()
{
	printf(N_T("usage: %s [IIRecord] [NTemplate]\n"), title);
	printf(N_T("\tIIRecord  - input IIRecord\n"));
	printf(N_T("\tNTemplate - output NTemplate\n"));

	return 1;
}

int main(int argc, NChar *argv[])
{
	HIIRecord hIIRecord = NULL;
	HIirIrisImage hIirIrisImage = NULL;
	HNImage hImage = NULL;
	HNBuffer hBuffer = NULL;
	HNSubject hSubject = NULL;
	HNIris hIris = NULL;
	HNBiometricClient hBiometricClient = NULL;
	HNString hBiometricStatus = NULL;

	NBiometricStatus biometricStatus = nbsNone;
	const NChar * szBiometricStatus = NULL;
	NResult result = N_OK;
	NInt iris_image_count;
	NInt i;
	const NChar * components = N_T("Biometrics.IrisExtraction,Biometrics.Standards.Irises");
	NBool available = NFalse;

	OnStart(title, description, version, copyright, argc, argv);

	if (argc < 3)
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

	result = NFileReadAllBytesCN(argv[1], &hBuffer);
	if (NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("error in NFileReadAllBytesCN, error code: %d\n"), result);
		goto FINALLY;
	}

	result = IIRecordCreateFromMemoryN(hBuffer, 0, bsIso, NULL, &hIIRecord);
	if(NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("error in IIRecordCreateFromMemory, error code: %d\n"), result);
		goto FINALLY;
	}

	result = NObjectSet(NULL, &hBuffer);
	if(NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("error in NObjectSet, error code: %d\n"), result);
		goto FINALLY;
	}

	result = IIRecordGetIrisImageCount(hIIRecord, &iris_image_count);
	if(NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("error in IIRecordGetIrisImageCount, error code: %d\n"), result);
		goto FINALLY;
	}

	// create biometric client
	result = NBiometricClientCreate(&hBiometricClient);
	if (NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("NBiometricClientCreate() failed (result = %d)!"), result);
		goto FINALLY;
	}

	// create subject
	result = NSubjectCreate(&hSubject);
	if (NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("NSubjectCreate() failed (result = %d)!"), result);
		goto FINALLY;
	}

	for (i = 0; i < iris_image_count; i++)
	{
		result = IIRecordGetIrisImage(hIIRecord, i, &hIirIrisImage);
		if(NFailed(result))
		{
			result = PrintErrorMsgWithLastError(N_T("error in IIRecordGetIrisImage, error code: %d\n"), result);
			goto FINALLY;
		}

		result = IirIrisImageToNImage(hIirIrisImage, 0, &hImage);
		if(NFailed(result))
		{
			result = PrintErrorMsgWithLastError(N_T("error in IirIrisImageToNImage, error code: %d\n"), result);
			goto FINALLY;
		}

		// create iris for the subject
		result = NIrisCreate(&hIris);
		if (NFailed(result))
		{
			result = PrintErrorMsgWithLastError(N_T("NIrisCreate() failed (result = %d)!"), result);
			goto FINALLY;
		}

		result = NIrisSetImage(hIris, hImage);
		if(NFailed(result))
		{
			result = PrintErrorMsgWithLastError(N_T("error in NIrisSetImage, error code: %d\n"), result);
			goto FINALLY;
		}

		// set the iris for the subject
		result = NSubjectAddIris(hSubject, hIris, NULL);
		if (NFailed(result))
		{
			result = PrintErrorMsgWithLastError(N_T("NSubjectAddIris() failed (result = %d)!"), result);
			goto FINALLY;
		}

		result = NObjectSet(NULL, &hIirIrisImage);
		if(NFailed(result))
		{
			result = PrintErrorMsgWithLastError(N_T("error in NObjectSet, error code: %d\n"), result);
			goto FINALLY;
		}

		result = NObjectSet(NULL, &hImage);
		if(NFailed(result))
		{
			result = PrintErrorMsgWithLastError(N_T("error in NObjectSet, error code: %d\n"), result);
			goto FINALLY;
		}

		result = NObjectSet(NULL, &hIris);
		if(NFailed(result))
		{
			result = PrintErrorMsgWithLastError(N_T("error in NObjectSet, error code: %d\n"), result);
			goto FINALLY;
		}
	}

	// create the template
	result = NBiometricEngineCreateTemplate(hBiometricClient, hSubject, &biometricStatus);
	if (NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("NBiometricEngineCreateTemplate() failed (result = %d)!"), result);
		goto FINALLY;
	}

	if (biometricStatus != nbsOk)
	{
		// retrieve biometric status
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
		printf(N_T("biometric status: %s\n"), szBiometricStatus);

		result = N_E_FAILED;
		goto FINALLY;
	}

	// retrieve template
	result = NSubjectGetTemplateBuffer(hSubject, &hBuffer);
	if (NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("NSubjectGetTemplateBuffer() failed (result = %d)!"), result);
		goto FINALLY;
	}

	result = NFileWriteAllBytesCN(argv[2], hBuffer);
	if (NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("error in NFileWriteAllBytesCN, error code: %d\n"), result);
		goto FINALLY;
	}

	printf(N_T("NTemplate successfully saved to file %s\n"), argv[2]);

	result = N_OK;
FINALLY:
	{
		NResult result2 = NObjectSet(NULL, &hIIRecord);
		if (NFailed(result2)) PrintErrorMsg(N_T("NObjectSet() failed (result = %d)!"), result2);
		result2 = NObjectSet(NULL, &hIirIrisImage);
		if (NFailed(result2)) PrintErrorMsg(N_T("NObjectSet() failed (result = %d)!"), result2);
		result2 = NObjectSet(NULL, &hImage);
		if (NFailed(result2)) PrintErrorMsg(N_T("NObjectSet() failed (result = %d)!"), result2);
		result2 = NObjectSet(NULL, &hBuffer);
		if (NFailed(result2)) PrintErrorMsg(N_T("NObjectSet() failed (result = %d)!"), result2);
		result2 = NObjectSet(NULL, &hSubject);
		if (NFailed(result2)) PrintErrorMsg(N_T("NObjectSet() failed (result = %d)!"), result2);
		result2 = NObjectSet(NULL, &hIris);
		if (NFailed(result2)) PrintErrorMsg(N_T("NObjectSet() failed (result = %d)!"), result2);
		result2 = NObjectSet(NULL, &hBiometricClient);
		if (NFailed(result2)) PrintErrorMsg(N_T("NObjectSet() failed (result = %d)!"), result2);
		result2 = NStringSet(NULL, &hBiometricStatus);
		if (NFailed(result2)) PrintErrorMsg(N_T("NStringSet() failed (result = %d)!"), result2);
		result2 = NLicenseReleaseComponents(components);
		if (NFailed(result2)) PrintErrorMsg(N_T("NLicenseReleaseComponents() failed, result = %d\n"), result2);
	}

	OnExit();
	return result;
}
