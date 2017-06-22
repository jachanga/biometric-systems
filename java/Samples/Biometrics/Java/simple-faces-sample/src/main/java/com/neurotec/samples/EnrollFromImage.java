package com.neurotec.samples;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.neurotec.biometrics.NBiometricStatus;
import com.neurotec.biometrics.NFace;
import com.neurotec.biometrics.NSubject;
import com.neurotec.biometrics.client.NBiometricClient;
import com.neurotec.biometrics.swing.NFaceView;
import com.neurotec.images.NImage;
import com.neurotec.io.NFile;
import com.neurotec.licensing.NLicense;
import com.neurotec.samples.swing.ImageThumbnailFileChooser;
import com.neurotec.samples.util.Utils;
import com.neurotec.swing.NViewZoomSlider;
import com.neurotec.util.concurrent.CompletionHandler;

public final class EnrollFromImage extends BasePanel implements ActionListener {

	// ===========================================================
	// Private static fields
	// ===========================================================

	private static final long serialVersionUID = 1L;

	private static final String PANEL_TITLE = "Enroll from image";
	private static final String LOAD_IMAGE_BUTTON_TEXT = "Load image";
	private static final String SAVE_TEMPLATE_BUTTON_TEXT = "Save template";
	private static final String EXTRACT_TEMPLATE = "Extract template";
	private static final String MAX_ROLL_ANGLE_DEVIATION_LABEL_TEXT = "Roll angle deviation";
	private static final String MAX_YAW_ANGLE_DEVIATION_LABEL_TEXT = "Max yaw angle deviation";
	private static final String TEMPLATE_CREATION_SUCCEEDED_LABEL_TEXT = "Template extracted";
	private static final String TEMPLATE_CREATION_FAILED_LABEL_TEXT = "Extraction failed";

	private static final Color TEMPLATE_CREATION_SUCCEEDED_LABEL_TEXT_COLOR = Color.green.darker();
	private static final Color TEMPLATE_CREATION_FAILED_LABEL_TEXT_COLOR = Color.red.darker();

	private static final int BORDER_WIDTH_TOP = 5;
	private static final int BORDER_WIDTH_LEFT = 5;
	private static final int BORDER_WIDTH_BOTTOM = 5;
	private static final int BORDER_WIDTH_RIGHT = 5;

	// ===========================================================
	// Private fields
	// ===========================================================Z

	private final ImageThumbnailFileChooser fc;
	private JPanel panelStatusBar;
	private JPanel panelSouth;
	private JPanel panelControls;
	private JPanel panelToolbar;
	private JLabel lblTemplateCreationStatus;
	private JLabel lblMaxRollAngleDeviation;
	private JLabel lblMaxYawAngleDeviation;
	private JButton btnLoadImage;
	private JButton btnExtractTemplate;
	private JButton btnSaveTemplate;
	private JComboBox comboBoxMaxRollAngleDeviation;
	private JComboBox comboBoxMaxYawAngleDeviation;
	private NFaceView view;
	private NViewZoomSlider zoomSlider;
	private JScrollPane scrollPane;

	private final TemplateCreationHandler templateCreationHandler = new TemplateCreationHandler();

	private NSubject subject;

	// ===========================================================
	// Public constructor
	// ===========================================================

	public EnrollFromImage() {
		super();
		setName(PANEL_TITLE);

		requiredLicenses.add("Biometrics.FaceExtraction");
		optionalLicenses.add("Biometrics.FaceSegmentsDetection");

		fc = new ImageThumbnailFileChooser();
		fc.setIcon(Utils.createIconImage("images/Logo16x16.png"));

	}

	// ===========================================================
	// Private methods
	// ===========================================================

	private void updateComboBoxes() {
		updateRollAngleDeviationComboBox();
		updateYawAngleDeviationComboBox();
	}

	private void updateRollAngleDeviationComboBox() {
		DefaultComboBoxModel model = (DefaultComboBoxModel) comboBoxMaxRollAngleDeviation.getModel();
		Float item = FaceTools.getInstance().getClient().getFacesMaximalRoll();
		updateComboBoxValues(model, item, 0, 180);
	}

	private void updateYawAngleDeviationComboBox() {
		DefaultComboBoxModel model = (DefaultComboBoxModel) comboBoxMaxYawAngleDeviation.getModel();
		Float item = FaceTools.getInstance().getClient().getFacesMaximalYaw();
		updateComboBoxValues(model, item, 0, 90);
	}

	private void updateComboBoxValues(DefaultComboBoxModel model, Float item, int min, int max) {
		List<Float> items = new ArrayList<Float>();
		for (float i = min; i <= max; i += 15) {
			items.add((i));
		}

		if (!items.contains(item)) {
			items.add(item);
		}

		Collections.sort(items);
		for (int i = 0; i != items.size(); i++) {
			model.addElement(items.get(i));
		}
		model.setSelectedItem(item);
	}

	private void updateTemplateCreationStatus(boolean created) {
		if (created) {
			lblTemplateCreationStatus.setText(TEMPLATE_CREATION_SUCCEEDED_LABEL_TEXT);
			lblTemplateCreationStatus.setForeground(TEMPLATE_CREATION_SUCCEEDED_LABEL_TEXT_COLOR);
			btnSaveTemplate.setEnabled(true);
		} else {
			lblTemplateCreationStatus.setText(TEMPLATE_CREATION_FAILED_LABEL_TEXT);
			lblTemplateCreationStatus.setForeground(TEMPLATE_CREATION_FAILED_LABEL_TEXT_COLOR);
			btnSaveTemplate.setEnabled(false);
		}
	}

	private void openFile() throws IOException {
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			NImage image = NImage.fromFile(fc.getSelectedFile().getAbsolutePath());
			subject = new NSubject();
			NFace face = new NFace();
			face.setImage(image);
			subject.getFaces().add(face);
			view.setFace(face);
			createTemplate();
		}
	}

	private void saveTemplate() throws IOException {
		if (subject != null) {
			fc.setSelectedFile(new File("subject"));
			if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				NFile.writeAllBytes(fc.getSelectedFile().getAbsolutePath(), subject.getTemplateBuffer());
			}
		}
	}

	private void createTemplate() {
		if (subject != null) {
			NBiometricClient client = FaceTools.getInstance().getClient();
			updateFacesTools();
			client.createTemplate(subject, null, templateCreationHandler);
		}
	}

	// ===========================================================
	// Protected methods
	// ===========================================================

	@Override
	protected void initGUI() {
		setLayout(new BorderLayout());
		{
			panelToolbar = new JPanel();
			panelToolbar.setLayout(new BoxLayout(panelToolbar, BoxLayout.Y_AXIS));
			add(panelToolbar, BorderLayout.PAGE_START);
			{
				licensing = new LicensingPanel(requiredLicenses, Collections.<String>emptyList());
				panelToolbar.add(licensing);
			}
			{
				panelControls = new JPanel();
				panelControls.setLayout(new FlowLayout(FlowLayout.LEFT));
				panelControls.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				panelControls.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
				panelToolbar.add(panelControls);
				{
					btnLoadImage = new JButton(LOAD_IMAGE_BUTTON_TEXT);
					btnLoadImage.addActionListener(this);
					panelControls.add(btnLoadImage);
				}
				{
					lblMaxRollAngleDeviation = new JLabel(MAX_ROLL_ANGLE_DEVIATION_LABEL_TEXT);
					panelControls.add(lblMaxRollAngleDeviation);
				}
				{
					comboBoxMaxRollAngleDeviation = new JComboBox();
					panelControls.add(comboBoxMaxRollAngleDeviation);
				}
				{
					lblMaxYawAngleDeviation = new JLabel(MAX_YAW_ANGLE_DEVIATION_LABEL_TEXT);
					panelControls.add(lblMaxYawAngleDeviation);
				}
				{
					comboBoxMaxYawAngleDeviation = new JComboBox();
					panelControls.add(comboBoxMaxYawAngleDeviation);
				}
				{
					btnExtractTemplate = new JButton(EXTRACT_TEMPLATE);
					btnExtractTemplate.addActionListener(this);
					panelControls.add(btnExtractTemplate);
				}
			}
		}
		{
			scrollPane = new JScrollPane();
			add(scrollPane, BorderLayout.CENTER);
			view = new NFaceView();
			view.setAutofit(true);
			scrollPane.setViewportView(view);
		}
		{
			panelSouth = new JPanel();
			panelSouth.setLayout(new BorderLayout());
			add(panelSouth, BorderLayout.PAGE_END);
			{
				panelStatusBar = new JPanel();
				panelStatusBar.setLayout(new FlowLayout(FlowLayout.LEFT));
				panelSouth.add(panelStatusBar, BorderLayout.WEST);
				{
					btnSaveTemplate = new JButton(SAVE_TEMPLATE_BUTTON_TEXT);
					btnSaveTemplate.setBorder(BorderFactory.createEmptyBorder(BORDER_WIDTH_TOP, BORDER_WIDTH_LEFT, BORDER_WIDTH_BOTTOM, BORDER_WIDTH_RIGHT));
					btnSaveTemplate.addActionListener(this);
					btnSaveTemplate.setSize(new Dimension(150, 20));
					btnSaveTemplate.setEnabled(false);
					panelStatusBar.add(btnSaveTemplate);
				}
				{
					lblTemplateCreationStatus = new JLabel("");
					lblTemplateCreationStatus.setSize(new Dimension(150, 20));
					lblTemplateCreationStatus.setBorder(BorderFactory.createEmptyBorder(BORDER_WIDTH_TOP, BORDER_WIDTH_LEFT, BORDER_WIDTH_BOTTOM, BORDER_WIDTH_RIGHT));
					panelStatusBar.add(lblTemplateCreationStatus);
				}
			}
			{
				zoomSlider = new NViewZoomSlider();
				zoomSlider.setView(view);
				panelSouth.add(zoomSlider, BorderLayout.EAST);
			}
		}

		updateComboBoxes();
	}

	@Override
	protected void setDefaultValues() {
		Float defaultMaxRoll = FaceTools.getInstance().getDefaultClient().getFacesMaximalRoll();
		comboBoxMaxRollAngleDeviation.setSelectedItem(defaultMaxRoll);
		if (!defaultMaxRoll.equals(comboBoxMaxRollAngleDeviation.getSelectedItem())) {
			comboBoxMaxRollAngleDeviation.addItem(defaultMaxRoll);
			comboBoxMaxRollAngleDeviation.setSelectedItem(defaultMaxRoll);
		}
		Float defaultMaxYaw = FaceTools.getInstance().getDefaultClient().getFacesMaximalYaw();
		comboBoxMaxYawAngleDeviation.setSelectedItem(defaultMaxYaw);
		if (!defaultMaxYaw.equals(comboBoxMaxYawAngleDeviation.getSelectedItem())) {
			comboBoxMaxYawAngleDeviation.addItem(defaultMaxYaw);
			comboBoxMaxYawAngleDeviation.setSelectedItem(defaultMaxYaw);
		}
	}

	@Override
	protected void updateControls() {
		// No controls to update.
	}

	@Override
	protected void updateFacesTools() {
		NBiometricClient client = FaceTools.getInstance().getClient();
		client.reset();
		client.setFacesMaximalRoll((Float) comboBoxMaxRollAngleDeviation.getSelectedItem());
		client.setFacesMaximalYaw((Float) comboBoxMaxYawAngleDeviation.getSelectedItem());
		boolean faceSegmentsDetectionActivated;
		try {
			faceSegmentsDetectionActivated = NLicense.isComponentActivated("Biometrics.FaceSegmentsDetection");
		} catch (IOException e) {
			e.printStackTrace();
			faceSegmentsDetectionActivated = false;
		}
		client.setFacesDetectAllFeaturePoints(faceSegmentsDetectionActivated);
		client.setFacesDetectBaseFeaturePoints(faceSegmentsDetectionActivated);
		client.setFacesDetermineGender(faceSegmentsDetectionActivated);
		client.setFacesDetermineAge(faceSegmentsDetectionActivated);
		client.setFacesDetectProperties(faceSegmentsDetectionActivated);
		client.setFacesRecognizeExpression(faceSegmentsDetectionActivated);
	}

	// ===========================================================
	// Public methods
	// ===========================================================

	@Override
	public void actionPerformed(ActionEvent ev) {
		try {
			if (ev.getSource().equals(btnLoadImage)) {
				openFile();
			} else if (ev.getSource().equals(btnExtractTemplate)) {
				createTemplate();
			} else if (ev.getSource().equals(btnSaveTemplate)) {
				saveTemplate();
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void onDestroy() {
	}

	@Override
	public void onClose() {
	}

	// ===========================================================
	// Inner classes
	// ===========================================================

	private class TemplateCreationHandler implements CompletionHandler<NBiometricStatus, Object> {

		@Override
		public void completed(final NBiometricStatus result, final Object attachment) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					if (result == NBiometricStatus.OK) {
						updateTemplateCreationStatus(true);
					} else if (result == NBiometricStatus.BAD_OBJECT) {
						JOptionPane.showMessageDialog(EnrollFromImage.this, "Face image quality is too low.");
						updateTemplateCreationStatus(false);
					} else {
						JOptionPane.showMessageDialog(EnrollFromImage.this, result);
						updateTemplateCreationStatus(false);
					}
				}

			});
		}

		@Override
		public void failed(final Throwable th, final Object attachment) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					showError(th);
					updateTemplateCreationStatus(false);
				}

			});
		}

	}

}
