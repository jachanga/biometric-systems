#include <TutorialUtils.h>

#ifdef N_MAC_OSX_FRAMEWORKS
	#include <NCore/NCore.h>
	#include <NMedia/NMedia.h>
	#include <NDevices/NDevices.h>
	#include <NLicensing/NLicensing.h>
#else
	#include <NCore.h>
	#include <NMedia.h>
	#include <NDevices.h>
	#include <NLicensing.h>
#endif

const NChar title[] = N_T("ImageCapture");
const NChar description[] = N_T("Demonstrates capturing images from cameras");
const NChar version[] = N_T("9.0.0.0");
const NChar copyright[] = N_T("Copyright (C) 2008-2017 Neurotechnology");

int usage()
{
	printf(N_T("usage:\n"));
	printf(N_T("\t%s [frameCount]\n"), title);
	printf(N_T("\n"));
	printf(N_T("\tframeCount - number of frames to capture from each camera to current directory\n"));
	printf(N_T("\n"));
	return 1;
}

int main(int argc, NChar **argv)
{
	HNDeviceManager hDeviceManager = NULL;
	HNDevice hDevice = NULL;
	HNImage hImage = NULL;
	HNString hDisplayName = NULL;
	NResult result;
	NInt cameraCount, frameCount = 0;
	NInt i;
	const NChar * components = N_T("Devices.Cameras");
	NBool available = NFalse;

	OnStart(title, description, version, copyright, argc, argv);

	if (argc < 2)
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

	frameCount = atoi(argv[1]);
	if (frameCount == 0)
	{
		printf(N_T("no frames will be captured as frame count is not specified"));
	}

	printf(N_T("creating device manager ...\n"));
	result = NDeviceManagerCreateEx(&hDeviceManager);
	if (NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("failed to create device manager failed (result = %d)\n"), result);
		goto FINALLY;
	}
	result = NDeviceManagerSetDeviceTypes(hDeviceManager, ndtCamera);
	if (NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("failed to set device types (result = %d)!\n"), result);
		goto FINALLY;
	}
	result = NDeviceManagerSetAutoPlug(hDeviceManager, NTrue);
	if (NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("failed to set auto plug (result = %d)!\n"), result);
		goto FINALLY;
	}

	result = NDeviceManagerInitialize(hDeviceManager);
	if (NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("failed to initialize device manager (result = %d)!\n"), result);
		goto FINALLY;
	}
	printf(N_T("done\n"));

	result = NDeviceManagerGetDeviceCount(hDeviceManager, &cameraCount);
	if (NFailed(result))
	{
		result = PrintErrorMsgWithLastError(N_T("failed to get number of cameras (result = %d)!\n"), result);
		goto FINALLY;
	}

	/** enumerate cameras */
	for (i = 0; i < cameraCount; i++)
	{
		NInt j;
		const NChar * szDisplayName;

		result = NDeviceManagerGetDevice(hDeviceManager, i, &hDevice);
		if (NFailed(result))
		{
			result = PrintErrorMsgWithLastError(N_T("failed to get device (result = %d)!\n"), result);
			goto FINALLY;
		}

		result = NDeviceGetDisplayNameN(hDevice, &hDisplayName);
		if (NFailed(result))
		{
			result = PrintErrorMsgWithLastError(N_T("failed to get device name (result = %d)!\n"), result);
			goto FINALLY;
		}
		result = NStringGetBuffer(hDisplayName, NULL, &szDisplayName);
		if (NFailed(result))
		{
			result = PrintErrorMsgWithLastError(N_T("failed to get device name (result = %d)!\n"), result);
			goto FINALLY;
		}
		printf(N_T("found camera: %s"), szDisplayName);

		result = NCaptureDeviceStartCapturing(hDevice);
		if (NFailed(result))
		{
			result = PrintErrorMsgWithLastError(N_T("\nfailed to start camera (result = %d)!\n"), result);
			goto FINALLY;
		}

		printf(N_T(", capturing "));
		for (j = 0; j < frameCount; j++)
		{
			NChar szFilename[1024];

			sprintf(szFilename, N_T("%s_%.4d.jpg"), szDisplayName, j);

			/** get frame from camera and save it to file */
			result = NCameraGetFrame(hDevice, &hImage);
			if (NFailed(result))
			{
				result = PrintErrorMsgWithLastError(N_T("failed to get current frame from camera (result = %d)!\n"), result);
				goto FINALLY;
			}

			result = NImageSaveToFileEx(hImage, szFilename, NULL, NULL, 0);
			if (NFailed(result))
			{
				result = PrintErrorMsgWithLastError(N_T("failed to save image to file (result = %d)!\n"), result);
				goto FINALLY;
			}

			printf(N_T("."));

			result = NObjectSet(NULL, &hImage);
			if (NFailed(result))
			{
				result = PrintErrorMsgWithLastError(N_T("failed to clear image (result = %d)!\n"), result);
				goto FINALLY;
			}
		}

		result = NCaptureDeviceStopCapturing(hDevice);
		if (NFailed(result))
		{
			result = PrintErrorMsgWithLastError(N_T("failed to stop camera (result = %d)!\n"), result);
			goto FINALLY;
		}

		result = NStringSet(NULL, &hDisplayName);
		if (NFailed(result))
		{
			result = PrintErrorMsgWithLastError(N_T("failed to clear display name (result = %d)!\n"), result);
			goto FINALLY;
		}

		result = NObjectSet(NULL, &hDevice);
		if (NFailed(result))
		{
			result = PrintErrorMsgWithLastError(N_T("failed to clear device (result = %d)!\n"), result);
			goto FINALLY;
		}

		printf(N_T(" done\n"));
	}
	printf(N_T("done\n"));

	result = N_OK;
FINALLY:
	{
		NResult result2 = NObjectSet(NULL, &hDevice);
		if (NFailed(result2)) PrintErrorMsg(N_T("NObjectSet() failed (result = %d)!"), result2);
		result2 = NObjectSet(NULL, &hDeviceManager);
		if (NFailed(result2)) PrintErrorMsg(N_T("NObjectSet() failed (result = %d)!"), result2);
		result2 = NObjectSet(NULL, &hImage);
		if (NFailed(result2)) PrintErrorMsg(N_T("NObjectSet() failed (result = %d)!"), result2);
		result2 = NStringSet(NULL, &hDisplayName);
		if (NFailed(result2)) PrintErrorMsg(N_T("NStringSet() failed (result = %d)!"), result2);

		result2 = NLicenseReleaseComponents(components);
		if (NFailed(result2)) PrintErrorMsg(N_T("NLicenseReleaseComponents() failed, result = %d\n"), result2);
	}

	OnExit();

	return result;
}
