#include <TutorialUtils.hpp>

#ifdef N_MAC_OSX_FRAMEWORKS
	#include <NCore/NCore.hpp>
	#include <NMedia/NMedia.hpp>
	#include <NLicensing/NLicensing.hpp>
#else
	#include <NCore.hpp>
	#include <NMedia.hpp>
	#include <NLicensing.hpp>
#endif

using namespace std;
using namespace Neurotec;
using namespace Neurotec::Licensing;
using namespace Neurotec::Media;
using namespace Neurotec::Video;
using namespace Neurotec::Images;

const NChar title[] = N_T("ReadVideo");
const NChar description[] = N_T("Demonstrates reading frames from specified filename or url");
const NChar version[] = N_T("9.0.0.0");
const NChar copyright[] = N_T("Copyright (C) 2016-2017 Neurotechnology");

int usage()
{
	cout << "usage:" << endl;
	cout << "\t" << title << "  [source] [frameCount] <optional: is url>" << endl << endl;
	cout << "\tsource - filename or url images should be captured from" << endl;
	cout << "\tframeCount - number of images to capture from specified filename or url" << endl;
	cout << "\tis url - specifies that passed source parameter is url (value: 1) or filename (value: 0)" << endl;
	return 1;
}

static void DumpMediaFormat(const NMediaFormat & mediaFormat)
{
	if (mediaFormat.IsNull()) NThrowException("mediaFormat");
	
	switch (mediaFormat.GetMediaType())
	{
	case nmtVideo:
		{
			NVideoFormat videoFormat = NObjectDynamicCast<NVideoFormat>(mediaFormat);
			cout << "video format .. " << videoFormat.GetWidth() << "x" << videoFormat.GetHeight() << " @ " << (videoFormat.GetFrameRate()).Numerator << "/"
				 << (videoFormat.GetFrameRate()).Denominator << " (interlace: " << videoFormat.GetInterlaceMode() << ", aspect ratio: "
				 << (videoFormat.GetPixelAspectRatio()).Numerator << "/" << (videoFormat.GetPixelAspectRatio()).Denominator << ")" << endl;
			break;
		}
	case nmtAudio:
		{
			NAudioFormat audioFormat = NObjectDynamicCast<NAudioFormat>(mediaFormat);
			cout << "audio format .. channels: " << audioFormat.GetChannelCount() << ", samples/second: " << audioFormat.GetSampleRate() << ", bits/channel: "
				 << audioFormat.GetBitsPerChannel() << endl;
			break;
		}
	default:
		{
			NThrowException("uknown media type specified in format!");
		}
	}
}

static void ReadFrames(NMediaReader & mediaReader, int frameCount)
{
	NMediaSource mediaSource = mediaReader.GetSource();
	cout << "Media length: " << mediaReader.GetLength().ToString() << endl;
	NArrayWrapper<NMediaFormat> formats = mediaSource.GetFormats(nmtVideo);
	int formatCount = formats.GetCount();
	cout << "format count: " << formatCount << endl;
	for (int i = 0; i < formatCount; i++)
	{
		cout << "[" << i << "]" << endl;
		DumpMediaFormat(formats.Get(i));
	}

	NMediaFormat currentMediaFormat = mediaSource.GetCurrentFormat(nmtVideo);
	if (!currentMediaFormat.IsNull())
	{
		cout << "Current media format: ";
		DumpMediaFormat(currentMediaFormat);
		cout << "set the last supported format (optional) ..." << endl;
		mediaSource.SetCurrentFormat(nmtVideo, formats.Get(formatCount - 1));
		cout << "Format setted" << endl;
	}
	else
		cout << "current media format is not yet available (will be availble after media reader start)" << endl;

	cout <<"Starting capture ..." << endl;
	mediaReader.Start();
	cout << "Capture started" << endl;
	currentMediaFormat = mediaSource.GetCurrentFormat(nmtVideo);
	if (currentMediaFormat.IsNull())
		NThrowException("current media format is not set even after media reader start!");
	cout << "Capturing with format: " << endl;
	DumpMediaFormat(currentMediaFormat);
	for (int i = 0; i < frameCount; i++)
	{
		NTimeSpan timespan(-1);
		NTimeSpan duration(0);
		NImage image = mediaReader.ReadVideoSample(&timespan, &duration);
		if (image.IsNull()) break;
		NString fileName = NString::Format("{I}.jpg", i);
		image.Save(fileName);
		cout << "[" << timespan.GetValue() << " " << duration.GetValue() << "] " << fileName << endl;
	}
	mediaReader.Stop();
}

int main(int argc, NChar **argv)
{
	const NChar * components = N_T("Media");
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

		NString uri = argv[1];
		bool isUrl = false;
		int framCount = atoi(argv[2]);
		if (framCount == 0)
			cout << "No frames will be captured as count is not specified" << endl;

		if (argc > 3)
			isUrl = atoi(argv[3]) == 1;

		NMediaSource mediaSource = NULL;
		if (isUrl)
			mediaSource = NMediaSource::FromUrl(uri);
		else
			mediaSource = NMediaSource::FromFile(uri);
		cout << "Display name: " << mediaSource.GetDisplayName() << endl;
		NMediaReader mediaReader(mediaSource, nmtVideo, true);
		ReadFrames(mediaReader, framCount);
		cout << "Done" << endl;
	}
	catch (NError& ex)
	{
		return LastError(ex);
	}

	NLicense::ReleaseComponents(components);
	OnExit();
	return 0;
}
