#include <TutorialUtils.hpp>

#ifdef N_MAC_OSX_FRAMEWORKS
	#include <NCore/NCore.hpp>
	#include <NBiometricClient/NBiometricClient.hpp>
	#include <NBiometrics/NBiometrics.hpp>
	#include <NMedia/NMedia.hpp>
	#include <NLicensing/NLicensing.hpp>
#else
	#include <NCore.hpp>
	#include <NBiometricClient.hpp>
	#include <NBiometrics.hpp>
	#include <NMedia.hpp>
	#include <NLicensing.hpp>
#endif

using namespace std;
using namespace Neurotec;
using namespace Neurotec::IO;
using namespace Neurotec::Images;
using namespace Neurotec::Licensing;
using namespace Neurotec::Biometrics;
using namespace Neurotec::Biometrics::Client;
using namespace Neurotec::Biometrics::Standards;

const NChar title[] = N_T("FIRecordToNTemplate");
const NChar description[] = N_T("Demonstrates creation of NTemplate from FIRecord");
const NChar version[] = N_T("9.0.0.0");
const NChar copyright[] = N_T("Copyright (C) 2016-2017 Neurotechnology");

int usage()
{
	cout << "usage:" << endl;
	cout << "\t" << title << " [FIRecord] [NTemplate]" << endl << endl;
	cout << "\tFIRecord  - input FIRecord" << endl;
	cout << "\tNTemplate - output NTemplate" << endl;
	return 1;
}

int main(int argc, NChar *argv[])
{
	const NChar * components = N_T("Biometrics.FingerExtraction,Biometrics.Standards.Fingers");
	OnStart(title, description, version, copyright, argc, argv);

	if (argc < 3)
	{
		OnExit();
		return usage();
	}

	try
	{
		if (!NLicense::ObtainComponents(N_T("/local"), N_T("5000"), components))
		{
			NThrowException(NString::Format(N_T("Could not obtain licenses for components: {S}"), components));
		}
		
		NBiometricClient biometricClient;
		NSubject subject;
		NBuffer fiRecordData = NFile::ReadAllBytes(argv[1]);
		FIRecord fiRec(fiRecordData, bsIso);
		for (int i = 0; i < fiRec.GetFingerViews().GetCount(); i++)
		{
			FirFingerView fv = fiRec.GetFingerViews().Get(i);
			NFinger finger;
			finger.SetImage(fv.ToNImage());
			subject.GetFingers().Add(finger);
		}
		biometricClient.SetFingersTemplateSize(ntsLarge);
		NBiometricStatus status = biometricClient.CreateTemplate(subject);
		if (status == nbsOk)
		{
			cout << "Template extracted" << endl;
			NFile::WriteAllBytes(argv[2], subject.GetTemplateBuffer());
			cout << "NTemplate saved to file " << argv[2];
		}
		else
			cout << "Extraction failed. Status: " << NEnum::ToString(NBiometricTypes::NBiometricStatusNativeTypeOf(), status) << endl;
	}
	catch (NError& ex)
	{
		return LastError(ex);
	}

	NLicense::ReleaseComponents(components);
	OnExit();
	return 0;
}
