package com.neurotec.tutorials.biometrics;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;

import com.neurotec.biometrics.NBiometricOperation;
import com.neurotec.biometrics.NBiometricStatus;
import com.neurotec.biometrics.NBiometricTask;
import com.neurotec.biometrics.NFace;
import com.neurotec.biometrics.NLAttributes;
import com.neurotec.biometrics.NSubject;
import com.neurotec.biometrics.NTemplateSize;
import com.neurotec.biometrics.client.NBiometricClient;
import com.neurotec.io.NFile;
import com.neurotec.samples.app.BaseActivity;
import com.neurotec.samples.app.DirectoryViewer;
import com.neurotec.samples.licensing.LicensingManager;
import com.neurotec.samples.licensing.LicensingManager.LicensingStateCallback;
import com.neurotec.samples.licensing.LicensingStateResult;
import com.neurotec.samples.util.NImageUtils;

public final class EnrollFaceFromImage extends BaseActivity implements LicensingStateCallback {

	private static final String TAG = EnrollFaceFromImage.class.getSimpleName();
	private static final int REQUEST_CODE_GET_IMAGE = 1;

		private static final String[] LICENSES = {
		LicensingManager.LICENSE_FACE_EXTRACTION,
		LicensingManager.LICENSE_FACE_SEGMENTS_DETECTION
	};

	private Button mButton;
	private TextView mResult;
	private ProgressDialog mProgressDialog;

	@Override
	public void onLicensingStateChanged(final LicensingStateResult state) {
		final Context context = this;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				switch (state.getState()) {
				case OBTAINING:
					Log.i(TAG, getString(R.string.format_obtaining_licenses, Arrays.toString(LICENSES)));
					mProgressDialog = ProgressDialog.show(context, "", getString(R.string.msg_obtaining_licenses));
					break;
				case OBTAINED:
					mProgressDialog.dismiss();
					showMessage(getString(R.string.msg_licenses_obtained));
					break;
				case NOT_OBTAINED:
					mProgressDialog.dismiss();
					showMessage(getString(R.string.msg_licenses_not_obtained));
					if (state.getException() != null) {
						showMessage(state.getException().getMessage());
					}
					break;
				default:
					throw new AssertionError("Unknown state: " + state);
				}
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tutorial_enroll_face_from_image);
		mButton = (Button) findViewById(R.id.tutorials_button_1);
		mButton.setText(R.string.msg_select_image);
		mButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getImage();
			}
		});
		mResult = (TextView) findViewById(R.id.tutorials_results);
		LicensingManager.getInstance().obtain(this, this, Arrays.asList(LICENSES));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			LicensingManager.getInstance().release(Arrays.asList(LICENSES));
		} catch (IOException e) {
			Log.e(TAG, getString(R.string.msg_licenses_not_obtained), e);
		}
		if ((mProgressDialog != null) && (mProgressDialog.isShowing())) {
			mProgressDialog.dismiss();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_GET_IMAGE) {
			if (resultCode == RESULT_OK) {
				try {
					enroll(data.getData());
				} catch (Exception e) {
					showMessage(e.getMessage());
					Log.e(TAG, "Exception", e);
				}
			}
		}
	}

	private void getImage() {
		Intent intent = new Intent(this, DirectoryViewer.class);
		intent.putExtra(DirectoryViewer.ASSET_DIRECTORY_LOCATION, BiometricsTutorialsApp.TUTORIALS_ASSETS_DIR);
		startActivityForResult(intent, REQUEST_CODE_GET_IMAGE);
	}

	private void showMessage(final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mResult.append(message + "\n");
			}
		});
	}

	private void enroll(Uri imageUri) throws IOException {
		if (!LicensingManager.isFaceExtractionActivated()) {
			showMessage(getString(R.string.format_not_activated, LicensingManager.LICENSE_FACE_EXTRACTION));
			return; // The following operation is not activated, so return
		}

		NBiometricClient biometricClient = null;
		NSubject subject = null;
		NFace face = null;
		NBiometricTask task = null;
		NBiometricStatus status = null;

		try {
			biometricClient = new NBiometricClient();
			subject = new NSubject();
			face = new NFace();

			//Set face image
			face.setImage(NImageUtils.fromUri(this, imageUri));

			subject.getFaces().add(face);

			//Set face template size (recommended, for enroll to database, is large) (optional)
			biometricClient.setFacesTemplateSize(NTemplateSize.MEDIUM);

			//Detect all faces features
			boolean isSegmentationActivated = LicensingManager.isActivated(LicensingManager.LICENSE_FACE_SEGMENTS_DETECTION);
			biometricClient.setFacesDetectAllFeaturePoints(isSegmentationActivated);

			task = biometricClient.createTask(EnumSet.of(NBiometricOperation.CREATE_TEMPLATE), subject);
			biometricClient.performTask(task);
			status = task.getStatus();
			if (task.getError() != null) {
				showError(task.getError());
				return;
			}

			if (subject.getFaces().size() > 1)
			showMessage(String.format("Found %d faces\n", subject.getFaces().size() - 1));

			//List attributes for all located faces
			for (NFace nface : subject.getFaces()) {
				for (NLAttributes attribute : nface.getObjects()) {
					Rect rect = attribute.getBoundingRect();
					showMessage(getString(R.string.msg_face_found));
					showMessage(getString(R.string.format_face_rect, rect.left, rect.top, rect.right, rect.bottom));

					if ((attribute.getRightEyeCenter().confidence > 0) || (attribute.getLeftEyeCenter().confidence > 0)) {
						showMessage(getString(R.string.msg_eyes_found));
						if (attribute.getRightEyeCenter().confidence > 0) {
							showMessage(getString(R.string.format_first_eye_location_confidence,
									attribute.getRightEyeCenter().x, attribute.getRightEyeCenter().y, attribute.getRightEyeCenter().confidence));
						}
						if (attribute.getLeftEyeCenter().confidence > 0) {
							showMessage(getString(R.string.format_second_eye_location_confidence,
									attribute.getLeftEyeCenter().x, attribute.getLeftEyeCenter().y, attribute.getLeftEyeCenter().confidence));
						}
					}
					if (isSegmentationActivated) {
						if (attribute.getNoseTip().confidence > 0) {
							showMessage(getString(R.string.msg_nose_found));
							showMessage(getString(R.string.format_location_confidence,
									attribute.getNoseTip().x, attribute.getNoseTip().y, attribute.getNoseTip().confidence));
						}
						if (attribute.getMouthCenter().confidence > 0) {
							showMessage(getString(R.string.msg_mouth_found));
							showMessage(getString(R.string.format_location_confidence,
									attribute.getMouthCenter().x, attribute.getMouthCenter().y, attribute.getMouthCenter().confidence));
						}
					}
				}
			}

			if (status == NBiometricStatus.OK) {
				// Save template to file.
				File outputFile = new File(BiometricsTutorialsApp.TUTORIALS_OUTPUT_DATA_DIR, "nltemplate-from-image.dat");
				NFile.writeAllBytes(outputFile.getAbsolutePath(), subject.getTemplate().save());
				showMessage(getString(R.string.format_face_template_saved_to, outputFile.getAbsolutePath()));
			} else {
				showMessage(getString(R.string.format_extraction_failed, status.toString()));
			}
		} finally {
			if (biometricClient != null) biometricClient.dispose();
			if (subject != null) subject.dispose();
			if (face != null) face.dispose();
		}
	}
}
